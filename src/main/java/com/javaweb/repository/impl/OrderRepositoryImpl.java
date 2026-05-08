package com.javaweb.repository.impl;

import com.javaweb.repository.OrderRepository;
import com.javaweb.utils.ConnectionJDBCUtil;
import model.CartItemResponseDTO;
import model.OrderRequestDTO;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
            String getCartDetailsSql = "SELECT ci.product_detail_id, ci.quantity, pd.price, pd.stock_quantity, p.name as product_name, " +
                                       "s.value as size_name, c_color.name as color_name " +
                                       "FROM cart_items ci " +
                                       "JOIN carts c ON ci.cart_id = c.cart_id " +
                                       "JOIN product_details pd ON ci.product_detail_id = pd.product_detail_id " +
                                       "JOIN products p ON pd.product_id = p.product_id " +
                                       "LEFT JOIN sizes s ON pd.size_id = s.size_id " +
                                       "LEFT JOIN colors c_color ON pd.color_id = c_color.color_id " +
                                       "WHERE c.user_id = ?";
            
            List<CartItemResponseDTO> itemsToOrder = new ArrayList<>();
            long totalOrderPrice = 0;

            try (PreparedStatement getDetailsStmt = conn.prepareStatement(getCartDetailsSql)) {
                getDetailsStmt.setInt(1, userId);
                try (ResultSet rs = getDetailsStmt.executeQuery()) {
                    while (rs.next()) {
                        String pName = rs.getString("product_name");
                        String sizeName = rs.getString("size_name");
                        String colorName = rs.getString("color_name");
                        String fullName = pName;
                        if (colorName != null && sizeName != null) {
                            fullName += " - " + colorName + " (Size " + sizeName + ")";
                        } else if (colorName != null) {
                            fullName += " - " + colorName;
                        } else if (sizeName != null) {
                            fullName += " - Size " + sizeName;
                        }

                        if (rs.getInt("quantity") > rs.getInt("stock_quantity")) {
                            throw new RuntimeException("Sản phẩm '" + fullName + "' không đủ số lượng trong kho (còn " + rs.getInt("stock_quantity") + ").");
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
            String createOrderSql = "INSERT INTO orders (user_id, order_date, total_money, discount_money, final_money, shipping_status, payment_status, shipping_address, receiver_name, receiver_phone, payment_method) VALUES (?, NOW(), ?, 0, ?, 'Processing', 'Unpaid', ?, ?, ?, ?)";
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
            // SỬA LỖI SQL BỀN VỮNG: Đổi "=" thành "IN" phòng trường hợp user có nhiều cart rác
            String clearCartSql = "DELETE FROM cart_items WHERE cart_id IN (SELECT cart_id FROM carts WHERE user_id = ?)";
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

    @Override
    public List<Map<String, Object>> getOrdersByStatus(String status) {
        System.out.println(">>> [DEBUG Backend] Đang lấy đơn hàng có trạng thái: " + status);
        List<Map<String, Object>> orders = new ArrayList<>();
        // Ánh xạ trạng thái Frontend sang trạng thái Database Enum
        String dbStatus = status.equals("PENDING") ? "Processing" : (status.equals("SHIPPING") ? "Shipping" : (status.equals("CANCELLED") ? "Cancelled" : (status.equals("COMPLETED") ? "Delivered" : status)));
        
        String sql = "SELECT order_id, receiver_name, receiver_phone, order_date, final_money FROM orders WHERE shipping_status = ?";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dbStatus);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> order = new HashMap<>();
                    order.put("id", rs.getInt("order_id"));
                    order.put("receiverName", rs.getString("receiver_name"));
                    order.put("receiverPhone", rs.getString("receiver_phone"));
                    order.put("date", rs.getTimestamp("order_date"));
                    order.put("total", rs.getLong("final_money"));
                    orders.add(order);
                }
            }
            System.out.println(">>> [DEBUG Backend] Đã tìm thấy " + orders.size() + " đơn hàng.");
        } catch (Exception e) {
            System.out.println(">>> [DEBUG Backend] LỖI CSDL KHI TÌM ĐƠN HÀNG: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public boolean updateOrderStatus(Integer orderId, String status) {
        String dbStatus = status.equals("PENDING") ? "Processing" : (status.equals("SHIPPING") ? "Shipping" : (status.equals("CANCELLED") ? "Cancelled" : (status.equals("COMPLETED") ? "Delivered" : status)));
        
        Connection conn = null;
        try {
            conn = ConnectionJDBCUtil.getConnection();
            conn.setAutoCommit(false); // Đảm bảo an toàn Transaction
            
            // TÍNH NĂNG MỚI: HOÀN TRẢ SỐ LƯỢNG (STOCK) VÀO KHO KHI HỦY ĐƠN
            if ("Cancelled".equals(dbStatus)) {
                String checkSql = "SELECT shipping_status FROM orders WHERE order_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, orderId);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        // Tránh trường hợp spam click khiến hàng được cộng dồn nhiều lần
                        if (rs.next() && "Cancelled".equalsIgnoreCase(rs.getString("shipping_status"))) {
                            return true; 
                        }
                    }
                }
                
                // Thực hiện trả lại kho
                String restoreStockSql = "UPDATE product_details pd " +
                                         "JOIN order_details od ON pd.product_detail_id = od.product_detail_id " +
                                         "SET pd.stock_quantity = pd.stock_quantity + od.quantity " +
                                         "WHERE od.order_id = ?";
                try (PreparedStatement restoreStmt = conn.prepareStatement(restoreStockSql)) {
                    restoreStmt.setInt(1, orderId);
                    restoreStmt.executeUpdate();
                }
            }
            
            // Cập nhật trạng thái đơn hàng
            String sql = "UPDATE orders SET shipping_status = ? WHERE order_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, dbStatus);
                pstmt.setInt(2, orderId);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return true; // Luôn trả về true nếu không có lỗi SQL nào xảy ra
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            throw new RuntimeException(e.getMessage()); // Ném lỗi cho OrderAPI bắt
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    // ==========================================
    // LẤY CHI TIẾT ĐƠN HÀNG
    // ==========================================
    public List<Map<String, Object>> getOrderDetails(Integer orderId) {
        List<Map<String, Object>> details = new ArrayList<>();
        String sql = "SELECT od.quantity, od.unit_price, p.name as product_name, " +
                     "s.value as size_name, c_color.name as color_name, " +
                     "pd.thumbnail_img_url as thumb " +
                     "FROM order_details od " +
                     "JOIN product_details pd ON od.product_detail_id = pd.product_detail_id " +
                     "JOIN products p ON pd.product_id = p.product_id " +
                     "LEFT JOIN sizes s ON pd.size_id = s.size_id " +
                     "LEFT JOIN colors c_color ON pd.color_id = c_color.color_id " +
                     "WHERE od.order_id = ?";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    String fullName = rs.getString("product_name");
                    String sizeName = rs.getString("size_name");
                    String colorName = rs.getString("color_name");
                    
                    if (colorName != null && sizeName != null) fullName += " - " + colorName + " (Size " + sizeName + ")";
                    else if (colorName != null) fullName += " - " + colorName;
                    else if (sizeName != null) fullName += " - Size " + sizeName;
                    
                    item.put("productName", fullName);
                    item.put("quantity", rs.getInt("quantity"));
                    item.put("price", rs.getLong("unit_price"));
                    item.put("thumbnail", rs.getString("thumb"));
                    details.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    // ==========================================
    // LẤY DANH SÁCH ĐƠN HÀNG CỦA MỘT USER CỤ THỂ
    // ==========================================
    public List<Map<String, Object>> getOrdersByUser(Integer userId) {
        List<Map<String, Object>> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.receiver_name, o.receiver_phone, o.order_date, o.final_money, o.shipping_status, o.payment_method, " +
                     "(SELECT SUM(quantity) FROM order_details WHERE order_id = o.order_id) as items_count " +
                     "FROM orders o WHERE o.user_id = ? ORDER BY o.order_date DESC";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> order = new HashMap<>();
                    order.put("id", rs.getInt("order_id"));
                    order.put("receiverName", rs.getString("receiver_name"));
                    order.put("receiverPhone", rs.getString("receiver_phone"));
                    order.put("date", rs.getTimestamp("order_date"));
                    order.put("total", rs.getLong("final_money"));
                    
                    // Quy đổi phương thức thanh toán
                    String paymentMethod = rs.getString("payment_method");
                    if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
                        order.put("methodName", "Thanh toán VNPay");
                    } else if ("MOMO".equalsIgnoreCase(paymentMethod)) {
                        order.put("methodName", "Thanh toán MoMo");
                    } else if ("BANKING".equalsIgnoreCase(paymentMethod)) {
                        order.put("methodName", "Chuyển khoản");
                    } else {
                        order.put("methodName", "Thanh toán COD");
                    }
                    
                    order.put("itemsCount", rs.getInt("items_count") == 0 ? 1 : rs.getInt("items_count"));

                    // Quy đổi trạng thái DB sang trạng thái giao diện HTML (PENDING, SHIPPING, COMPLETED, CANCELLED)
                    String dbStatus = rs.getString("shipping_status");
                    String status = "PENDING";
                    if ("Shipping".equalsIgnoreCase(dbStatus)) status = "SHIPPING";
                    else if ("Delivered".equalsIgnoreCase(dbStatus)) status = "COMPLETED";
                    else if ("Cancelled".equalsIgnoreCase(dbStatus)) status = "CANCELLED";
                    
                    order.put("status", status);
                    orders.add(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    // ==========================================
    // THỐNG KÊ DOANH THU & DASHBOARD (DÀNH CHO ADMIN)
    // ==========================================
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Lấy Tổng doanh thu và Tổng số đơn hàng (Bỏ qua đơn đã Hủy)
        String sqlOrder = "SELECT COUNT(order_id) as total_orders, SUM(final_money) as total_revenue FROM orders WHERE shipping_status != 'Cancelled'";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlOrder);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                stats.put("totalOrders", rs.getInt("total_orders"));
                stats.put("totalRevenue", rs.getLong("total_revenue"));
            }
        } catch (Exception e) { e.printStackTrace(); }

        // Lấy Tổng số lượng khách hàng (role_id = 2)
        String sqlUser = "SELECT COUNT(user_id) as total_users FROM users WHERE role_id = 2";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlUser);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                stats.put("totalUsers", rs.getInt("total_users"));
            }
        } catch (Exception e) { e.printStackTrace(); }

        return stats;
    }

    public List<Map<String, Object>> getMonthlyRevenue(int year) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT MONTH(order_date) as month, SUM(final_money) as revenue FROM orders WHERE YEAR(order_date) = ? AND shipping_status != 'Cancelled' GROUP BY MONTH(order_date) ORDER BY MONTH(order_date)";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", rs.getInt("month"));
                    map.put("revenue", rs.getLong("revenue"));
                    list.add(map);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}