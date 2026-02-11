package com.javaweb.repository;

import java.util.List;

import com.javaweb.builder.ProductSearchBuilder;
import com.javaweb.repository.entity.ProductEntity;

public interface ProductRepository {
	List<ProductEntity> findProduct(ProductSearchBuilder paramss);
	List<ProductEntity> findAll();
	
}
