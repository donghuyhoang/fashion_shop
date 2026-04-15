package com.javaweb.service;

import java.util.List;

import com.javaweb.builder.ProductSearchBuilder;

import model.ProductDTO;

public interface ProductService {
	List<ProductDTO> findAll();
	List<ProductDTO> findProduct(ProductSearchBuilder params);
	void save(model.ProductDTO dto);
    void update(model.ProductDTO dto);
    void delete(Integer id);
}
