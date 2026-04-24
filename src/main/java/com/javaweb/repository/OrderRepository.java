package com.javaweb.repository;
import java.util.List;
import com.javaweb.repository.entity.OrderEntity;
import model.OrderRequestDTO;

public interface OrderRepository {
    List<OrderEntity> findByUserId(Integer userId);
    boolean createOrder(OrderRequestDTO orderRequest);
}