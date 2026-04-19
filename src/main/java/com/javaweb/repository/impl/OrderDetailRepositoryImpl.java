package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.javaweb.repository.OrderDetailRepository;
import com.javaweb.repository.entity.OrderDetailEntity;
import com.javaweb.utils.ConnectionJDBCUtil;

@Repository
public class OrderDetailRepositoryImpl implements OrderDetailRepository {
    
    @Override
    public List<OrderDetailEntity> findByOrderId(Integer orderId) {
        List<OrderDetailEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id = ?";
                     
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, orderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    OrderDetailEntity entity = new OrderDetailEntity();
                    entity.setOrder_detail_id(rs.getInt("order_detail_id"));
                    entity.setOrder_id(rs.getInt("order_id"));
                    entity.setProduct_detail_id(rs.getInt("product_detail_id"));
                    entity.setQuantity(rs.getInt("quantity"));
                    entity.setUnit_price(rs.getInt("unit_price"));
                    entity.setTotal_price(rs.getInt("total_price"));
                    list.add(entity);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}