package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.javaweb.repository.ProductDetailRepository;
import com.javaweb.repository.entity.ProductDetailEntity;
import com.javaweb.utils.ConnectionJDBCUtil;

@Repository
public class ProductDetailRepositoryImpl implements ProductDetailRepository {
    
    @Override
    public List<ProductDetailEntity> findByProductId(Integer productId) {
        List<ProductDetailEntity> list = new ArrayList<>();
        
        // Ekdam simple query
        String sql = "SELECT * FROM product_details WHERE product_id = ?";
                     
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, productId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    ProductDetailEntity entity = new ProductDetailEntity();
                    entity.setProduct_detail_id(rs.getInt("product_detail_id"));
                    entity.setProduct_id(rs.getInt("product_id"));
                    
                    // Direct ID melavo
                    entity.setSize_id(rs.getInt("size_id"));
                    entity.setColor_id(rs.getInt("color_id"));
                    
                    entity.setStock_quantity(rs.getInt("stock_quantity"));
                    entity.setPrice(rs.getInt("price"));
                    entity.setThumbnail_img_url(rs.getString("thumbnail_img_url"));
                    list.add(entity);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}