package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-backed employee data access.
 *
 * Every employee row is owned by exactly one admin (owner_admin_id).
 * The DAO refuses to leak data across admins by always filtering on the
 * ownerAdminId argument the controller supplies. Use {@code null} for
 * ownerAdminId to perform an unscoped query (only used for the seeded
 * admin's view, internal lookups by email, or migration utilities).
 */
public class EmployeeDao implements IEmployeeDao {

    public EmployeeDao() {
        // Schema is managed manually — DAO performs no DDL.
    }

    @Override
    public List<Employee> getAllEmployees(Integer ownerAdminId, String search, String department, String status)
            throws SQLException {
        List<Employee> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM employees WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (ownerAdminId != null) { sql.append(" AND owner_admin_id = ?"); params.add(ownerAdminId); }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR email LIKE ?)");
            String p = "%" + search.trim() + "%";
            params.add(p); params.add(p);
        }
        if (department != null && !department.trim().isEmpty()) {
            sql.append(" AND department = ?"); params.add(department.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?"); params.add(status.trim());
        }
        sql.append(" ORDER BY id DESC");

        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public Employee getEmployeeById(int id, Integer ownerAdminId) throws SQLException {
        String sql = ownerAdminId == null
                ? "SELECT * FROM employees WHERE id = ?"
                : "SELECT * FROM employees WHERE id = ? AND owner_admin_id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ownerAdminId != null) ps.setInt(2, ownerAdminId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapRow(rs); }
        }
        return null;
    }

    @Override
    public Employee getEmployeeByEmail(String email) throws SQLException {
        if (email == null || email.isBlank()) return null;
        String sql = "SELECT * FROM employees WHERE email = ? LIMIT 1";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapRow(rs); }
        }
        return null;
    }

    @Override
    public Employee getEmployeeByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM employees WHERE user_id = ? LIMIT 1";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapRow(rs); }
        }
        return null;
    }

    @Override
    public boolean addEmployee(Employee e) throws SQLException {
        String sql = "INSERT INTO employees (name, email, phone, department, role, salary, user_id, owner_admin_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindEmployee(ps, e);
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getInt(1));
            }
            return true;
        }
    }

    @Override
    public boolean updateEmployee(Employee e) throws SQLException {
        String sql = "UPDATE employees SET name = ?, email = ?, phone = ?, department = ?, role = ?, salary = ?, " +
                     "user_id = ?, owner_admin_id = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bindEmployee(ps, e);
            ps.setInt(10, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteEmployee(int id, Integer ownerAdminId) throws SQLException {
        String sql = ownerAdminId == null
                ? "DELETE FROM employees WHERE id = ?"
                : "DELETE FROM employees WHERE id = ? AND owner_admin_id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ownerAdminId != null) ps.setInt(2, ownerAdminId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Transactional variant used by EmployeeService — caller owns the Connection. */
    public boolean addEmployee(Connection conn, Employee e) throws SQLException {
        String sql = "INSERT INTO employees (name, email, phone, department, role, salary, user_id, owner_admin_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindEmployee(ps, e);
            if (ps.executeUpdate() == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) e.setId(keys.getInt(1)); }
            return true;
        }
    }

    /** Link an Employee row to its user account after the account is created. */
    public void linkUserId(int employeeId, int userId) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE employees SET user_id = ? WHERE id = ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, employeeId);
            ps.executeUpdate();
        }
    }

    // ---- helpers ----
    private void bindEmployee(PreparedStatement ps, Employee e) throws SQLException {
        ps.setString(1, e.getName());
        ps.setString(2, e.getEmail());
        ps.setString(3, e.getPhone());
        ps.setString(4, e.getDepartment());
        ps.setString(5, e.getRole());
        ps.setDouble(6, e.getSalary());
        if (e.getUserId() == null) ps.setNull(7, Types.INTEGER); else ps.setInt(7, e.getUserId());
        if (e.getOwnerAdminId() == null) ps.setNull(8, Types.INTEGER); else ps.setInt(8, e.getOwnerAdminId());
        ps.setString(9, e.getStatus());
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
        try {
            int uid = rs.getInt("user_id");
            if (!rs.wasNull()) e.setUserId(uid);
        } catch (SQLException ignore) {}
        try {
            int oid = rs.getInt("owner_admin_id");
            if (!rs.wasNull()) e.setOwnerAdminId(oid);
        } catch (SQLException ignore) {}
        e.setStatus(rs.getString("status"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        e.setUpdatedAt(rs.getTimestamp("updated_at"));
        return e;
    }
}
