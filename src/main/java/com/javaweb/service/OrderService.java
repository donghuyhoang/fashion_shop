package com.javaweb.service;
import java.util.List;
import model.OrderDTO;
import model.OrderRequestDTO;

public interface OrderService {
    List<OrderDTO> getOrdersByUserId(Integer userId);
    boolean placeOrder(OrderRequestDTO orderRequest);
}