package com.javaweb.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaweb.repository.ProductImageRepository;
import com.javaweb.repository.entity.ProductImageEntity;
import com.javaweb.service.ProductImageService;
import model.ProductImageDTO;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    
    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    public List<ProductImageDTO> getImagesByProductId(Integer productId) {
        List<ProductImageEntity> entities = productImageRepository.findByProductId(productId);
        List<ProductImageDTO> dtos = new ArrayList<>();
        
        for (ProductImageEntity entity : entities) {
            ProductImageDTO dto = new ProductImageDTO();
            dto.setImageId(entity.getImage_id());
            dto.setProductId(entity.getProduct_id());
            dto.setImageUrl(entity.getImage_url());
            dto.setIsThumbnail(entity.getIs_thumbnail());
            dto.setSortOrder(entity.getSort_order());
            dtos.add(dto);
        }
        return dtos;
    }
}