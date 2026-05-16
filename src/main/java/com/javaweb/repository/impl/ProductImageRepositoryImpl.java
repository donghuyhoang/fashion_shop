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

    @Override
    public void saveAll(Integer productId, List<String> imageUrls) {
        String sql = "INSERT INTO product_images (product_id, image_url, is_thumbnail, sort_order) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int order = 1;
            for (String url : imageUrls) {
                pstmt.setInt(1, productId);
                pstmt.setString(2, url);
                pstmt.setBoolean(3, order == 1); // True nếu là ảnh đầu tiên (ảnh bìa)
                pstmt.setInt(4, order);
                pstmt.addBatch(); // Gom lệnh lại để chạy 1 lần cho tối ưu
                order++;
            }
            pstmt.executeBatch();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void deleteByProductId(Integer productId) {
        String sql = "DELETE FROM product_images WHERE product_id = ?";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}