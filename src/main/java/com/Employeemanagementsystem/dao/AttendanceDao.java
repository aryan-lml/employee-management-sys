package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.model.Attendance;
import com.Employeemanagementsystem.model.AttendanceStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDao implements IAttendanceDao {

    public AttendanceDao() {
        try (Connection conn = DBConfig.getConnection(); Statement st = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS attendance (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  employee_id INT NOT NULL,
                  check_in DATETIME NOT NULL,
                  check_out DATETIME DEFAULT NULL,
                  status VARCHAR(20) DEFAULT 'PRESENT',
                  worked_hours DECIMAL(6,2) DEFAULT NULL,
                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;
            st.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("[AttendanceDao] Could not ensure attendance table exists: " + e.getMessage());
        }
    }

    @Override
    public Attendance getTodayAttendanceForEmployee(int employeeId) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE employee_id = ? AND DATE(check_in) = CURDATE() LIMIT 1";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public boolean checkIn(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendance (employee_id, check_in, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, attendance.getEmployeeId());
            ps.setTimestamp(2, attendance.getCheckIn());
            ps.setString(3, attendance.getStatus() == null ? AttendanceStatus.PRESENT.name() : attendance.getStatus().name());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) attendance.setId(keys.getInt(1)); }
            return true;
        }
    }

    @Override
    public boolean checkOut(int attendanceId, Timestamp checkOut, double workedHours, String status) throws SQLException {
        String sql = "UPDATE attendance SET check_out = ?, worked_hours = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, checkOut);
            ps.setDouble(2, workedHours);
            ps.setString(3, status);
            ps.setInt(4, attendanceId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    @Override
    public List<Attendance> getAttendanceForEmployee(int employeeId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE employee_id = ? ORDER BY check_in DESC";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Attendance> getAllAttendance() throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance ORDER BY check_in DESC";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setEmployeeId(rs.getInt("employee_id"));
        a.setCheckIn(rs.getTimestamp("check_in"));
        a.setCheckOut(rs.getTimestamp("check_out"));
        String s = rs.getString("status");
        try { a.setStatus(AttendanceStatus.valueOf(s)); } catch (Exception ex) { /* ignore */ }
        a.setWorkedHours(rs.getDouble("worked_hours"));
        return a;
    }
}
