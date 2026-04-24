package com.javaweb.service;

import model.OrderRequestDTO;

public interface OrderService {
    Integer checkout(OrderRequestDTO orderRequestDTO);
}