package com.javaweb.repository.impl;

import com.javaweb.repository.OrderRepository;
import com.javaweb.utils.ConnectionJDBCUtil;
import model.CartItemResponseDTO;
import model.OrderRequestDTO;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public Integer createOrderFromCart(OrderRequestDTO dto) {
        Connection conn = null;
        Integer userId = dto.getUserId();
        try {
            conn = ConnectionJDBCUtil.getConnection();
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            // 1. Lấy tất cả sản phẩm trong giỏ và kiểm tra tồn kho
            String getCartDetailsSql = "SELECT ci.product_detail_id, ci.quantity, pd.price, pd.stock_quantity, p.name as product_name " +
                                       "FROM cart_items ci " +
                                       "JOIN carts c ON ci.cart_id = c.cart_id " +
                                       "JOIN product_details pd ON ci.product_detail_id = pd.product_detail_id " +
                                       "JOIN products p ON pd.product_id = p.product_id " +
                                       "WHERE c.user_id = ?";
            
            List<CartItemResponseDTO> itemsToOrder = new ArrayList<>();
            long totalOrderPrice = 0;

            try (PreparedStatement getDetailsStmt = conn.prepareStatement(getCartDetailsSql)) {
                getDetailsStmt.setInt(1, userId);
                try (ResultSet rs = getDetailsStmt.executeQuery()) {
                    while (rs.next()) {
                        if (rs.getInt("quantity") > rs.getInt("stock_quantity")) {
                            throw new RuntimeException("Sản phẩm '" + rs.getString("product_name") + "' không đủ số lượng trong kho (còn " + rs.getInt("stock_quantity") + ").");
                        }
                        CartItemResponseDTO item = new CartItemResponseDTO();
                        item.setProductDetailId(rs.getInt("product_detail_id"));
                        item.setQuantity(rs.getInt("quantity"));
                        item.setPrice(rs.getLong("price"));
                        itemsToOrder.add(item);
                        totalOrderPrice += item.getPrice() * item.getQuantity();
                    }
                }
            }
            
            if (itemsToOrder.isEmpty()) {
                throw new RuntimeException("Giỏ hàng của bạn đang trống.");
            }

            // 2. Tạo một đơn hàng mới trong bảng `orders`
            String createOrderSql = "INSERT INTO orders (user_id, order_date, total_money, discount_money, final_money, shipping_status, payment_status, shipping_address, receiver_name, receiver_phone, payment_method) VALUES (?, NOW(), ?, 0, ?, '1', '1', ?, ?, ?, ?)";
            int orderId;
            try (PreparedStatement createOrderStmt = conn.prepareStatement(createOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                createOrderStmt.setInt(1, userId);
                createOrderStmt.setLong(2, totalOrderPrice);
                createOrderStmt.setLong(3, totalOrderPrice + 35000); // final_money: Tổng tiền sản phẩm + 35k phí ship
                createOrderStmt.setString(4, dto.getShippingAddress());
                createOrderStmt.setString(5, dto.getReceiverName());
                createOrderStmt.setString(6, dto.getReceiverPhone());
                createOrderStmt.setString(7, dto.getPaymentMethod());
                createOrderStmt.executeUpdate();
                try (ResultSet generatedKeys = createOrderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Tạo đơn hàng thất bại, không lấy được ID.");
                    }
                }
            }

            // 3. Chuyển sản phẩm từ giỏ hàng sang chi tiết đơn hàng và cập nhật kho (dùng Batch cho hiệu năng cao)
            String insertDetailSql = "INSERT INTO order_details (order_id, product_detail_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            String updateStockSql = "UPDATE product_details SET stock_quantity = stock_quantity - ? WHERE product_detail_id = ?";
            
            try (PreparedStatement insertDetailStmt = conn.prepareStatement(insertDetailSql);
                 PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSql)) {

                for (CartItemResponseDTO item : itemsToOrder) {
                    insertDetailStmt.setInt(1, orderId);
                    insertDetailStmt.setInt(2, item.getProductDetailId());
                    insertDetailStmt.setInt(3, item.getQuantity());
                    insertDetailStmt.setLong(4, item.getPrice());
                    insertDetailStmt.addBatch();

                    updateStockStmt.setInt(1, item.getQuantity());
                    updateStockStmt.setInt(2, item.getProductDetailId());
                    updateStockStmt.addBatch();
                }
                insertDetailStmt.executeBatch();
                updateStockStmt.executeBatch();
            }

            // 4. Xóa sạch giỏ hàng
            String clearCartSql = "DELETE FROM cart_items WHERE cart_id = (SELECT cart_id FROM carts WHERE user_id = ?)";
            try (PreparedStatement clearCartStmt = conn.prepareStatement(clearCartSql)) {
                clearCartStmt.setInt(1, userId);
                clearCartStmt.executeUpdate();
            }

            conn.commit(); // LƯU TẤT CẢ THAY ĐỔI NẾU MỌI THỨ THÀNH CÔNG

            return orderId; // Trả về ID đơn hàng để Frontend hiển thị

        } catch (SQLException | RuntimeException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException(e.getMessage(), e); // Ném lỗi ra để API bắt được
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Trả lại trạng thái cho connection pool
                    conn.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}