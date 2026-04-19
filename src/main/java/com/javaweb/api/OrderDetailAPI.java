package com.javaweb.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javaweb.service.OrderDetailService;
import model.OrderDetailDTO;

@CrossOrigin
@RestController
@RequestMapping("/api/order-details")
public class OrderDetailAPI {

    @Autowired
    private OrderDetailService orderDetailService;

    // Xem chi tiết các món hàng trong 1 đơn hàng cụ thể
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderDetailDTO>> getDetailsByOrder(@PathVariable Integer orderId) {
        List<OrderDetailDTO> details = orderDetailService.getDetailsByOrderId(orderId);
        if (details.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }
}