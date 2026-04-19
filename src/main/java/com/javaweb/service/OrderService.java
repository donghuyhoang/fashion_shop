package com.javaweb.service;
import java.util.List;
import model.OrderDTO;

public interface OrderService {
    List<OrderDTO> getOrdersByUserId(Integer userId);
}