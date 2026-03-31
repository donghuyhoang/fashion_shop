package com.javaweb.api;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javaweb.builder.ProductSearchBuilder;
import com.javaweb.service.BrandService;
import com.javaweb.service.ProductService;
import com.javaweb.utils.ConnectionJDBCUtil;

import model.*;
@CrossOrigin
@RestController
public class ProductAPI {
	@Autowired
	private ProductService productService;
	@Autowired
    private BrandService brandService;
	@GetMapping(value = "")
    public List<productDTO> getAllProduct(){
		List<productDTO> result = productService.findAll();
		return result;
	}
	@GetMapping(value = "/api/products/")
    public List<productDTO> getProduct(@ModelAttribute ProductSearchBuilder params) {
    	List<productDTO> result = productService.findProduct(params);
    	return result;
	}
    @GetMapping(value = "/api/brands/")
    public List<ItemDTO> getBrands() {
    	return brandService.findAll();
	}
    @GetMapping(value = "/api/categories")
    public List<productDTO> getCategories() {
    	List<productDTO> result = productService.findAll();
    	return result;
	}
    @PostMapping(value = "/api/products/")
	public void addProduct(@RequestBody productDTO dto) {
		productService.save(dto);
		System.out.println("Đã thêm sản phẩm thành công vào DB!");
	}

	@PutMapping(value = "/api/products/{id}")
	public void updateProduct(@PathVariable Integer id, @RequestBody productDTO dto) {
		dto.setId(id);
		productService.update(dto);
		System.out.println("Đã cập nhật sản phẩm ID: " + id);
	}

	@DeleteMapping(value = "/api/products/{id}")
	public void deleteProduct(@PathVariable Integer id) {
		productService.delete(id);
		System.out.println("Đã xóa sản phẩm ID: " + id);
	}
}
