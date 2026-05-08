package com.javaweb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionJDBCUtil {
	private static final String DB_URL = "jdbc:mysql://fashion_db:3306/fashion_shop?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASS = "rootpassword";

	public static Connection getConnection() {
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			return conn;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}