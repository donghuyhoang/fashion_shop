package com.javaweb.service;
import java.util.List;
import model.OrderDetailDTO;

public interface OrderDetailService {
    List<OrderDetailDTO> getDetailsByOrderId(Integer orderId);
}