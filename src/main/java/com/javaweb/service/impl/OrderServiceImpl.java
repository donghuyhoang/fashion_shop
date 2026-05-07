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
}