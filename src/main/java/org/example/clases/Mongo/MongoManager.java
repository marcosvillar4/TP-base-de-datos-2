package org.example.clases.Mongo;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoManager {
    private static final String URI = "mongodb://localhost:27017";
    private static final String DB_NAME = "tienda";

    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {
        if(database == null){
            MongoClient client = MongoClients.create(URI);
            database = client.getDatabase(DB_NAME);
        }

        return database;
    }
}
