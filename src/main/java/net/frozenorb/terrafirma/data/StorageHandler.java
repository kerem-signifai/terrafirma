package net.frozenorb.terrafirma.data;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.terrafirma.TerraFirma;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.map.RealmBoard;
import org.bson.Document;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageHandler {
    private static final String MONGO_COLLECTION = "PlayerClaims";

    private static Gson                 gson;
    private static MongoDatabase        db;
    private static MongoClient          mongoPool;

    @Getter private static boolean      loading;

    static {
        gson        =      new Gson();
        mongoPool   =      new MongoClient();
    }

    /**
     * Synchronously loads all of the claim objects from a MongoDB database
     */
    public void loadClaims() {
        loading = true;
        long now = System.nanoTime();

        getCollection(MONGO_COLLECTION).find().iterator().forEachRemaining(data -> {
            if (data.containsKey("claims")) {

                List<Document> claimsJson = data.get("claims", ArrayList.class);

                for (Document claimDoc : claimsJson) {

                    String jsonPayload = claimDoc.toJson();

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(jsonPayload).getAsJsonObject();

                    Claim claim = gson.fromJson(jsonObject, Claim.class);

                    RealmBoard.addClaim(claim);
                }
            }
        });

        loading = false;

        String nanosFancy = new DecimalFormat("#.##").format((System.nanoTime() - now) / 1E6D);
        System.out.println("{" + TerraFirma.getInstance().getName() + "} Successfully loaded "
                + RealmBoard.getClaims().size() + " claims in " + nanosFancy + "ms");
    }

    /**
     * Closes the MongoClient collection
     */
    public void closeMongo() {
        mongoPool.close();
    }

    /**
     * Synchronously saves a player's data
     *
     * @param uuid the UUID of the player to save
     */
    public void updatePlayer(UUID uuid) {
        BasicDBObject payload = new BasicDBObject();
        BasicDBList claims = new BasicDBList();

        RealmBoard.getClaims().forEach(claim -> {
            if (claim.getOwner().equals(uuid)) {

                BasicDBObject parsedGson = (BasicDBObject) JSON.parse(gson.toJson(claim)); // Store Mongo's JSON object
                claims.add(parsedGson);
            }
        });


        payload.put("claims", claims);

        set(uuid, MONGO_COLLECTION, payload);
    }


    /**
     * Upserts a player's Claim data in the MongoDB database
     *
     * @param uuid the uuid of the player
     * @param coll name of the collection to modify
     * @param data the JSON object to upsert
     * @return MongoDB UpdateResult
     */
    private UpdateResult set(UUID uuid, String coll, BasicDBObject data) {

        return getCollection(coll).updateOne(
                new BasicDBObject("_id", uuid.toString()),
                new BasicDBObject("$set", data),
                new UpdateOptions().upsert(true)
        );
    }

    /**
     * @param coll collection name
     * @return MongoDB Collection
     */
    private MongoCollection<Document> getCollection(String coll) {
        if (db == null) {
            db = mongoPool.getDatabase(TerraFirma.getInstance().getName());
        }

        return db.getCollection(coll);
    }
}
