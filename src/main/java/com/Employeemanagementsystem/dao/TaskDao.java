package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.model.Task;
import com.Employeemanagementsystem.model.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence for the tasks table.
 * Multi-tenancy: tasks belong to the admin in {@code created_by}.
 */
public class TaskDao implements ITaskDao {

    public TaskDao() {
        // Schema is managed manually — DAO performs no DDL.
    }

    @Override
    public Task createTask(Task t) throws SQLException {
        String sql = "INSERT INTO tasks (title, description, assigned_to, status, due_date, created_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTitle());
            ps.setString(2, t.getDescription());
            if (t.getAssignedTo() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, t.getAssignedTo());
            ps.setString(4, t.getStatus() == null ? TaskStatus.PENDING.name() : t.getStatus().name());
            if (t.getDueDate() == null) ps.setNull(5, Types.DATE); else ps.setDate(5, t.getDueDate());
            if (t.getCreatedBy() == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, t.getCreatedBy());
            if (ps.executeUpdate() == 0) return null;
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) t.setId(keys.getInt(1)); }
            return t;
        }
    }

    @Override
    public boolean updateTask(Task t) throws SQLException {
        String sql = "UPDATE tasks SET title = ?, description = ?, assigned_to = ?, status = ?, due_date = ? WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTitle());
            ps.setString(2, t.getDescription());
            if (t.getAssignedTo() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, t.getAssignedTo());
            ps.setString(4, t.getStatus() == null ? TaskStatus.PENDING.name() : t.getStatus().name());
            if (t.getDueDate() == null) ps.setNull(5, Types.DATE); else ps.setDate(5, t.getDueDate());
            ps.setInt(6, t.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteTask(int id, Integer ownerAdminId) throws SQLException {
        String sql = ownerAdminId == null
                ? "DELETE FROM tasks WHERE id = ?"
                : "DELETE FROM tasks WHERE id = ? AND created_by = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ownerAdminId != null) ps.setInt(2, ownerAdminId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Task findById(int id) throws SQLException {
        return findById(id, null);
    }

    @Override
    public Task findById(int id, Integer ownerAdminId) throws SQLException {
        String sql = ownerAdminId == null
                ? "SELECT * FROM tasks WHERE id = ? LIMIT 1"
                : "SELECT * FROM tasks WHERE id = ? AND created_by = ? LIMIT 1";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ownerAdminId != null) ps.setInt(2, ownerAdminId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapRow(rs); }
        }
        return null;
    }

    @Override
    public List<Task> findByEmployee(int employeeId) throws SQLException {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE assigned_to = ? ORDER BY due_date ASC";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapRow(rs)); }
        }
        return list;
    }

    @Override
    public List<Task> findByCreator(int adminId) throws SQLException {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE created_by = ? ORDER BY due_date ASC";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapRow(rs)); }
        }
        return list;
    }

    @Override
    public List<Task> findAll() throws SQLException {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY due_date ASC";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapRow(rs)); }
        }
        return list;
    }

    private Task mapRow(ResultSet rs) throws SQLException {
        Task t = new Task();
        t.setId(rs.getInt("id"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        int aid = rs.getInt("assigned_to"); if (!rs.wasNull()) t.setAssignedTo(aid);
        String s = rs.getString("status"); try { t.setStatus(TaskStatus.valueOf(s)); } catch (Exception ex) {}
        t.setDueDate(rs.getDate("due_date"));
        int cb = rs.getInt("created_by"); if (!rs.wasNull()) t.setCreatedBy(cb);
        return t;
    }
}
