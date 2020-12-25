package ca.tlannigan.settlement.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DatabaseHandler {
    private String connString = "";
    private MongoCollection<Document> playerCollection;

    public DatabaseHandler(FileConfiguration config) {
        this.connString = config.getString("mongo_connection_string");

        // Connect to MongoDB
        MongoClient mongoClient = MongoClients.create(connString);
        MongoDatabase database = mongoClient.getDatabase("settlement");
        this.playerCollection = database.getCollection("player");
    }

    public void createPlayer(Player player) {
        String uuid = player.getUniqueId().toString();

        if (playerCollection.countDocuments(Filters.eq("_id", uuid)) < 1) {
            Document newPlayer = new Document("_id", uuid);
            playerCollection.insertOne(newPlayer);
        }
    }
}

/*  playerDocument = {
 *      _id: 12348-9703465-627345
 *  }
 */