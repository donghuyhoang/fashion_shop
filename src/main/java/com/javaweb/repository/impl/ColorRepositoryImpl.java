package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.javaweb.repository.ColorRepository;
import com.javaweb.repository.entity.ColorEntity;
import com.javaweb.utils.ConnectionJDBCUtil;
import java.util.ArrayList;


@Repository
public class ColorRepositoryImpl implements ColorRepository
{
    @Override
    public List<ColorEntity> findAll() 
    {
        List<ColorEntity> colors = new ArrayList<>();
        String sql = "SELECT * FROM colors";
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ColorEntity color = new ColorEntity();
                color.setColor_id(rs.getInt("color_id"));
                color.setName(rs.getString("name"));
                colors.add(color);
            }
        } catch (SQLException e) {
            e.printStackTrace();
    }
        return colors;
    }
}
