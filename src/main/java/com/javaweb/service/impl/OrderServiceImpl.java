package com.javaweb.service.impl;

import com.javaweb.repository.OrderRepository;
import com.javaweb.service.OrderService;
import model.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Integer checkout(OrderRequestDTO orderRequestDTO) {
        return orderRepository.createOrderFromCart(orderRequestDTO);
    }

    @Override
    public List<Map<String, Object>> getOrdersByStatus(String status) {
        return orderRepository.getOrdersByStatus(status);
    }

    @Override
    public boolean updateOrderStatus(Integer orderId, String status) {
        return orderRepository.updateOrderStatus(orderId, status);
    }

    @Override
    public List<Map<String, Object>> getOrderDetails(Integer orderId) {
        // Ép kiểu để gọi trực tiếp hàm từ lớp Impl
        return ((com.javaweb.repository.impl.OrderRepositoryImpl) orderRepository).getOrderDetails(orderId);
    }

    @Override
    public List<Map<String, Object>> getOrdersByUser(Integer userId) {
        return ((com.javaweb.repository.impl.OrderRepositoryImpl) orderRepository).getOrdersByUser(userId);
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        return ((com.javaweb.repository.impl.OrderRepositoryImpl) orderRepository).getDashboardStats();
    }

    @Override
    public List<Map<String, Object>> getMonthlyRevenue(int year) {
        return ((com.javaweb.repository.impl.OrderRepositoryImpl) orderRepository).getMonthlyRevenue(year);
    }
}