package dao;

import model.Employee;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> getAllEmployees() throws SQLException {
        String sql = "SELECT id, employee_code, name, email, department, is_active FROM employees ORDER BY name";
        List<Employee> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Employee e = mapRow(rs);
                list.add(e);
            }
        }
        return list;
    }

    public Employee getEmployeeById(int id) throws SQLException {
        String sql = "SELECT id, employee_code, name, email, department, is_active FROM employees WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        }
    }

    public void addEmployee(Employee e) throws SQLException {
        String sql = "INSERT INTO employees (employee_code, name, email, department, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getEmployeeCode());
            ps.setString(2, e.getName());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getDepartment());
            ps.setBoolean(5, e.isStatus());
            ps.executeUpdate();
        }
    }

    public void updateEmployee(Employee e) throws SQLException {
        String sql = "UPDATE employees SET employee_code = ?, name = ?, email = ?, department = ?, is_active = ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getEmployeeCode());
            ps.setString(2, e.getName());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getDepartment());
            ps.setBoolean(5, e.isStatus());
            ps.setInt(6, e.getId());
            ps.executeUpdate();
        }
    }

    public void deleteEmployee(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setEmployeeCode(rs.getString("employee_code"));
        e.setName(rs.getString("name"));
        e.setEmail(rs.getString("email"));
        e.setDepartment(rs.getString("department"));
        e.setStatus(rs.getBoolean("is_active"));
        return e;
    }

    // Dashboard stats
    public int totalEmployees() throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM employees";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("cnt");
            return 0;
        }
    }

    public int activeEmployees() throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM employees WHERE is_active = TRUE";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("cnt");
            return 0;
        }
    }
}
