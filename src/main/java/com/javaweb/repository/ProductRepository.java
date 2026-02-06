package com.javaweb.repository;

import java.util.ArrayList;
import java.util.List;

import com.javaweb.repository.entity.ProductEntity;

public interface ProductRepository {
	List<ProductEntity> findAll(String name);
	
}
