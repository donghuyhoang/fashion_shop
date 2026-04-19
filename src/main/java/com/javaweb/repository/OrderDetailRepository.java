package com.javaweb.repository;
import java.util.List;
import com.javaweb.repository.entity.OrderDetailEntity;

public interface OrderDetailRepository {
    List<OrderDetailEntity> findByOrderId(Integer orderId);
}