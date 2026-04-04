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
}
