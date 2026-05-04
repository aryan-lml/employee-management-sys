package dao;

import model.Attendance;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public void markAttendance(Attendance a) throws SQLException {
        String sql = "INSERT INTO attendance (employee_id, status, check_in) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, a.getEmployeeId());
            ps.setString(2, a.getStatus());
            ps.setTimestamp(3, Timestamp.valueOf(a.getCheckIn()));
            ps.executeUpdate();
        }
    }

    public void checkOut(int attendanceId) throws SQLException {
        String sql = "UPDATE attendance SET check_out = ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, attendanceId);
            ps.executeUpdate();
        }
    }

    public List<Attendance> getAttendanceByEmployee(int employeeId) throws SQLException {
        String sql = "SELECT id, employee_id, status, check_in, check_out, created_at FROM attendance WHERE employee_id = ? ORDER BY created_at DESC";
        List<Attendance> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Attendance> getTodayAttendance() throws SQLException {
        String sql = "SELECT id, employee_id, status, check_in, check_out, created_at FROM attendance WHERE DATE(check_in) = ? ORDER BY created_at DESC";
        List<Attendance> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int todayAttendanceCount() throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM attendance WHERE DATE(check_in) = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        }
        return 0;
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setEmployeeId(rs.getInt("employee_id"));
        a.setStatus(rs.getString("status"));
        Timestamp ci = rs.getTimestamp("check_in");
        if (ci != null) a.setCheckIn(ci.toLocalDateTime());
        Timestamp co = rs.getTimestamp("check_out");
        if (co != null) a.setCheckOut(co.toLocalDateTime());
        return a;
    }
}
