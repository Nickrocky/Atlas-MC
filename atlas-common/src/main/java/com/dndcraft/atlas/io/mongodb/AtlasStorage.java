package com.dndcraft.atlas.io.mongodb;

import com.dndcraft.atlas.InstanceProvider;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.SneakyThrows;
import org.bson.Document;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A general storage class for MongoDB to be used by any plugin that uses Atlas-Api, this removes a lot of the annoying
 * mongo setup saving the same 6 fields over 20 plugins.
 * @Author Nickrocky213
 * @Date 5/17/2021
 * */
public class AtlasStorage {
    private static HashMap<String, MongoCollection<Document>> atlasCollections;
    private static MongoCollection<Document> playerConfiguration;
    private static MongoDatabase database;
    private static MongoClient client;

    public AtlasStorage(){
        client = MongoClients.create("mongodb+srv://" + InstanceProvider.MONGO_CONFIG.getUsername() + ":" + InstanceProvider.MONGO_CONFIG.getPassword() + "@" + InstanceProvider.MONGO_CONFIG.getIpAddress() + "/" + InstanceProvider.MONGO_CONFIG.getDatabase() + "?retryWrites=true&w=majority&ssl=true&maxIdleTimeMS=5000");
        Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.SEVERE);
        Logger.getLogger("org.mongodb.driver.management").setLevel(Level.SEVERE);
        Logger.getLogger("org.mongodb.driver.cluster").setLevel(Level.SEVERE);
        Logger.getLogger("org.mongodb.driver.protocol.insert").setLevel(Level.SEVERE);
        Logger.getLogger("org.mongodb.driver.protocol.query").setLevel(Level.SEVERE);
        Logger.getLogger("org.mongodb.driver.protocol.update").setLevel(Level.SEVERE);
        atlasCollections = new HashMap<>();
        database = client.getDatabase(InstanceProvider.MONGO_CONFIG.getDatabase());
        loadCollections();
        playerConfiguration = database.getCollection("Atlas_Player_Configuration");
    }

    /**
     * Call this when you want to close the connection to the DB for whatever reason. (Plugman Reloads)
     * */
    public static void stop(){
        client.close();
    }

    /**
     * Loads all existent Collections in the mongo database.
     * */
    private void loadCollections(){
        var iter = database.listCollectionNames().iterator();
        while(iter.hasNext()){
            String collect = iter.next();
            atlasCollections.put(collect, database.getCollection(collect));
        }
    }

    /**
     * Checks if a particular collection is registered in Atlas-Api
     * @ApiNote: This does NOT check the DB directly if you want to check solely on the database side use isRegistered()
     * @param collectionName the String of the collection name you want to lookup
     * @return true if it exists and false if it doesn't.
     * */
    public static boolean hasCollection(String collectionName){
        if(atlasCollections.containsKey(collectionName)) return true;
        return false;
    }

    /**
     * Gets a collection by name if the collection exists in the map
     * @param collectionName the String of the name of the collection you want to get
     * @return the Collection of Documents
     * */
    public static MongoCollection<Document> getCollection(String collectionName){
        if(hasCollection(collectionName)){
            return atlasCollections.get(collectionName);
        }
        return null;
    }

    /**
     * Gets the player configuration instance
     * @ApiNote: YOU DO NOT NEED TO GRAB THIS, YOUR PLUGIN SHOULD BE USING THE META TAGS NOT THIS
     * @return the collection of documents for player config
     * */
    protected static MongoCollection<Document> getPlayerConfiguration(){
        return playerConfiguration;
    }

    /**
     * A method for the registration of new collections in Atlas' instance of MongoDB
     * @ApiNote: isRegistered() exists as a check solely for this method, if for some reason its not in the map but in the db
     * @param collectionName must be a unique name not used by any other plugins
     * @throws ReservedCollectionException If you try and register a collection with the Atlas player config name
     * @throws DuplicateRegistrationException If you try and register a collection with the name of another already registered collection
     * */
    @SneakyThrows
    public static void registerCollection(String collectionName){
        if(collectionName.equalsIgnoreCase("Atlas_Player_Configuration")) throw new ReservedCollectionException(collectionName);
        if(atlasCollections.containsKey(collectionName) || isRegistered(collectionName)) throw new DuplicateRegistrationException(collectionName);
        database.createCollection(collectionName);
        var collection = database.getCollection(collectionName);
        atlasCollections.put(collectionName, collection);
    }

    /**
     * A method for checking if a particular String is affiliated with a collection in the database.
     * @param collectionName the String you wish to check for the existence of a collection with
     * */
    public static boolean isRegistered(String collectionName){
        var collections = database.listCollectionNames().iterator();
        //System.out.println("Database: " + database.getName());
        while (collections.hasNext()){
            String nextCollectionName = collections.next();
            //System.out.println("Collection: "+nextCollectionName);
            if(nextCollectionName.equalsIgnoreCase(collectionName)){
                //System.out.println("2 Collection: "+nextCollectionName);
                return true;
            }
        }
        return false;
    }

    static class DuplicateRegistrationException extends Exception {
        public DuplicateRegistrationException(String collectionName){
            super("Error registering collection: " + collectionName + " collection name already taken!");
        }
    }

    static class ReservedCollectionException extends Exception {
        public ReservedCollectionException(String collectionName){
            super("Error registering collection: " + collectionName + " as the collection name is reserved by Atlas and cannot be changed!");
        }
    }

}
