package dev.nevermind.baimao.MongoDB;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.nevermind.baimao.Main;
import dev.nevermind.baimao.Utils.MongoUtils;
import lombok.Data;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public class MongodbProfile {
    private final UUID uniqueId;
    @Getter
    private String playerName;
    @Getter
    private String used = "none";
    @Getter
    private List<String> obtained = new ArrayList<>();

    public MongodbProfile(UUID uniqueId) {
        this.uniqueId = uniqueId;

        MongodbLoad();
    }

    public void MongodbLoad() {
        Document document = MongoUtils.getCollection("profiles").find(Filters.eq("uuid", uniqueId.toString())).first();

        if (document == null) return;

        playerName = document.getString("playerName");
        used = document.getString("used");
        obtained = document.getList("obtained", String.class, new ArrayList<>());
    }

    public void MongodbSave(){
        CompletableFuture.runAsync(() -> {
            Document document = new Document();
            document.put("uuid", this.uniqueId.toString());
            document.put("playerName", Bukkit.getPlayer(uniqueId).getName());
            document.put("used", used);
            document.put("obtained", obtained);

            Bson filter = Filters.eq("uuid", uniqueId.toString());
            MongoUtils.getCollection("profiles").replaceOne(filter, document, new ReplaceOptions().upsert(true));
        });
    }

    public boolean isPermission(Player player, String s) {
        return player.hasPermission(Main.getInstance().getDisplaySlotHandler().getDisplaySlot(s).getPermission()) || obtained.contains(s);
    }
}
