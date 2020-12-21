package ca.tlannigan.settlement;

import ca.tlannigan.settlement.commands.CommandDescription;
import org.bukkit.plugin.java.JavaPlugin;

public class Settlement extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Settlements will reign");
        this.getCommand("description").setExecutor(new CommandDescription());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // Connect to MongoDB
//        MongoClient mongoClient = MongoClients.create(
//                "mongodb+srv://tlannigan:PmU9F334CtgZiuC@spigotcluster.ahu5k.mongodb.net/sample_weatherdata?retryWrites=true&w=majority");
//        MongoDatabase database = mongoClient.getDatabase("sample_weatherdata");
//        MongoCollection<Document> coll = database.getCollection("data");


        System.out.println("Plugin has started up");
    }

    @Override
    public void onDisable() {
        // Fired when the server stop and disables all plugins
    }
}
