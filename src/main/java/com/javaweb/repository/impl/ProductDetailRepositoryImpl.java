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
//            e.printStackTrace();
        	throw new RuntimeException("Da xay ra loi khi truy van chi tiet san pham voi ID:" + productId,e);
        }
        return list;
    }
    @Override
    public void addProductDetail(model.ProductDetailDTO dto) {
        String checkSql = "SELECT product_detail_id, stock_quantity, price, thumbnail_img_url FROM product_details WHERE product_id = ? AND size_id = ? AND color_id = ?";
        
        try (java.sql.Connection conn = com.javaweb.utils.ConnectionJDBCUtil.getConnection();
             java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            Integer existingId = null;
            int currentStock = 0;
            Integer oldPrice = null;
            String oldThumb = null;

            checkStmt.setInt(1, dto.getProductId());
            // Lưu ý: Sử dụng setObject thay cho setInt để tránh lỗi NullPointerException khi size/color trống
            checkStmt.setObject(2, dto.getSizeId());
            checkStmt.setObject(3, dto.getColorId());
            try (java.sql.ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    existingId = rs.getInt("product_detail_id");
                    currentStock = rs.getInt("stock_quantity");
                    Object priceObj = rs.getObject("price");
                    if (priceObj != null) {
                        oldPrice = ((Number) priceObj).intValue();
                    }
                    oldThumb = rs.getString("thumbnail_img_url");
                }
            }
            
            // [SỬA LỖI Ở ĐÂY] - Bóc tách chuỗi ||| để chỉ lấy duy nhất 1 ảnh làm Thumbnail
            String firstImage = null;
            if (dto.getThumbnailUrl() != null && !dto.getThumbnailUrl().trim().isEmpty()) {
                String separator = dto.getThumbnailUrl().contains("|||") ? "\\|\\|\\|" : ";";
                String[] images = dto.getThumbnailUrl().split(separator);
                if (images.length > 0) {
                    firstImage = images[0].trim();
                    // Khóa an toàn cuối cùng: Nếu link đầu tiên vẫn lỡ dài hơn 255 ký tự thì cắt bớt cho khỏi sập DB
                    if (firstImage.length() > 255) {
                        firstImage = firstImage.substring(0, 255); 
                    }
                }
            }

            if (existingId != null) {
                // CẬP NHẬT (UPDATE)
                String updateSql = "UPDATE product_details SET stock_quantity = ?, price = ?, thumbnail_img_url = ? WHERE product_detail_id = ?";
                try (java.sql.PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, currentStock + (dto.getStockQuantity() != null ? dto.getStockQuantity() : 0));
                    
                    // Nếu giá mới trống, giữ nguyên giá cũ
                    updateStmt.setObject(2, dto.getPrice() != null ? dto.getPrice() : (oldPrice != null ? oldPrice : 0));
                    
                    // Nhét biến firstImage đã được lọc cẩn thận vào DB
                    updateStmt.setString(3, firstImage != null ? firstImage : oldThumb);
                    
                    updateStmt.setInt(4, existingId);
                    updateStmt.executeUpdate();
                }
            } else {
                // THÊM MỚI (INSERT)
                String insertSql = "INSERT INTO product_details (product_id, size_id, color_id, price, stock_quantity, thumbnail_img_url) VALUES (?, ?, ?, ?, ?, ?)";
                try (java.sql.PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, dto.getProductId());
                    insertStmt.setObject(2, dto.getSizeId());
                    insertStmt.setObject(3, dto.getColorId());
                    insertStmt.setObject(4, dto.getPrice() != null ? dto.getPrice() : 0);
                    insertStmt.setInt(5, dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
                    
                    // Nhét biến firstImage đã được lọc cẩn thận vào DB
                    insertStmt.setString(6, firstImage);
                    insertStmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi logic UPSERT tại ProductDetail: " + e.getMessage());
        }
    }
}