package com.javaweb.api;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Beans.*;

@RestController
public class BuildingAPI {
	static final String DB_URL = "jdbc:mysql://localhost:3306/fashion_shop";
	static final String USER = "javauser";
	static final String PASS = "Java@123";
	
    @GetMapping(value = "/api/products/")
    public List<productDTO> getProduct() {
    	String sql = "select * from products";
    	List<productDTO> result = new ArrayList<>();
    	try(Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
    			Statement stmt = conn.createStatement();
    			ResultSet rs = stmt.executeQuery(sql);){
    		while(rs.next()) {
    			productDTO product = new productDTO();
    			product.setName(rs.getString("name"));
    			product.setPrice(rs.getInt("price"));
    			product.setProduct_id(rs.getString("product_id"));
    			result.add(product);
    		}
    		System.out.println("Connected database successfully!");
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    		System.out.println("Connect database failed!");
    	}
//		System.out.println("ok");    
		return result;
	}
    
//    @PostMapping(value = "/api/building/")
    /*public List<BuildingDTO> getBuilding(@RequestBody){
    	
    }*/
}
