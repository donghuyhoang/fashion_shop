package com.javaweb.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaweb.repository.OrderDetailRepository;
import com.javaweb.repository.entity.OrderDetailEntity;
import com.javaweb.service.OrderDetailService;
import model.OrderDetailDTO;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetailDTO> getDetailsByOrderId(Integer orderId) {
        List<OrderDetailEntity> entities = orderDetailRepository.findByOrderId(orderId);
        List<OrderDetailDTO> dtos = new ArrayList<>();
        
        for (OrderDetailEntity entity : entities) {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setOrderDetailId(entity.getOrder_detail_id());
            dto.setOrderId(entity.getOrder_id());
            dto.setProductDetailId(entity.getProduct_detail_id());
            dto.setQuantity(entity.getQuantity());
            dto.setUnitPrice(entity.getUnit_price());
            dto.setTotalPrice(entity.getTotal_price());
            dtos.add(dto);
        }
        return dtos;
    }
}