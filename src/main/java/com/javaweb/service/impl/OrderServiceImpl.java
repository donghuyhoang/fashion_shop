package com.javaweb.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaweb.repository.OrderRepository;
import com.javaweb.repository.entity.OrderEntity;
import com.javaweb.service.OrderService;
import model.OrderDTO;
import model.OrderRequestDTO;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<OrderDTO> getOrdersByUserId(Integer userId) {
        List<OrderEntity> entities = orderRepository.findByUserId(userId);
        List<OrderDTO> dtos = new ArrayList<>();
        
        for (OrderEntity entity : entities) {
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(entity.getOrder_id());
            dto.setUserId(entity.getUser_id());
            dto.setOrderDate(entity.getOrder_date());
            dto.setTotalMoney(entity.getTotal_money());
            dto.setDiscountMoney(entity.getDiscount_money());
            dto.setFinalMoney(entity.getFinal_money());
            dto.setShippingAddress(entity.getShipping_address());
            dto.setReceiverName(entity.getReceiver_name());
            dto.setReceiverPhone(entity.getReceiver_phone());
            dto.setPaymentMethod(entity.getPayment_method());
            dto.setPaymentStatus(entity.getPayment_status());
            dto.setShippingStatus(entity.getShipping_status());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public boolean placeOrder(OrderRequestDTO orderRequest) {
        return orderRepository.createOrder(orderRequest);
    }
}