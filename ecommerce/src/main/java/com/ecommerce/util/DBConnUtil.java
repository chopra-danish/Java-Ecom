package com.ecommerce.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnUtil {
    
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String host = PropertyUtil.getPropertyString("db.host");
                String port = PropertyUtil.getPropertyString("db.port");
                String dbName = PropertyUtil.getPropertyString("db.name");
                String username = PropertyUtil.getPropertyString("db.username");
                String password = PropertyUtil.getPropertyString("db.password");
                
                // Forming the connection URL
                String connectionString = String.format("jdbc:mysql://%s:%s/%s", host, port, dbName);
                
                // Establishing the connection
                connection = DriverManager.getConnection(connectionString, username, password);
                
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Database connection failed: " + e.getMessage());
            }
        }
        return connection;
    }
}
