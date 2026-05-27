package com.javaweb.api;

import com.javaweb.service.OrderService;
import model.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/orders")
public class OrderAPI {

    @Autowired
    private OrderService orderService;
    // Đã xóa injection trực tiếp OrderRepositoryImpl - dùng OrderService thay thế

    @PostMapping("/checkout")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        try {
            Integer orderId = orderService.checkout(orderRequestDTO);
            return ResponseEntity.ok(orderId);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<List<Map<String, Object>>> getOrderDetails(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderDetails(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Map<String, Object>>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getOrdersByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        try {
            String newStatus = payload.get("status");
            boolean success = orderService.updateOrderStatus(id, newStatus);
            
            if (success) {
                return ResponseEntity.ok("Cập nhật trạng thái thành công");
            }
            return ResponseEntity.badRequest().body("Cập nhật trạng thái thất bại");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi Database: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(orderService.getDashboardStats());
    }

    // VD: /api/orders/dashboard/revenue/2024
    @GetMapping("/dashboard/revenue/{year}")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyRevenue(@PathVariable Integer year) {
        return ResponseEntity.ok(orderService.getMonthlyRevenue(year));
    }
}