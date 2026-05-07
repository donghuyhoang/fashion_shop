package com.javaweb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

public class ConnectionJDBCUtil {
    // Đọc thông tin từ file application.properties
    private static final ResourceBundle bundle = ResourceBundle.getBundle("application");
    
    private static final String URL = bundle.getString("spring.datasource.url");
    private static final String USER = bundle.getString("spring.datasource.username");
    private static final String PASS = bundle.getString("spring.datasource.password");

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }
}
