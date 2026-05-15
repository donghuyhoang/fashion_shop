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
	public ResponseEntity<?> addProduct(@RequestBody ProductDTO dto) {
		try {
			// [QUAN TRỌNG] Phải nhận về ID từ Service
			Integer newProductId = productService.save(dto); 
			
			// Trả về JSON chứa ID để Frontend sử dụng
			return ResponseEntity.ok().body(java.util.Collections.singletonMap("id", newProductId));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(java.util.Collections.singletonMap("error", "Lỗi khi thêm sản phẩm: " + e.getMessage()));
		}
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO dto) {
		try {
			dto.setId(id);
			productService.update(dto);
			return ResponseEntity.ok().body(java.util.Collections.singletonMap("message", "Đã cập nhật sản phẩm thành công!"));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(java.util.Collections.singletonMap("error", "Lỗi khi cập nhật: " + e.getMessage()));
		}
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable("id") Integer id) {
	    try {
	        productService.delete(id);
	        return ResponseEntity.ok(java.util.Collections.singletonMap("message", "Đã xóa thành công sản phẩm ID: " + id));
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body(java.util.Collections.singletonMap("error", e.getMessage()));
	    }
	}
}
