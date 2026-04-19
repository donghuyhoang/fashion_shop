package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.javaweb.repository.ProductReviewRepository;
import com.javaweb.repository.entity.ProductReviewEntity;
import com.javaweb.utils.ConnectionJDBCUtil;

@Repository
public class ProductReviewRepositoryImpl implements ProductReviewRepository {
    
    @Override
    public List<ProductReviewEntity> findByProductId(Integer productId) {
        List<ProductReviewEntity> list = new ArrayList<>();
        // JOIN với bảng users để lấy tên người bình luận, sắp xếp đánh giá mới nhất lên đầu
        String sql = "SELECT pr.*, u.full_name " +
                     "FROM product_reviews pr " +
                     "JOIN users u ON pr.user_id = u.user_id " +
                     "WHERE pr.product_id = ? " +
                     "ORDER BY pr.created_at DESC";
                     
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, productId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    ProductReviewEntity entity = new ProductReviewEntity();
                    entity.setReview_id(rs.getInt("review_id"));
                    entity.setUser_id(rs.getInt("user_id"));
                    entity.setUser_full_name(rs.getString("full_name"));
                    entity.setProduct_id(rs.getInt("product_id"));
                    entity.setOrder_id(rs.getInt("order_id"));
                    entity.setRating(rs.getInt("rating"));
                    entity.setComment(rs.getString("comment"));
                    entity.setCreated_at(rs.getTimestamp("created_at"));
                    list.add(entity);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}