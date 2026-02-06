package com.javaweb.service;

import java.util.List;

import model.productDTO;

public interface ProductService {
	List<productDTO> findAll(String name);
}
