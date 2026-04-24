package com.javaweb.repository;
import java.util.List;
import com.javaweb.repository.entity.ProductDetailEntity;

public interface ProductDetailRepository {
    List<ProductDetailEntity> findByProductId(Integer productId);
}