package com.javaweb.service;
import java.util.List;
import model.ProductImageDTO;

public interface ProductImageService {
    List<ProductImageDTO> getImagesByProductId(Integer productId);
}