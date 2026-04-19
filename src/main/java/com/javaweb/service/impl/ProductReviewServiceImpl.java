package com.javaweb.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaweb.repository.ProductReviewRepository;
import com.javaweb.repository.entity.ProductReviewEntity;
import com.javaweb.service.ProductReviewService;
import model.ProductReviewDTO;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {
    
    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Override
    public List<ProductReviewDTO> getReviewsByProductId(Integer productId) {
        List<ProductReviewEntity> entities = productReviewRepository.findByProductId(productId);
        List<ProductReviewDTO> dtos = new ArrayList<>();
        
        for (ProductReviewEntity entity : entities) {
            ProductReviewDTO dto = new ProductReviewDTO();
            dto.setReviewId(entity.getReview_id());
            dto.setUserId(entity.getUser_id());
            dto.setUserFullName(entity.getUser_full_name());
            dto.setProductId(entity.getProduct_id());
            dto.setOrderId(entity.getOrder_id());
            dto.setRating(entity.getRating());
            dto.setComment(entity.getComment());
            dto.setCreatedAt(entity.getCreated_at());
            dtos.add(dto);
        }
        return dtos;
    }
}