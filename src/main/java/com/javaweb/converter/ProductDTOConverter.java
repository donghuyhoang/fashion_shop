// Chuyen doi du lieu

package com.javaweb.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javaweb.repository.ProductRepository;
import com.javaweb.repository.entity.ProductEntity;

import model.productDTO;

@Component
public class ProductDTOConverter {
	@Autowired
	private ProductRepository productReponsitory;
	
	public static productDTO toProductDTO(ProductEntity productEntity) {
		productDTO product = new productDTO();
		product.setName(productEntity.getName());
		product.setPrice(productEntity.getPrice());
		product.setDescription(productEntity.getDescription());
		return product;
	}
	
	
}
