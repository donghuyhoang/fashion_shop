package com.javaweb.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaweb.builder.ProductSearchBuilder;
import com.javaweb.repository.ProductRepository;
import com.javaweb.repository.entity.ProductEntity;
import com.javaweb.service.ProductService;
import model.productDTO;


@Service
public class ProductServiceImpl implements ProductService{
	@Autowired
	private ProductRepository productReponsitory;
	@Override
	public List<productDTO> findProduct(ProductSearchBuilder params) {
		
		List<ProductEntity> productEntities = productReponsitory.findProduct(params);
		List<productDTO> result = new ArrayList<productDTO>();
		for (ProductEntity item : productEntities) {
			productDTO product = new productDTO();
			product.setName(item.getName());
			product.setPrice(item.getPrice());
			product.setDescription(item.getDescription());
			result.add(product);
		}
		return result;
	}
	@Override
	public List<productDTO> findAll() {
		List<ProductEntity> productEntities = productReponsitory.findAll();
		List<productDTO> result = new ArrayList<productDTO>();
		for (ProductEntity item : productEntities) {
			productDTO product = new productDTO();
			product.setName(item.getName());
			product.setPrice(item.getPrice());
			product.setDescription(item.getDescription());
			result.add(product);
		}
		return result;
	}

}
