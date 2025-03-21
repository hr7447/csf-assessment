package vttp.batch5.csf.assessment.server.repositories;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class OrdersRepository {

  @Autowired
  private MongoTemplate mongoTemplate;

  public List<Document> getMenu() {
    Query query = new Query();
    query.with(Sort.by(Sort.Direction.ASC, "name"));
    
    return mongoTemplate.find(query, Document.class, "menus");
  }

  public void insertOrder(String orderId, String paymentId, String username, 
                          double total, List<Document> items) {
    Document order = new Document();
    order.append("_id", orderId)
         .append("order_id", orderId)
         .append("payment_id", paymentId)
         .append("username", username)
         .append("total", total)
         .append("timestamp", new Date())
         .append("items", items);
    
    mongoTemplate.insert(order, "orders");
  }
}
