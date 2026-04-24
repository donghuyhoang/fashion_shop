package com.javaweb.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javaweb.service.OrderService;
import model.OrderDTO;
import model.OrderRequestDTO;

@CrossOrigin
@RestController
@RequestMapping("/api/orders")
public class OrderAPI {

    @Autowired
    private OrderService orderService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Integer userId) {
        List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orders);	
    }
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        boolean isSuccess = orderService.placeOrder(orderRequest);
        if (isSuccess) {
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.badRequest().body("fail");
    }
}