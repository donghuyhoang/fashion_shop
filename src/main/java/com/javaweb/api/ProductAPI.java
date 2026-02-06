package com.javaweb.api;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javaweb.service.ProductService;

import model.*;

@RestController
public class ProductAPI {
	@Autowired
	private ProductService productService;
	
    @GetMapping(value = "/api/products/")
    public List<productDTO> getProduct(@RequestParam(name = "name") String name) {
    	List<productDTO> result = productService.findAll(name);
    	return result;
	}
    

}
