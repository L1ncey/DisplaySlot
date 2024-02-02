package dev.nevermind.baimao.MongoDB;

import dev.nevermind.baimao.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MongodbManager implements Listener {
    public MongodbManager() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Getter
    private final Map<UUID, MongodbProfile> profiles = new ConcurrentHashMap<>();

    public MongodbProfile getProfile(UUID uniqueId) {
        for (MongodbProfile profile : profiles.values()) {
            if (profile.getUniqueId() == uniqueId)
                return profile;
        }
        return new MongodbProfile(uniqueId);
    }

    public MongodbProfile getProfile(Player player) {
        for (MongodbProfile profile : profiles.values()) {
            if (profile.getUniqueId() == player.getUniqueId())
                return profile;
        }
        return new MongodbProfile(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        MongodbProfile profile = new MongodbProfile(event.getUniqueId());

        Main.getInstance().getMongodbManager().getProfiles().put(event.getUniqueId(), profile);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CompletableFuture.runAsync(() -> getProfile(event.getPlayer().getUniqueId()).MongodbSave());
    }
}
