package com.javaweb.repository;
import java.util.List;
import com.javaweb.repository.entity.ProductReviewEntity;

public interface ProductReviewRepository {
    List<ProductReviewEntity> findByProductId(Integer productId);
}