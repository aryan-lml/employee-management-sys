package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {

    public EmployeeDao() {
        // Ensure the employees table exists. Fail silently but print a helpful message to server logs.
        try (Connection conn = DBConfig.getConnection(); Statement st = conn.createStatement()) {
            String sql = CREATE_TABLE_SQL.trim();
            if (sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1);
            st.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("[EmployeeDao] Could not ensure employees table exists: " + e.getMessage());
        }
    }

    // Create table SQL (for reference)
    public static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS employees (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            phone VARCHAR(50),
            department VARCHAR(100),
            role VARCHAR(100),
            salary DOUBLE,
            status VARCHAR(20),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        ) ENGINE=InnoDB;
        """;

    public List<Employee> getAllEmployees(String search, String department, String status) throws SQLException {
        List<Employee> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM employees WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR email LIKE ?)");
            String p = "%" + search.trim() + "%";
            params.add(p);
            params.add(p);
        }
        if (department != null && !department.trim().isEmpty()) {
            sql.append(" AND department = ?");
            params.add(department.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status.trim());
        }
        sql.append(" ORDER BY id DESC");

        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee e = mapRow(rs);
                    list.add(e);
                }
            }
        }

        return list;
    }

    public Employee getEmployeeById(int id) throws SQLException {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Employee getEmployeeByEmail(String email) throws SQLException {
        if (email == null || email.isBlank()) return null;
        String sql = "SELECT * FROM employees WHERE email = ? LIMIT 1";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean addEmployee(Employee e) throws SQLException {
        String sql = "INSERT INTO employees (name, email, phone, department, role, salary, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getDepartment());
            ps.setString(5, e.getRole());
            ps.setDouble(6, e.getSalary());
            ps.setString(7, e.getStatus());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return true;
        }
    }

    public boolean updateEmployee(Employee e) throws SQLException {
        String sql = "UPDATE employees SET name = ?, email = ?, phone = ?, department = ?, role = ?, salary = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getDepartment());
            ps.setString(5, e.getRole());
            ps.setDouble(6, e.getSalary());
            ps.setString(7, e.getStatus());
            ps.setInt(8, e.getId());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean deleteEmployee(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setName(rs.getString("name"));
        e.setEmail(rs.getString("email"));
        e.setPhone(rs.getString("phone"));
        e.setDepartment(rs.getString("department"));
        e.setRole(rs.getString("role"));
        e.setSalary(rs.getDouble("salary"));
        e.setStatus(rs.getString("status"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        e.setUpdatedAt(rs.getTimestamp("updated_at"));
        return e;
    }
}
