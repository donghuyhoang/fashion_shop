package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.javaweb.repository.OrderRepository;
import com.javaweb.repository.entity.OrderEntity;
import com.javaweb.utils.ConnectionJDBCUtil;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    @Override
    public List<OrderEntity> findByUserId(Integer userId) {
        List<OrderEntity> list = new ArrayList<>();
        // Sắp xếp đơn hàng mới nhất lên đầu (DESC)
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
}