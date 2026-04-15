package com.javaweb.repository.impl;

import com.javaweb.repository.SizeRepository;
import com.javaweb.repository.entity.SizeEntity;
import com.javaweb.utils.ConnectionJDBCUtil;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SizeRepositoryImpl implements SizeRepository {

    @Override
    public List<SizeEntity> findAll() {
        List<SizeEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM sizes";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                SizeEntity entity = new SizeEntity();
                entity.setId(rs.getInt("size_id"));
                entity.setName(rs.getString("value"));
                list.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}