package com.javaweb.repository;

import model.OrderRequestDTO;

public interface OrderRepository {
    // Hàm chính xử lý toàn bộ logic checkout trong 1 transaction
    Integer createOrderFromCart(OrderRequestDTO orderRequestDTO);
}