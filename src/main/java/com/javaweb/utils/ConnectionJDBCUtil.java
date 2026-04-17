package com.javaweb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionJDBCUtil {
	static final String DB_URL = "jdbc:mysql://localhost:3306/fashion_shop";
	static final String USER = "root";
	static final String PASS = "123456";
	public static Connection getConnection() {
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			return conn;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
