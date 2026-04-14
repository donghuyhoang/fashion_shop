package com.javaweb.repository.impl;

import com.javaweb.repository.BrandRepository;
import com.javaweb.repository.entity.BrandEntity;
import com.javaweb.utils.ConnectionJDBCUtil;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BrandRepositoryImpl implements BrandRepository {
    public List<BrandEntity> findAll()
    {
        List<BrandEntity> result = new ArrayList<>();
        String sql = "SELECT * FROM brands";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql))
            {
                while (rs.next()) {
                    BrandEntity brand = new BrandEntity();
                    brand.setBrand_id(rs.getInt("brand_id"));
                    brand.setName(rs.getString("name"));
                    result.add(brand);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}