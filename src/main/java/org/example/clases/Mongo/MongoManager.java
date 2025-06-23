package org.example.clases.Mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoManager {
    private static final Logger logger = LoggerFactory.getLogger(MongoManager.class);

    //private static final String URI = "mongodb://localhost:27017";
    private static final String URI = "mongodb+srv://santimussi1959:adminpass@tienda.klg0vpp.mongodb.net/?retryWrites=true&w=majority&appName=tienda";
    private static final String DB_NAME = "tienda";

    private static MongoClient client;
    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {
        if (database == null) {
            client = MongoClients.create(URI);
            database = client.getDatabase(DB_NAME);
        }
        return database;
    }

    public static void close() {
        if (client != null) {
            client.close();
        }
    }
}
