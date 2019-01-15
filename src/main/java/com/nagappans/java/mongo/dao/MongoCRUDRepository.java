package com.nagappans.java.mongo.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoClientImpl;
import com.mongodb.client.model.Filters;
import com.nagappans.java.mongo.entity.Customer;
import org.bson.BSON;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.Code;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoCRUDRepository {

    public MongoCollection<Customer> connectDB(String hostname, Integer port, String dbname) {
        String connectionSting = String.format("mongodb://%s:%d",hostname, port);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                                .applyConnectionString(new ConnectionString(connectionSting))
                                .codecRegistry(getPojoCodecRegistry())
                                .build();
        MongoClient mongoClient = MongoClients.create(mongoClientSettings);
        //one more options of directly instantiating it
        //MongoClient mongoClient = new MongoClientImpl(mongoClientSettings, null);

        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbname);
        //return mongoDatabase.getCollection("customer");
        return mongoDatabase.getCollection("customer", Customer.class);
    }

    private CodecRegistry getPojoCodecRegistry() {
        return fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    public void createDocument() {
        Customer customer1 = new Customer("rahul", "gandhi", "rajeshkannan@gmail.com");
        MongoCollection<Customer> collection = connectDB("localhost", 27017, "ecommerce");
        collection.insertOne(customer1);
        readDocument();
    }

    public void updateDocument() {
        //Customer customer1 = new Customer("kamal", "kannan", "rajeshkannan@gmail.com");
        //Customer updateCustomer1 = new Customer("rajesh", "marimuthu", "rajeshmarimuthu@gmail.com");
        MongoCollection<Customer> collection = connectDB("localhost", 27017, "ecommerce");
        collection.updateOne(eq("firstName", "rahul"), set("firstName", "soniya"));
        //yet to try below way.
        //collection.updateOne(customer1, updateCustomer1);
        readDocument();
    }

    public void deleteDocument() {
        MongoCollection<Customer> collection = connectDB("localhost", 27017, "ecommerce");
        collection.deleteOne(eq("firstName", "mukesh"));
        readDocument();
    }

    public void customizedQuery() {
        MongoCollection<Customer> collection = connectDB("localhost", 27017, "ecommerce");
        collection.find(Filters.regex("firstName", "r")).forEach(new Consumer<Customer>() {
            public void accept(Customer customer) {
                System.out.println(customer);
            }
        });
    }

    public void readDocument() {
        //MongoCollection<Document> collection = connectDB("localhost", 27017, "ecommerce");
        MongoCollection<Customer> collection = connectDB("localhost", 27017, "ecommerce");
        /*collection.find().forEach(new Consumer<Document>() {
            public void accept(Document customer) {
                System.out.println(customer.toJson());
            }
        });*/
        collection.find().forEach(new Consumer<Customer>() {
            public void accept(Customer customer) {
                System.out.println(customer);
            }
        });
    }


    public static void main(String args[]) {
        MongoCRUDRepository mongoCRUDRepository = new MongoCRUDRepository();
        //Below are different CRUD operations methods available which can be uncommented and tried out for understading.
        //mongoCRUDRepository.readDocument();
        //mongoCRUDRepository.createDocument();
        //mongoCRUDRepository.updateDocument();
        //mongoCRUDRepository.deleteDocument();
        mongoCRUDRepository.customizedQuery();
    }
}
