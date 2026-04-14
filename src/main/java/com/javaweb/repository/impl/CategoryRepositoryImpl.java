package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

import org.springframework.stereotype.Repository;

import com.javaweb.repository.CategoryRepository;
import com.javaweb.repository.entity.CategoryEntity;
import com.javaweb.utils.ConnectionJDBCUtil;


@Repository
public class CategoryRepositoryImpl implements CategoryRepository{
	@Override
	public List<CategoryEntity> findAll(){
		List<CategoryEntity> list = new ArrayList<>();
		String sql = "select * from categories";
		
        try (Connection conn = ConnectionJDBCUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
                
               while(rs.next()) {
                   CategoryEntity category = new CategoryEntity();
                   category.setCategory_id(rs.getInt("category_id"));
                   category.setName(rs.getString("name"));
                   list.add(category);
               }
           } catch (Exception e) { 
               e.printStackTrace(); 
           }
           return list;
	}
}
