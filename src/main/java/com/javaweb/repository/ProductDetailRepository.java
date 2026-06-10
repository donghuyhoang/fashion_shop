package com.javaweb.repository;
import java.util.List;
import com.javaweb.repository.entity.ProductDetailEntity;

import model.ProductDetailDTO;

public interface ProductDetailRepository {
    List<ProductDetailEntity> findByProductId(Integer productId);
    void addProductDetail(ProductDetailDTO dto);
}