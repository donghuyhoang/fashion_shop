package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.javaweb.repository.ProductRepository;
import com.javaweb.repository.entity.ProductEntity;
@Repository

public class ProductRepositoryImpl implements ProductRepository{
	static final String DB_URL = "jdbc:mysql://localhost:3306/fashion_shop";
	static final String USER = "javauser";
	static final String PASS = "Java@123";
	@Override
	public List<ProductEntity> findAll(String name) {
		String sql = "select * from products where name like '%"  + name + "%'";
    	List<ProductEntity> result = new ArrayList<>();
    	try(Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
    			Statement stmt = conn.createStatement();
    			ResultSet rs = stmt.executeQuery(sql);){
    		while(rs.next()) {
    			ProductEntity product = new ProductEntity();
    			product.setName(rs.getString("name"));
    			product.setPrice(rs.getInt("price"));
    			product.setProduct_id(rs.getInt("product_id"));
    			product.setDescription(rs.getString("description"));
    			result.add(product);
    		}
    		System.out.println("Connected database successfully!");
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    		System.out.println("Connect database failed!");
    	}
		return result;
	}
	
}
