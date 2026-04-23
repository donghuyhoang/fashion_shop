package com.javaweb.api;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javaweb.builder.ProductSearchBuilder;
import com.javaweb.service.ProductService;

import model.*;
@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductAPI {
	@Autowired
	private ProductService productService;
	
	@GetMapping
    public List<ProductDTO> getAllProduct(){
		List<ProductDTO> result = productService.findAll();
		return result;
	}
	@GetMapping(value = "/search")
    public List<ProductDTO> getProduct(@ModelAttribute ProductSearchBuilder params) {
    	List<ProductDTO> result = productService.findProduct(params);
    	return result;
	}
    @PostMapping
	public void addProduct(@RequestBody ProductDTO dto) {
		productService.save(dto);
		System.out.println("Đã thêm sản phẩm thành công vào DB!");
	}

	@PutMapping(value = "/{id}")
	public void updateProduct(@PathVariable Integer id, @RequestBody ProductDTO dto) {
		dto.setId(id);
		productService.update(dto);
		System.out.println("Đã cập nhật sản phẩm ID: " + id);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable("id") Integer id) {
	    productService.delete(id);
	    return ResponseEntity.ok("Đã xóa thành công sản phẩm ID: " + id);
	}
}
