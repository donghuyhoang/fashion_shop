package com.javaweb.repository.impl;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.javaweb.repository.RoleRepository;
import com.javaweb.utils.ConnectionJDBCUtil;
import java.sql.PreparedStatement;

import com.javaweb.repository.entity.RoleEntity;

@Repository
public class RoleRepositoryImpl implements RoleRepository
{
    @Override
    public List<RoleEntity> findAll()
    {
        List<RoleEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM roles";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql))
            {
                while(rs.next())
                {
                    RoleEntity entities = new RoleEntity();
                    entities.setRole_id(rs.getInt("role_id"));
                    entities.setRole_name(rs.getString("role_name"));
                    list.add(entities);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        return list;
    }

    @Override
    public RoleEntity findById(Integer id) {
        RoleEntity role = null;
        String sql = "SELECT * FROM roles WHERE role_id = ?";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    role = new RoleEntity();
                    role.setRole_id(rs.getInt("role_id"));
                    role.setRole_name(rs.getString("role_name"));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }    
}
