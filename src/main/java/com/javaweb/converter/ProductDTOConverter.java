package com.javaweb.converter;
import org.springframework.stereotype.Component;
import com.javaweb.repository.entity.ProductEntity;
import model.productDTO;

@Component
public class ProductDTOConverter {
	public static productDTO toProductDTO(ProductEntity productEntity) {
		productDTO product = new productDTO();
		product.setId(productEntity.getProduct_id());
		product.setName(productEntity.getName());
		product.setPrice(productEntity.getPrice());
		product.setDescription(productEntity.getDescription());
		product.setBrandName(productEntity.getBrandName());
		product.setStock(productEntity.getStockQuantity());
		product.setThumb(productEntity.getThumbnailUrl());
		return product;
	}
}