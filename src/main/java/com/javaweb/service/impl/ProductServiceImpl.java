package com.javaweb.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaweb.builder.ProductSearchBuilder;
import com.javaweb.converter.ProductDTOConverter;
import com.javaweb.repository.ProductRepository;
import com.javaweb.repository.entity.ProductEntity;
import com.javaweb.service.ProductService;

import model.ProductDTO;


@Service
public class ProductServiceImpl implements ProductService{
	@Autowired
	private ProductRepository productReponsitory;
	@Override
	public List<ProductDTO> findProduct(ProductSearchBuilder params) {
		
		List<ProductEntity> productEntities = productReponsitory.findProduct(params);
		List<ProductDTO> result = new ArrayList<ProductDTO>();
		for (ProductEntity item : productEntities) {
			result.add(ProductDTOConverter.toProductDTO(item));
		}
		return result;
	}
	@Override
	public List<ProductDTO> findAll() {
		List<ProductEntity> productEntities = productReponsitory.findAll();
		List<ProductDTO> result = new ArrayList<ProductDTO>();
		for (ProductEntity item : productEntities) {
			result.add(ProductDTOConverter.toProductDTO(item));
		}
		return result;
	}
	@Override
	public void save(model.ProductDTO dto) {
		productReponsitory.save(dto);
	}

	@Override
	public void update(model.ProductDTO dto) {
		productReponsitory.update(dto);
	}

	@Override
	public void delete(Integer id) {
		productReponsitory.delete(id);
	}

}
