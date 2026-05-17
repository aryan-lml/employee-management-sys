package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.PasswordUtils;

import java.sql.*;

/**
 * Data-access layer for the users table.
 *
 * Schema is owned outside the DAO (no DDL here). The constructor is a no-op.
 *
 * `must_change_password` is set to 1 when an admin creates an employee login,
 * forcing the employee to choose their own password on first sign-in.
 */
public class UserDao implements IUserDao {

    public UserDao() {
        // Schema is managed manually — DAO performs no DDL.
    }

    // -------- Create --------

    public boolean createUserWithHash(String username, String passwordHash, String role) throws SQLException {
        return createUserWithHash(username, passwordHash, role, false);
    }

    public boolean createUserWithHash(String username, String passwordHash, String role, boolean mustChangePassword) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, must_change_password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            if (passwordHash == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, passwordHash);
            ps.setString(3, role == null ? "USER" : role);
            ps.setInt(4, mustChangePassword ? 1 : 0);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean createUserWithHash(Connection conn, String username, String passwordHash, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            if (passwordHash == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, passwordHash);
            ps.setString(3, role == null ? "USER" : role);
            return ps.executeUpdate() > 0;
        }
    }

    public void createUser(String username, String plainPassword, String role) throws SQLException {
        createUser(username, plainPassword, role, false);
    }

    public void createUser(String username, String plainPassword, String role, boolean mustChangePassword) throws SQLException {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password must be provided and not blank");
        }
        createUserWithHash(username, PasswordUtils.hashPassword(plainPassword), role, mustChangePassword);
    }

    // -------- Read --------

    public boolean validateCredentials(String username, String plainPassword) throws SQLException {
        if (username == null || plainPassword == null) return false;
        User u = findByUsername(username);
        if (u == null || u.getPasswordHash() == null) return false;
        return PasswordUtils.verifyPassword(plainPassword, u.getPasswordHash());
    }

    public User findByUsername(String username) throws SQLException {
        try (Connection conn = DBConfig.getConnection()) {
            return findByUsername(conn, username);
        }
    }

    public User findByUsername(Connection conn, String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    @Override
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? LIMIT 1";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        }
        return null;
    }

    // -------- Lockout --------

    /** Increment failed_attempts; lock account for 15 minutes after 5 failures. */
    public void recordFailedAttempt(String username) throws SQLException {
        String sql = "UPDATE users SET failed_attempts = failed_attempts + 1, " +
                "locked_until = CASE WHEN failed_attempts + 1 >= 5 THEN DATE_ADD(NOW(), INTERVAL 15 MINUTE) ELSE locked_until END " +
                "WHERE username = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

    public void resetFailedAttempts(String username) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET failed_attempts = 0, locked_until = NULL WHERE username = ?")) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

    public boolean isLocked(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ? AND locked_until IS NOT NULL AND locked_until > NOW()";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    // -------- Password reset --------

    public void setResetToken(String username, String token, Timestamp expires) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE users SET reset_token = ?, reset_expires = ? WHERE username = ?")) {
            ps.setString(1, token);
            ps.setTimestamp(2, expires);
            ps.setString(3, username);
            ps.executeUpdate();
        }
    }

    public String findUsernameByValidToken(String token) throws SQLException {
        String sql = "SELECT username FROM users WHERE reset_token = ? AND reset_expires > NOW() LIMIT 1";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString(1); }
        }
        return null;
    }

    /** Persist new password and clear lockout, reset-token and the first-login flag in one statement. */
    public void updatePassword(String username, String newHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, reset_token = NULL, reset_expires = NULL, " +
                     "failed_attempts = 0, locked_until = NULL, must_change_password = 0 WHERE username = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    // -------- mapping helper --------
    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        try { u.setMustChangePassword(rs.getInt("must_change_password") == 1); } catch (SQLException ignore) {}
        return u;
    }
}
