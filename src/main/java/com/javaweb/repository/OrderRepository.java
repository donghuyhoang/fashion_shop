package com.javaweb.repository;

import model.OrderRequestDTO;
import java.util.List;
import java.util.Map;

public interface OrderRepository {
    // Hàm chính xử lý toàn bộ logic checkout trong 1 transaction
    Integer createOrderFromCart(OrderRequestDTO orderRequestDTO);
    List<Map<String, Object>> getOrdersByStatus(String status);
    boolean updateOrderStatus(Integer orderId, String status);
}