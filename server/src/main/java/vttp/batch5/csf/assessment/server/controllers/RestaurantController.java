package vttp.batch5.csf.assessment.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.batch5.csf.assessment.server.services.RestaurantService;

@RestController
@RequestMapping(path="/api")
public class RestaurantController {

  @Autowired
  private RestaurantService restaurantSvc;

  @GetMapping(path="/menu", produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getMenus() {
    try {
      return ResponseEntity.ok(restaurantSvc.getMenu().toString());
    } catch (Exception e) {
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Json.createObjectBuilder()
                  .add("message", "Error retrieving menu: " + e.getMessage())
                  .build().toString());
    }
  }

  @PostMapping(path="/food_order", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> postFoodOrder(@RequestBody String payload) {
    try {
      JsonReader reader = Json.createReader(new java.io.StringReader(payload));
      JsonObject orderJson = reader.readObject();
      
      JsonObject confirmation = restaurantSvc.processOrder(orderJson);
      
      return ResponseEntity.ok(confirmation.toString());
    } catch (Exception e) {
      JsonObject errorPayload = Json.createObjectBuilder()
          .add("message", e.getMessage())
          .build();
          
      if (e.getMessage().contains("Invalid username and/or password")) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorPayload.toString());
      }
      
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(errorPayload.toString());
    }
  }
}
