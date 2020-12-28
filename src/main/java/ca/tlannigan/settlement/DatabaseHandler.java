package ca.tlannigan.settlement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import java.util.Arrays;
import java.util.List;

public class DatabaseHandler {
    private final MongoClient mongoClient;
    private final MongoCollection<Document> playerCollection;

    public DatabaseHandler(FileConfiguration config) {
        String connString = config.getString("mongo_connection_string");

        // Connect to MongoDB
        this.mongoClient = MongoClients.create(connString);
        MongoDatabase database = mongoClient.getDatabase("settlement");
        this.playerCollection = database.getCollection("player");
    }

    public Document getPlayer(String uuid) {
        return playerCollection.find(eq("_id", uuid)).first();
    }

    public void createPlayer(String uuid) {
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

    public void updatePlayer(String uuid, String fieldPath, int value) {
        playerCollection.updateOne(eq("_id", uuid), set(fieldPath, value));
    }

    public void updatePlayer(String uuid, String fieldPath, List<Integer> value) {
        playerCollection.updateOne(eq("_id", uuid), set(fieldPath, value));
    }

    public void closeClient() {
        mongoClient.close();
    }

    private Boolean doesPlayerExist(String uuid) {
        if (playerCollection.countDocuments(eq("_id", uuid)) < 1) {
            return false;
        } else {
            return true;
        }
    }

    private String getUUID(Player player) {
        return player.getUniqueId().toString();
    }
}

/*  playerDocument = {
 *      _id: 12348-9703465-627345
 *      settlement: {
 *          home: {
 *              level: 0,
 *              location: [ 0, 0, 0 ]
 *          },
 *          etc for every building
 *      }
 *  }
 */