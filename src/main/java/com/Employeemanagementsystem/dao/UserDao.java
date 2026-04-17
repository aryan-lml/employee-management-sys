package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.model.User;

import java.sql.*;
import com.Employeemanagementsystem.util.PasswordUtils;

public class UserDao {

    public static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS users (
          id INT AUTO_INCREMENT PRIMARY KEY,
          username VARCHAR(255) NOT NULL UNIQUE,
          password_hash VARCHAR(255) DEFAULT NULL,
          role VARCHAR(50) NOT NULL DEFAULT 'user',
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """;

    public UserDao() {
        // ensure users table exists
        try (Connection conn = DBConfig.getConnection(); Statement st = conn.createStatement()) {
            String sql = CREATE_TABLE_SQL.trim();
            if (sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1);
            st.executeUpdate(sql);
            // Ensure password_hash column accepts NULL (in case an older schema made it NOT NULL)
            try {
                st.executeUpdate("ALTER TABLE users MODIFY password_hash VARCHAR(255) DEFAULT NULL;");
            } catch (SQLException ignore) {
                // ignore errors (e.g., insufficient privileges) but continue
            }
        } catch (SQLException e) {
            System.err.println("[UserDao] Could not ensure users table exists: " + e.getMessage());
        }
    }

    /**
     * Create a user when password is already hashed.
     */
    public boolean createUserWithHash(String username, String passwordHash, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            if (passwordHash == null) ps.setNull(2, Types.VARCHAR);
            else ps.setString(2, passwordHash);
            ps.setString(3, role == null ? "user" : role);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    /**
     * Convenience: create a user from a plain password (will be hashed using BCrypt).
     * Throws IllegalArgumentException if password is null/blank to ensure password_hash is always set.
     */
    public void createUser(String username, String plainPassword, String role) throws SQLException {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password must be provided and not blank");
        }
        String hashed = PasswordUtils.hashPassword(plainPassword);
        createUserWithHash(username, hashed, role);
    }

    /** Validate username/password credentials using BCrypt. */
    public boolean validateCredentials(String username, String plainPassword) throws SQLException {
        if (username == null || plainPassword == null) return false;
        User u = findByUsername(username);
        if (u == null || u.getPasswordHash() == null) return false;
        return PasswordUtils.verifyPassword(plainPassword, u.getPasswordHash());
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRole(rs.getString("role"));
                    u.setCreatedAt(rs.getTimestamp("created_at"));
                    return u;
                }
            }
        }
        return null;
    }
}
