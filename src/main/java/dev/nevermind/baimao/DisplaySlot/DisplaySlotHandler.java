package dev.nevermind.baimao.DisplaySlot;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.nevermind.baimao.Main;
import dev.nevermind.baimao.MongoDB.MongodbProfile;
import dev.nevermind.baimao.Utils.MongoUtils;
import dev.nevermind.baimao.listener.MatchListener;
import gg.noob.practice.match.Match;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DisplaySlotHandler {

    @Getter private final Set<DisplaySlotEntry> displaySlotEntry = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DisplaySlotHandler() {
        load();
        Bukkit.getPluginManager().registerEvents(new MatchListener(), Main.getInstance());
    }

    void load() {
        FindIterable<Document> documentMongoCollection = MongoUtils.getCollection("displayslot").find();

        for(Document document : documentMongoCollection) {
            displaySlotEntry.add(new DisplaySlotEntry(document.getString("name"),
                    document.getString("title"),
                    document.getInteger("level"),
                    document.getString("permission")));
        }
    }

    public void save(DisplaySlotEntry displaySlotEntry) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document();
            document.put("name", displaySlotEntry.getName());
            document.put("title", displaySlotEntry.getTitle());
            document.put("level", displaySlotEntry.getLevel());
            document.put("permission", displaySlotEntry.getPermission());

            Bson filter = Filters.eq("name", displaySlotEntry.getName());
            MongoUtils.getCollection("displayslot").replaceOne(filter, document, new ReplaceOptions().upsert(true));
        });
    }

    public void remove(String name) {
        CompletableFuture.runAsync(() -> {
            DisplaySlotEntry displaySlotEntry1 = getDisplaySlot(name);

            if(displaySlotEntry1 == null) return;

            Bson filter = Filters.eq("name", displaySlotEntry1.getName());

            displaySlotEntry.remove(displaySlotEntry1);

            MongoUtils.getCollection("displayslot").deleteOne(filter);
        });
    }

    public void setup(Match match) {
        Map<UUID, DisplaySlotEntry> displaySlotEntryMap = new HashMap<>();
        for(UUID uuid : match.getAllMembers()) {

            MongodbProfile profile = Main.getInstance().getMongodbManager().getProfile(uuid);

            for(DisplaySlotEntry displaySlotEntry : displaySlotEntry) {

                if(displaySlotEntry.getName().equals(profile.getUsed())) {
                    displaySlotEntryMap.put(uuid, displaySlotEntry);
                }
            }
        }

        Player player = getMaxLevel(displaySlotEntryMap);

        if(player == null) return;

        Collection<Player> playerCollection = getPlayerSameOwnership(player, displaySlotEntryMap);

        for(UUID uuid : match.getAllMembers()) {
            sendPacket(player, Bukkit.getPlayer(uuid), displaySlotEntryMap.get(player.getUniqueId()).getTitle(), playerCollection);
        }
    }

    public Player getMaxLevel(Map<UUID, DisplaySlotEntry> displaySlotEntryMap) {
        Map<UUID, Integer> uuidsLevel = new HashMap<>();

        for(Map.Entry<UUID, DisplaySlotEntry> entry : displaySlotEntryMap.entrySet()) {
            uuidsLevel.put(entry.getKey(), entry.getValue().getLevel());
        }

        List<Map.Entry<UUID, Integer>> list = new ArrayList<>(uuidsLevel.entrySet());

        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        if(list.isEmpty()) {
            return null;
        }

        return Bukkit.getPlayer(list.get(0).getKey());
    }

    public Collection<Player> getPlayerSameOwnership(Player player, Map<UUID, DisplaySlotEntry> displaySlotEntryMap) {
        Collection<Player> collection = new ArrayList<>();

        for(Map.Entry<UUID, DisplaySlotEntry> entry : displaySlotEntryMap.entrySet()) {

            if(displaySlotEntryMap.get(player.getUniqueId()) == entry.getValue()) collection.add(Bukkit.getPlayer(entry.getKey()));
        }

        return collection;
    }

    public void sendPacket(Player owner, Player player, String displayName, int i) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);

        if (objective == null) {
            objective = scoreboard.registerNewObjective("showdiplayslot", "diplayslot");
        } else {
            return;
        }

        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName(displayName);
        objective.getScore(owner.getName()).setScore(i);
    }

    public void sendPacket(Player owner, Player player, String displayName, Collection<Player> players) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);

        if (objective == null) {
            objective = scoreboard.registerNewObjective("showdiplayslot", "diplayslot");
        } else {
            return;
        }

        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        if(!players.contains(player)) {
            objective.getScore(player.getName()).setScore(0);
        }
        for(Player player1 : players) {
            objective.getScore(player1.getName()).setScore(1);
        }

    }

    /*public void clear(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("", "dummy");

        ScoreboardObjective scoreboardObjective = ((CraftScoreboard) scoreboard).getHandle().getObjective(objective.getName());

        PacketPlayOutScoreboardObjective createPacket = new PacketPlayOutScoreboardObjective(scoreboardObjective, 1);
        PacketPlayOutScoreboardObjective displayPacket = new PacketPlayOutScoreboardObjective(scoreboardObjective, 2);
        PacketPlayOutScoreboardDisplayObjective belowNamePacket = new PacketPlayOutScoreboardDisplayObjective(2, scoreboardObjective);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(createPacket);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(displayPacket);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(belowNamePacket);
    }*/

    public void clearAll(Player player) {
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
        if (objective != null) {
            objective.unregister();
        }
        player.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
    }

    public DisplaySlotEntry getDisplaySlot(String name) {
        for(DisplaySlotEntry displaySlotEntry1 : displaySlotEntry) {
            if(displaySlotEntry1.getName().equals(name)) {
                return displaySlotEntry1;
            }
        }
        return null;
    }
}
