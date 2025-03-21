package vttp.batch5.csf.assessment.server.config;

import java.util.Arrays;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

@Configuration
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            System.out.println("Initializing databases...");
            
            // Initialize MySQL tables
            initializeMySQLDatabase();
            
            // Initialize MongoDB collections
            initializeMongoDBDatabase();
            
            System.out.println("Database initialization complete!");
        };
    }

    private void initializeMySQLDatabase() {
        System.out.println("Initializing MySQL database...");
        
        // Create customers table if it doesn't exist
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS customers (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  username VARCHAR(128) NOT NULL UNIQUE," +
            "  password VARCHAR(256) NOT NULL" +
            ")"
        );
        
        // Create place_orders table if it doesn't exist
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS place_orders (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  order_id VARCHAR(8) NOT NULL," +
            "  payment_id VARCHAR(128) NOT NULL," +
            "  order_date DATE NOT NULL," +
            "  total DECIMAL(10,2) NOT NULL," +
            "  username VARCHAR(128) NOT NULL" +
            ")"
        );
        
        // Check if 'fred' user exists, if not add it
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM customers WHERE username = ?", 
            Integer.class, 
            "fred"
        );
        
        if (count != null && count == 0) {
            jdbcTemplate.update(
                "INSERT INTO customers (username, password) VALUES (?, SHA2(?, 224))",
                "fred", "fred"
            );
            System.out.println("Added default user 'fred'");
        }
    }

    private void initializeMongoDBDatabase() {
        System.out.println("Initializing MongoDB database...");
        
        // Check if menus collection exists and has items
        MongoCollection<Document> menusCollection = mongoTemplate.getCollection("menus");
        
        long count = menusCollection.countDocuments();
        
        if (count == 0) {
            // Add sample menu items
            Document[] menuItems = {
                new Document("_id", "9aedc2a8")
                    .append("name", "Hawaiian Pizza")
                    .append("price", 9.2)
                    .append("description", "Tomato sauce, cheese, ham, and pineapple"),
                    
                new Document("_id", "bf5a7d6e")
                    .append("name", "Pepperoni Pizza")
                    .append("price", 9.9)
                    .append("description", "Tomato sauce, cheese, and pepperoni"),
                    
                new Document("_id", "c4db8e9a")
                    .append("name", "Kielbasa Quesadillas")
                    .append("price", 7.7)
                    .append("description", "Polish sausage with cheese in a flour tortilla"),
                    
                new Document("_id", "d2f7c1b5")
                    .append("name", "Chicken Bruschetta")
                    .append("price", 7.7)
                    .append("description", "Grilled chicken with tomatoes on toasted bread")
            };
            
            menusCollection.insertMany(Arrays.asList(menuItems));
            System.out.println("Added sample menu items");
        }
        
        // Ensure orders collection exists
        if (!mongoTemplate.collectionExists("orders")) {
            mongoTemplate.createCollection("orders");
            System.out.println("Created orders collection");
        }
    }
} 