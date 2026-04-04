package com.javaweb.repository.impl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.stereotype.Repository;
import com.javaweb.repository.UserRepository;
import com.javaweb.repository.entity.UserEntity;
import com.javaweb.utils.ConnectionJDBCUtil;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public UserEntity findByEmailAndPassword(String email, String password_hash)
    {
        String sql = "SELECT * FROM users WHERE email = ? AND password_hash = ?";
        // Implementation for finding user by email and password
        UserEntity user = null;

        try (Connection conn = ConnectionJDBCUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql))
                {
                    pstmt.setString(1,email);
                    pstmt.setString(2,password_hash);

                    try (ResultSet rs = pstmt.executeQuery())
                    {
                        if(rs.next())
                        {
                            user = new UserEntity();
                            user.setUser_id(rs.getInt("user_id"));
                            user.setFullname(rs.getString("full_name"));
                            user.setEmail(rs.getString("email"));
                            user.setRole_id(rs.getInt("role_id"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return user;
    }

    @Override
    public boolean checkEmailExists(String email) 
    {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try(Connection conn = ConnectionJDBCUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql))
            {
                pstmt.setString(1, email);
                try(ResultSet rs = pstmt.executeQuery())
                {
                    if(rs.next() && rs.getInt(1) > 0)
                    {
                        return true;
                    }
                }
            } catch(Exception e)
            {
                e.printStackTrace();
            }
            return false;
    }
    
    @Override
    public boolean register(UserEntity user) {
        String sql = "INSERT INTO users (full_name, email, phone, password_hash, role_id, is_active, created_at) VALUES (?, ?, ?, ?, ?, 1, NOW())";
        try (Connection conn = ConnectionJDBCUtil.getConnection()) {
            // Tắt auto commit để quản lý transaction
            conn.setAutoCommit(false); 
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getFullname());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPhone());
                pstmt.setString(4, user.getPassword_hash());
                pstmt.setInt(5, user.getRole_id());
                
                int rowsAffected = pstmt.executeUpdate();
                conn.commit(); // CHỐT DỮ LIỆU VÀO DB
                
                return rowsAffected > 0; // Trả về true nếu insert thành công
            } catch (Exception e) {
                conn.rollback(); // Nếu có lỗi thì hoàn tác
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Trả về false nếu thất bại
    }
}
