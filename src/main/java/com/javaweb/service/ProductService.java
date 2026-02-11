package com.javaweb.service;

import java.util.List;

import com.javaweb.builder.ProductSearchBuilder;

import model.productDTO;

public interface ProductService {
	List<productDTO> findAll();
	List<productDTO> findProduct(ProductSearchBuilder params);
}
