package vttp.batch5.csf.assessment.server.repositories;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

// Use the following class for MySQL database
@Repository
public class RestaurantRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SQL_VALIDATE_USER = 
        "SELECT COUNT(*) FROM customers WHERE username = ? AND password = SHA2(?, 224)";

    private static final String SQL_INSERT_ORDER = 
        "INSERT INTO place_orders(order_id, payment_id, order_date, total, username) VALUES(?, ?, ?, ?, ?)";

    // Validate customer credentials
    public boolean validateCustomer(String username, String password) {
        Integer count = jdbcTemplate.queryForObject(
            SQL_VALIDATE_USER, 
            Integer.class, 
            username, password
        );
        return count != null && count > 0;
    }

    // Insert order into MySQL
    public boolean insertOrder(String orderId, String paymentId, double total, String username) {
        int updated = jdbcTemplate.update(
            SQL_INSERT_ORDER,
            orderId,
            paymentId,
            Date.valueOf(LocalDate.now()),
            total,
            username
        );
        return updated > 0;
    }
}
