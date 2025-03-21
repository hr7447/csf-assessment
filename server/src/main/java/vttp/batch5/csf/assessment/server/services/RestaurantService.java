package vttp.batch5.csf.assessment.server.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp.batch5.csf.assessment.server.repositories.OrdersRepository;
import vttp.batch5.csf.assessment.server.repositories.RestaurantRepository;

@Service
public class RestaurantService {

  @Autowired
  private OrdersRepository ordersRepo;
  
  @Autowired
  private RestaurantRepository restaurantRepo;
  
  @Autowired
  private RestTemplate restTemplate;

  public JsonArray getMenu() {
    List<Document> menuItems = ordersRepo.getMenu();
    
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    
    for (Document item : menuItems) {
      JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
          .add("_id", item.getString("_id"))
          .add("name", item.getString("name"))
          .add("price", item.getDouble("price"))
          .add("description", item.getString("description"));
      
      arrayBuilder.add(objectBuilder);
    }
    
    return arrayBuilder.build();
  }
  
  public JsonObject processOrder(JsonObject orderJson) throws Exception {
    String username = orderJson.getString("username");
    String password = orderJson.getString("password");
    
    if (!restaurantRepo.validateCustomer(username, password)) {
      throw new Exception("Invalid username and/or password");
    }
    
    String orderId = generateRandomId(8);
    
    JsonArray items = orderJson.getJsonArray("items");
    double total = 0.0;
    for (int i = 0; i < items.size(); i++) {
      JsonObject item = items.getJsonObject(i);
      total += item.getJsonNumber("price").doubleValue() * item.getInt("quantity");
    }
    
    JsonObject paymentResponse = processPayment(orderId, username, total);
    String paymentId = paymentResponse.getString("payment_id");
    
    restaurantRepo.insertOrder(orderId, paymentId, total, username);
    
    List<Document> itemDocuments = items.stream()
        .map(item -> {
          JsonObject jsonItem = (JsonObject) item;
          Document doc = new Document();
          doc.append("id", jsonItem.getString("id"))
             .append("price", jsonItem.getJsonNumber("price").doubleValue())
             .append("quantity", jsonItem.getInt("quantity"));
          return doc;
        })
        .collect(Collectors.toList());
    
    ordersRepo.insertOrder(orderId, paymentId, username, total, itemDocuments);
    
    return Json.createObjectBuilder()
        .add("orderId", orderId)
        .add("paymentId", paymentId)
        .add("total", total)
        .add("timestamp", paymentResponse.getJsonNumber("timestamp").longValue())
        .build();
  }
  
  private String generateRandomId(int length) {
    return UUID.randomUUID().toString().substring(0, length);
  }
  
  private JsonObject processPayment(String orderId, String username, double amount) throws Exception {
    String paymentServiceUrl = "https://payment-service-production-a75a.up.railway.app/api/payment";
    
    String jsonPayload = String.format(
        "{\"order_id\":\"%s\",\"payer\":\"%s\",\"payee\":\"%s\",\"payment\":%.2f}",
        orderId, username, "Tan Hong Rui", amount);
    
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set("Accept", "application/json");
    headers.set("X-Authenticate", username);
    
    HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
    
    try {
      ResponseEntity<String> response = restTemplate.exchange(
          paymentServiceUrl, 
          HttpMethod.POST, 
          request, 
          String.class);
      
      return Json.createReader(new java.io.StringReader(response.getBody())).readObject();
    } catch (Exception e) {
      throw new Exception("Payment processing failed: " + e.getMessage());
    }
  }
}
