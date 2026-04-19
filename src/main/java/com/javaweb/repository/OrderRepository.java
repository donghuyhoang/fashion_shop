package com.javaweb.repository;
import java.util.List;
import com.javaweb.repository.entity.OrderEntity;

public interface OrderRepository {
    List<OrderEntity> findByUserId(Integer userId);
}