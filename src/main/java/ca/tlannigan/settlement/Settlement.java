package ca.tlannigan.settlement;

import ca.tlannigan.settlement.commands.CommandDescription;
import ca.tlannigan.settlement.utils.DatabaseHandler;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Settlement extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Settlements will reign");

        // Create default config.yml if one is not available
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();

        // Register commands and listeners
        this.getCommand("description").setExecutor(new CommandDescription());
        getServer().getPluginManager().registerEvents(new PlayerListener(config), this);
    }

    @Override
    public void onDisable() {
        // Fired when the server stop and disables all plugins
    }
}
