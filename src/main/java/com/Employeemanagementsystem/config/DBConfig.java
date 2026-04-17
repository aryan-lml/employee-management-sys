package com.Employeemanagementsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBConfig provides a single place to get a JDBC Connection.
 * Default connection values can be overridden using system properties:
 * - db.url
 * - db.user
 * - db.password
 */
public class DBConfig {

    // Default values -- change these or override via system properties or environment when running
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/ems?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static boolean DRIVER_AVAILABLE = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DRIVER_AVAILABLE = true;
        } catch (ClassNotFoundException e) {
            // If driver is missing, callers will get SQLException when trying to connect.
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getProperty("db.url", System.getenv().getOrDefault("DB_URL", DEFAULT_URL));
        String user = System.getProperty("db.user", System.getenv().getOrDefault("DB_USER", DEFAULT_USER));
        String password = System.getProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASSWORD));

        if (!DRIVER_AVAILABLE) {
            throw new SQLException("MySQL JDBC driver not found. Add the MySQL Connector/J JAR to WEB-INF/lib or Tomcat lib.");
        }

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            String msg = ex.getMessage() == null ? "" : ex.getMessage();
            // If database does not exist, try to create it automatically (development convenience)
            if (msg.contains("Unknown database") || msg.toLowerCase().contains("unknown database")) {
                // try to connect without database and create it
                try {
                    String baseUrl = url.replaceAll("(jdbc:mysql://[^/]+)(/[^?]+)(.*)", "$1");
                    try (Connection conn = DriverManager.getConnection(baseUrl, user, password);
                         Statement st = conn.createStatement()) {
                        st.executeUpdate("CREATE DATABASE IF NOT EXISTS ems CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                    }
                    // try again
                    return DriverManager.getConnection(url, user, password);
                } catch (SQLException ex2) {
                    // return original exception to caller but attach helpful suggestion
                    throw new SQLException("Failed to create database 'ems'. Original: " + ex.getMessage() + "; Attempt: " + ex2.getMessage(), ex2);
                }
            }
            throw ex;
        }
    }
}

