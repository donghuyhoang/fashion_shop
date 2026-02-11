package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.javaweb.builder.ProductSearchBuilder;
import com.javaweb.repository.ProductRepository;
import com.javaweb.repository.entity.ProductEntity;
import com.javaweb.utils.ConnectionJDBCUtil;
@Repository

public class ProductRepositoryImpl implements ProductRepository{
	@Override
	public List<ProductEntity> findProduct(ProductSearchBuilder params) {
//		String sql = "select * from products where name like '%"  + params.getName() + "%'";
		StringBuilder sql = new StringBuilder("select * from products p join product_details pd on p.product_id = pd.product_id where  ");
		if(params.getName() != "" || params.getName() != "null") {
			sql.append("p.name like '%" + params.getName() + "%' ");
		}
		if(params.getMinPrice() != null) {
			sql.append(" and p.price >= " + params.getMinPrice());
		}
		if(params.getMaxPrice() != null) {
			sql.append(" and p.price <= " + params.getMaxPrice());
		}
		if(params.getBrandId() != null) {
			sql.append(" and p.brand_id = " + params.getBrandId());
		}
		if(params.getCategoryId() != null) {
			sql.append(" and pd.category_id = " + params.getCategoryId());
		}
		if(params.getColorId() != null) {
			sql.append(" and pd.color_id = " + params.getColorId());
		}
		if(params.getSizeId() != null) {
			sql.append(" and pd.size_id = " + params.getSizeId());
		}
    	List<ProductEntity> result = new ArrayList<>();
    	try(Connection conn = ConnectionJDBCUtil.getConnection();
    			Statement stmt = conn.createStatement();
    			ResultSet rs = stmt.executeQuery(sql.toString());){
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

	@Override
	public List<ProductEntity> findAll() {
		String sql = "select * from products";
		List<ProductEntity> result = new ArrayList<>();
		try(Connection conn = ConnectionJDBCUtil.getConnection();
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
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
