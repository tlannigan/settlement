package ca.tlannigan.settlement.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class DatabaseHandler {
    private String connString = "";
    private MongoClient mongoClient;
    private MongoCollection<Document> playerCollection;

    public DatabaseHandler(FileConfiguration config) {
        this.connString = config.getString("mongo_connection_string");

        // Connect to MongoDB
        this.mongoClient = MongoClients.create(connString);
        MongoDatabase database = mongoClient.getDatabase("settlement");
        this.playerCollection = database.getCollection("player");
    }

    public void createPlayer(Player player) {
        String uuid = player.getUniqueId().toString();

        if (!doesPlayerExist(uuid)) {
            Document newPlayer = new Document("_id", uuid)
                    .append("settlement",
                            new Document("home",
                                    new Document("level", 0)
                                            .append("location", Arrays.asList(0, 0, 0)))
                            .append("mine",
                                    new Document("level", 0)
                                            .append("location", Arrays.asList(0, 0, 0)))
                            .append("blacksmith",
                                    new Document("level", 0)
                                            .append("location", Arrays.asList(0, 0, 0)))
                            .append("enchanting",
                                    new Document("level", 0)
                                            .append("location", Arrays.asList(0, 0, 0)))
                            .append("stable",
                                    new Document("level", 0)
                                            .append("location", Arrays.asList(0, 0, 0)))
                            .append("barn",
                                    new Document("level", 0)
                                            .append("location", Arrays.asList(0, 0, 0)))
                            .append("market",
                                    new Document("level", 0)
                                            .append("location", Arrays.asList(0, 0, 0)))
                            .append("farm",
                                    new Document("level", 0)
                                            .append("location", Arrays.asList(0, 0, 0)))
                            .append("mob_farm",
                                    new Document("level", 0)
                                            .append("location", Arrays.asList(0, 0, 0)))
                            .append("portal",
                                    new Document("level", 0)
                                            .append("location", Arrays.asList(0, 0, 0))));

            playerCollection.insertOne(newPlayer);
        }
    }

    private Boolean doesPlayerExist(String uuid) {
        if (playerCollection.countDocuments(Filters.eq("_id", uuid)) < 1) {
            return false;
        } else {
            return true;
        }
    }

    public void closeClient() {
        mongoClient.close();
    }
}

/*  playerDocument = {
 *      _id: 12348-9703465-627345
 *  }
 */