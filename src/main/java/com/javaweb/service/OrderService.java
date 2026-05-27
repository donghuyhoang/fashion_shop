package com.javaweb.service;

import model.OrderRequestDTO;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Integer checkout(OrderRequestDTO orderRequestDTO);
    List<Map<String, Object>> getOrdersByStatus(String status);
    boolean updateOrderStatus(Integer orderId, String status);
    List<Map<String, Object>> getOrderDetails(Integer orderId);
    List<Map<String, Object>> getOrdersByUser(Integer userId);
    // Dashboard stats - đưa vào Service thay vì inject Impl trực tiếp vào Controller
    Map<String, Object> getDashboardStats();
    List<Map<String, Object>> getMonthlyRevenue(int year);
}