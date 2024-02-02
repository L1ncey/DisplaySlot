package dev.nevermind.baimao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import dev.nevermind.baimao.DisplaySlot.DisplaySlotHandler;
import dev.nevermind.baimao.DisplaySlot.command.DisplaySlotCommand;
import dev.nevermind.baimao.MongoDB.MongodbManager;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    private MongoClient mongoClient;
    @Getter
    private MongoDatabase mongoDatabase;
    @Getter
    private MongodbManager mongodbManager;
    @Getter
    private DisplaySlotHandler displaySlotHandler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        setupMongo();
        mongodbManager = new MongodbManager();
        displaySlotHandler = new DisplaySlotHandler();
        registerCommands();
        Bukkit.getLogger().info("  ");
        Bukkit.getLogger().info("  Display Loaded.");
        Bukkit.getLogger().info("  NeverMind Dev Team: BaiMao");
        Bukkit.getLogger().info("  ");
    }

    public void registerCommands() {
        DisplaySlotCommand displaySlotCommand = new DisplaySlotCommand("displayslot");
        MinecraftServer.getServer().server.getCommandMap().register(displaySlotCommand.getName(), " ", displaySlotCommand);
    }

    private void setupMongo() {
        mongoClient = new MongoClient(
                getConfig().getString("Mongo.Host"),
                getConfig().getInt("Mongo.Port")
        );

        String databaseId = getConfig().getString("Mongo.Database");
        mongoDatabase = mongoClient.getDatabase(databaseId);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("  ");
        Bukkit.getLogger().info("  Display Disable.");
        Bukkit.getLogger().info("  NeverMind Dev Team: BaiMao");
        Bukkit.getLogger().info("  ");
    }
}
