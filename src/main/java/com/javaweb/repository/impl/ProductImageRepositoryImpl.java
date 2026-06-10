package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.javaweb.repository.ProductImageRepository;
import com.javaweb.repository.entity.ProductImageEntity;
import com.javaweb.utils.ConnectionJDBCUtil;

@Repository
public class ProductImageRepositoryImpl implements ProductImageRepository {
    
    @Override
    public List<ProductImageEntity> findByProductId(Integer productId) {
        List<ProductImageEntity> list = new ArrayList<>();
        // Sắp xếp theo sort_order để Frontend biết ảnh nào hiện trước, ảnh nào hiện sau
        String sql = "SELECT * FROM product_images WHERE product_id = ? ORDER BY sort_order ASC";
                     
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, productId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    ProductImageEntity entity = new ProductImageEntity();
                    entity.setImage_id(rs.getInt("image_id"));
                    entity.setProduct_id(rs.getInt("product_id"));
                    entity.setImage_url(rs.getString("image_url"));
                    entity.setIs_thumbnail(rs.getBoolean("is_thumbnail"));
                    entity.setSort_order(rs.getInt("sort_order"));
                    list.add(entity);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}