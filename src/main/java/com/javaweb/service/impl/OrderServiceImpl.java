package com.javaweb.service.impl;

import com.javaweb.repository.OrderRepository;
import com.javaweb.service.OrderService;
import model.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Integer checkout(OrderRequestDTO orderRequestDTO) {
        return orderRepository.createOrderFromCart(orderRequestDTO);
    }
}