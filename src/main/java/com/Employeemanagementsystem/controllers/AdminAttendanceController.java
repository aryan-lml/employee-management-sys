package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.model.Employee;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin attendance management, scoped to the current admin's employees.
 *
 * The query joins {@code attendance} ↔ {@code employees} and filters
 * {@code employees.owner_admin_id = ?} so a given admin only sees and
 * mutates attendance for employees they own.
 */
@WebServlet("/admin-attendance")
public class AdminAttendanceController extends HttpServlet {

    private final EmployeeDao employeeDao = new EmployeeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = AuthUtils.getUser(req.getSession());
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
        }
        try {
            String empIdParam = req.getParameter("employeeId");
            String from = req.getParameter("from");
            String to = req.getParameter("to");

            List<Employee> employees = employeeDao.getAllEmployees(admin.getId(), null, null, null);
            List<Row> rows = query(admin.getId(), empIdParam, from, to);
            req.setAttribute("employees", employees);
            req.setAttribute("rows", rows);
            req.setAttribute("fEmployeeId", empIdParam);
            req.setAttribute("fFrom", from);
            req.setAttribute("fTo", to);
            req.getRequestDispatcher("/pages/admin-attendance.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = AuthUtils.getUser(req.getSession());
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
        }
        String action = req.getParameter("action");
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            // Ownership check via the join: bail if this admin doesn't own that employee.
            if (!ownsAttendance(admin.getId(), id)) {
                resp.sendRedirect(req.getContextPath() + "/admin-attendance"); return;
            }
            if ("update".equalsIgnoreCase(action)) {
                String status = req.getParameter("status");
                String note = req.getParameter("note");
                try (Connection c = DBConfig.getConnection();
                     PreparedStatement ps = c.prepareStatement("UPDATE attendance SET status = ?, note = ? WHERE id = ?")) {
                    ps.setString(1, status);
                    ps.setString(2, note);
                    ps.setInt(3, id);
                    ps.executeUpdate();
                }
            } else if ("delete".equalsIgnoreCase(action)) {
                try (Connection c = DBConfig.getConnection();
                     PreparedStatement ps = c.prepareStatement("DELETE FROM attendance WHERE id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin-attendance");
        } catch (SQLException e) { throw new ServletException(e); }
    }

    // ---- queries ----
    private boolean ownsAttendance(int adminId, int attendanceId) throws SQLException {
        String sql = "SELECT 1 FROM attendance a JOIN employees e ON a.employee_id = e.id " +
                     "WHERE a.id = ? AND e.owner_admin_id = ? LIMIT 1";
        try (Connection c = DBConfig.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            ps.setInt(2, adminId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private List<Row> query(int adminId, String empIdParam, String from, String to) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT a.id, a.employee_id, e.name AS emp_name, a.check_in, a.check_out, " +
            "a.worked_hours, a.status, a.note " +
            "FROM attendance a JOIN employees e ON e.id = a.employee_id " +
            "WHERE e.owner_admin_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(adminId);
        if (empIdParam != null && !empIdParam.isBlank()) {
            sql.append(" AND a.employee_id = ?"); params.add(Integer.parseInt(empIdParam));
        }
        if (from != null && !from.isBlank()) { sql.append(" AND DATE(a.check_in) >= ?"); params.add(from); }
        if (to   != null && !to.isBlank())   { sql.append(" AND DATE(a.check_in) <= ?"); params.add(to); }
        sql.append(" ORDER BY a.check_in DESC LIMIT 500");

        List<Row> rows = new ArrayList<>();
        try (Connection c = DBConfig.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Row r = new Row();
                    r.id = rs.getInt("id");
                    r.employeeId = rs.getInt("employee_id");
                    r.employeeName = rs.getString("emp_name");
                    r.checkIn = rs.getTimestamp("check_in");
                    r.checkOut = rs.getTimestamp("check_out");
                    r.workedHours = rs.getObject("worked_hours") == null ? null : rs.getDouble("worked_hours");
                    r.status = rs.getString("status");
                    r.note = rs.getString("note");
                    rows.add(r);
                }
            }
        }
        return rows;
    }

    /** Flat row exposed to the JSP. */
    public static class Row {
        public int id;
        public int employeeId;
        public String employeeName;
        public Timestamp checkIn;
        public Timestamp checkOut;
        public Double workedHours;
        public String status;
        public String note;

        public int getId() { return id; }
        public int getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public Timestamp getCheckIn() { return checkIn; }
        public Timestamp getCheckOut() { return checkOut; }
        public Double getWorkedHours() { return workedHours; }
        public String getStatus() { return status; }
        public String getNote() { return note; }
    }
}
