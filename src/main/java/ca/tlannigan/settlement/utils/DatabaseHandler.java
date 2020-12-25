package ca.tlannigan.settlement.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
        Document newPlayer = new Document("_id", player.getUniqueId().toString());
        playerCollection.insertOne(newPlayer);
    }
}

/*  playerDocument = {
 *      _uuid: 123489703465627345
 *  }
 */