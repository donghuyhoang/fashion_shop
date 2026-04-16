package com.javaweb.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaweb.repository.ProductDetailRepository;
import com.javaweb.repository.entity.ProductDetailEntity;
import com.javaweb.service.ProductDetailService;
import model.ProductDetailDTO;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {
    
    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Override
    public List<ProductDetailDTO> getDetailsByProductId(Integer productId) {
        List<ProductDetailEntity> entities = productDetailRepository.findByProductId(productId);
        List<ProductDetailDTO> dtos = new ArrayList<>();
        
        for (ProductDetailEntity entity : entities) {
            ProductDetailDTO dto = new ProductDetailDTO();
            dto.setProductDetailId(entity.getProduct_detail_id());
            dto.setProductId(entity.getProduct_id());
            
            // Map IDs
            dto.setSizeId(entity.getSize_id());
            dto.setColorId(entity.getColor_id());
            
            dto.setStockQuantity(entity.getStock_quantity());
            dto.setPrice(entity.getPrice());
            dto.setThumbnailUrl(entity.getThumbnail_img_url());
            dtos.add(dto);
        }
        return dtos;
    }
}