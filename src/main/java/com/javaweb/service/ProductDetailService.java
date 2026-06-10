package com.javaweb.service;
import java.util.List;
import model.ProductDetailDTO;

public interface ProductDetailService {
    List<ProductDetailDTO> getDetailsByProductId(Integer productId);
    void addProductDetail(model.ProductDetailDTO dto);
}