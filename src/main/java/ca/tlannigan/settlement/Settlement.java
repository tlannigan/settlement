package ca.tlannigan.settlement;

import ca.tlannigan.settlement.commands.CommandDescription;
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

        // Connect to MongoDB
        String connString = config.getString("mongo_connection_string");
        MongoClient mongoClient = MongoClients.create(connString);
        MongoDatabase database = mongoClient.getDatabase("sample_weatherdata");
        MongoCollection<Document> coll = database.getCollection("data");

        this.getCommand("description").setExecutor(new CommandDescription());
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        // Fired when the server stop and disables all plugins
    }
}
