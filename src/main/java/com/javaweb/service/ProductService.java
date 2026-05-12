package com.javaweb.service;

import com.javaweb.builder.ProductSearchBuilder;
import model.ProductDTO;
import java.util.List;

public interface ProductService {
    List<ProductDTO> findAll();
    List<ProductDTO> findProduct(ProductSearchBuilder params);
    void save(ProductDTO dto);
    void update(ProductDTO dto);
    void delete(Integer id);
}