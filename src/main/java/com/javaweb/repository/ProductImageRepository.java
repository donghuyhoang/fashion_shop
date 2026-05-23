package com.javaweb.repository;
import java.util.List;
import com.javaweb.repository.entity.ProductImageEntity;

public interface ProductImageRepository {
    List<ProductImageEntity> findByProductId(Integer productId);
    
    void saveAll(Integer productId, List<String> imageUrls);
    void deleteByProductId(Integer productId);
}