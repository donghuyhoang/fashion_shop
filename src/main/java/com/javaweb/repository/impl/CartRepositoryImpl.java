package com.javaweb.repository.impl;

import model.CartItemResponseDTO;
import com.javaweb.repository.CartRepository;
import com.javaweb.utils.ConnectionJDBCUtil;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CartRepositoryImpl implements CartRepository {

    @Override
    public void addToCart(Integer userId, Integer productDetailId, Integer quantity) {
        String findCartSql = "SELECT cart_id FROM carts WHERE user_id = ?";
        String createCartSql = "INSERT INTO carts (user_id, created_at) VALUES (?, NOW())";
        String checkItemSql = "SELECT cart_item_id, quantity FROM cart_items WHERE cart_id = ? AND product_detail_id = ?";
        String updateItemSql = "UPDATE cart_items SET quantity = quantity + ? WHERE cart_item_id = ?";
        String insertItemSql = "INSERT INTO cart_items (cart_id, product_detail_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionJDBCUtil.getConnection()) {
            conn.setAutoCommit(false); 
            try {
                Integer cartId = null;
                // 1. Tìm giỏ hàng của user
                try (PreparedStatement findCartStmt = conn.prepareStatement(findCartSql)) {
                    findCartStmt.setInt(1, userId);
                    try (ResultSet rs = findCartStmt.executeQuery()) {
                        if (rs.next()) cartId = rs.getInt("cart_id"); 
                    }
                }

                // 2. Nếu chưa có thì tạo mới
                if (cartId == null) {
                    try (PreparedStatement createCartStmt = conn.prepareStatement(createCartSql, Statement.RETURN_GENERATED_KEYS)) {
                        createCartStmt.setInt(1, userId);
                        createCartStmt.executeUpdate();
                        try (ResultSet keys = createCartStmt.getGeneratedKeys()) {
                            if (keys.next()) cartId = keys.getInt(1);
                            else throw new RuntimeException("Tạo giỏ hàng thất bại.");
                        }
                    }
                }

                // 3. Kiểm tra sản phẩm đã có trong giỏ chưa
                try (PreparedStatement checkItemStmt = conn.prepareStatement(checkItemSql)) {
                    checkItemStmt.setInt(1, cartId); 
                    checkItemStmt.setInt(2, productDetailId);
                    try (ResultSet rs = checkItemStmt.executeQuery()) {
                        if (rs.next()) {
                            // Đã có -> Cộng dồn số lượng
                            int cartItemId = rs.getInt("cart_item_id");
                            try (PreparedStatement updateItemStmt = conn.prepareStatement(updateItemSql)) {
                                updateItemStmt.setInt(1, quantity);
                                updateItemStmt.setInt(2, cartItemId);
                                updateItemStmt.executeUpdate();
                            }
                        } else {
                            // Chưa có -> Thêm mới vào chi tiết giỏ
                            try (PreparedStatement insertItemStmt = conn.prepareStatement(insertItemSql)) {
                                insertItemStmt.setInt(1, cartId);
                                insertItemStmt.setInt(2, productDetailId);
                                insertItemStmt.setInt(3, quantity);
                                insertItemStmt.executeUpdate();
                            }
                        }
                    }
                }
                conn.commit(); 
            } catch (SQLException e) {
                conn.rollback(); 
                e.printStackTrace(); // Nên sử dụng logger chuyên dụng
                throw new RuntimeException("Lỗi xử lý giỏ hàng: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Nên sử dụng logger chuyên dụng
            throw new RuntimeException("Lỗi kết nối Database: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer getCartCount(Integer userId) {
        String sql = "SELECT SUM(ci.quantity) FROM cart_items ci JOIN carts c ON ci.cart_id = c.cart_id WHERE c.user_id = ?";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Nên sử dụng một logger chuyên dụng ở đây
        }
        return 0;
    }

    @Override
    public List<CartItemResponseDTO> getCartDetails(Integer userId) {
        List<CartItemResponseDTO> list = new ArrayList<>();
        String sql = "SELECT pd.product_detail_id, p.name AS product_name, p.price, ci.quantity, " +
                     "s.value AS size_name, c_color.name AS color_name, " + 
                     "pd.thumbnail_img_url AS thumb " +  
                     "FROM cart_items ci " +
                     "JOIN carts c ON ci.cart_id = c.cart_id " +
                     "JOIN product_details pd ON ci.product_detail_id = pd.product_detail_id " +
                     "JOIN products p ON pd.product_id = p.product_id " +
                     "LEFT JOIN sizes s ON pd.size_id = s.size_id " +
                     "LEFT JOIN colors c_color ON pd.color_id = c_color.color_id " +
                     "WHERE c.user_id = ?";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CartItemResponseDTO dto = new CartItemResponseDTO();
                    dto.setProductDetailId(rs.getInt("product_detail_id"));
                    dto.setProductName(rs.getString("product_name")); 
                    dto.setPrice(rs.getLong("price"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setSizeName(rs.getString("size_name"));
                    dto.setColorName(rs.getString("color_name"));
                    dto.setThumbnail(rs.getString("thumb"));
                    dto.setTotalPrice(dto.getPrice() * dto.getQuantity());
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Nên sử dụng một logger chuyên dụng ở đây
        }
        return list;
    }

    @Override
    public void removeCartItem(Integer userId, Integer productDetailId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = (SELECT cart_id FROM carts WHERE user_id = ?) AND product_detail_id = ?";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productDetailId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa sản phẩm khỏi giỏ hàng: " + e.getMessage(), e);
        }
    }
}