package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence layer for the notifications table.
 * Notifications are surfaced on the employee dashboard and in the topbar bell.
 */
public class NotificationDao {

    public NotificationDao() {
        // Schema is managed manually — DAO performs no DDL.
    }

    public boolean create(int employeeId, String message, String link) throws SQLException {
        String sql = "INSERT INTO notifications (employee_id, message, link) VALUES (?, ?, ?)";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setString(2, message);
            if (link == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, link);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Notification> forEmployee(int employeeId, int limit) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE employee_id = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int unreadCount(int employeeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE employee_id = ? AND is_read = 0";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        return 0;
    }

    public boolean markAllRead(int employeeId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = 1 WHERE employee_id = ? AND is_read = 0";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() >= 0;
        }
    }

    private Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setEmployeeId(rs.getInt("employee_id"));
        n.setMessage(rs.getString("message"));
        n.setLink(rs.getString("link"));
        n.setRead(rs.getInt("is_read") == 1);
        n.setCreatedAt(rs.getTimestamp("created_at"));
        return n;
    }
}
