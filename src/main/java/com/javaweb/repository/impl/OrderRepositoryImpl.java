package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.javaweb.repository.OrderRepository;
import com.javaweb.repository.entity.OrderEntity;
import com.javaweb.utils.ConnectionJDBCUtil;
import model.OrderRequestDTO;
import model.CartItemDTO;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    @Override
    public List<OrderEntity> findByUserId(Integer userId) {
        List<OrderEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
                     
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    OrderEntity entity = new OrderEntity();
                    entity.setOrder_id(rs.getInt("order_id"));
                    entity.setUser_id(rs.getInt("user_id"));
                    entity.setOrder_date(rs.getTimestamp("order_date"));
                    entity.setTotal_money(rs.getInt("total_money"));
                    entity.setDiscount_money(rs.getInt("discount_money"));
                    entity.setFinal_money(rs.getInt("final_money"));
                    entity.setShipping_address(rs.getString("shipping_address"));
                    entity.setReceiver_name(rs.getString("receiver_name"));
                    entity.setReceiver_phone(rs.getString("receiver_phone"));
                    entity.setPayment_method(rs.getString("payment_method"));
                    entity.setPayment_status(rs.getString("payment_status"));
                    entity.setShipping_status(rs.getString("shipping_status"));
                    list.add(entity);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    @Override
    public boolean createOrder(OrderRequestDTO orderRequest) {
        String sqlOrder = "INSERT INTO orders (user_id, order_date, total_money, final_money, shipping_address, receiver_name, receiver_phone, payment_method, payment_status, shipping_status) VALUES (?, NOW(), ?, ?, ?, ?, ?, ?, 'PENDING', 'PENDING')";
        String sqlOrderDetail = "INSERT INTO order_details (order_id, product_detail_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE product_details SET stock_quantity = stock_quantity - ? WHERE product_detail_id = ? AND stock_quantity >= ?";

        try (Connection conn = ConnectionJDBCUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                pstmtOrder.setInt(1, orderRequest.getUserId());
                pstmtOrder.setInt(2, orderRequest.getTotalMoney());
                pstmtOrder.setInt(3, orderRequest.getTotalMoney());
                pstmtOrder.setString(4, orderRequest.getShippingAddress());
                pstmtOrder.setString(5, orderRequest.getReceiverName());
                pstmtOrder.setString(6, orderRequest.getReceiverPhone());
                pstmtOrder.setString(7, orderRequest.getPaymentMethod());
                
                pstmtOrder.executeUpdate();
                
                ResultSet rs = pstmtOrder.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);
                    
                    try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlOrderDetail);
                         PreparedStatement pstmtStock = conn.prepareStatement(sqlUpdateStock)) {
                        
                        for (CartItemDTO item : orderRequest.getItems()) {
                            pstmtStock.setInt(1, item.getQuantity());
                            pstmtStock.setInt(2, item.getProductDetailId());
                            pstmtStock.setInt(3, item.getQuantity());
                            int updatedRows = pstmtStock.executeUpdate();
                            
                            if (updatedRows == 0) {
                                conn.rollback();
                                return false;
                            }

                            pstmtDetail.setInt(1, orderId);
                            pstmtDetail.setInt(2, item.getProductDetailId());
                            pstmtDetail.setInt(3, item.getQuantity());
                            pstmtDetail.setInt(4, item.getUnitPrice());
                            pstmtDetail.setInt(5, item.getQuantity() * item.getUnitPrice());
                            pstmtDetail.executeUpdate();
                        }
                    }
                } else {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}