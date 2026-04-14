package com.javaweb.service;

import java.util.List;

import com.javaweb.builder.ProductSearchBuilder;

import model.productDTO;

public interface ProductService {
	List<productDTO> findAll();
	List<productDTO> findProduct(ProductSearchBuilder params);
	void save(model.productDTO dto);
    void update(model.productDTO dto);
    void delete(Integer id);
}
