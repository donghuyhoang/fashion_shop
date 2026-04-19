package com.javaweb.service;
import java.util.List;
import model.ProductReviewDTO;

public interface ProductReviewService {
    List<ProductReviewDTO> getReviewsByProductId(Integer productId);
}