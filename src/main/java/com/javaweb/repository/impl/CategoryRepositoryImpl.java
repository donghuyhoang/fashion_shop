package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.javaweb.repository.CategoryRepository;
import com.javaweb.utils.ConnectionJDBCUtil;

import model.ItemDTO;
@Repository
public class CategoryRepositoryImpl implements CategoryRepository{
	@Override
	public List<ItemDTO> findAll(){
		List<ItemDTO> list = new ArrayList<>();
		String sql = "select category_id, name from categories";
		
        try (Connection conn = ConnectionJDBCUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
                
               while(rs.next()) {
                   ItemDTO item = new ItemDTO();
                   item.setId(rs.getInt("category_id"));
                   item.setName(rs.getString("name"));
                   list.add(item);
               }
           } catch (Exception e) { 
               e.printStackTrace(); 
           }
           return list;
	}
}
