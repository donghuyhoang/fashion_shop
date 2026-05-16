package com.javaweb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
public class ConnectionJDBCUtil {
	private static String DB_URL;
	private static String USER;
	private static String PASS;
	static {
		try (InputStream input = ConnectionJDBCUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
			Properties prop = new Properties();
			if (input != null) {
				prop.load(input);
				DB_URL = prop.getProperty("spring.datasource.url");
				USER = prop.getProperty("spring.datasource.username");
				PASS = prop.getProperty("spring.datasource.password");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
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
