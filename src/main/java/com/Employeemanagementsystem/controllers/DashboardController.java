package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.TaskDao;
import com.Employeemanagementsystem.model.Employee;
import com.Employeemanagementsystem.model.Task;
import com.Employeemanagementsystem.model.TaskStatus;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.List;

/**
 * Admin landing dashboard. KPIs are scoped to the current admin.
 * Employees logged in here are redirected to /employee-dashboard.
 */
@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final TaskDao taskDao = new TaskDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/employee-dashboard"); return;
        }
        try {
            List<Employee> employees = employeeDao.getAllEmployees(user.getId(), null, null, null);
            List<Task> tasks = taskDao.findByCreator(user.getId());

            int active = (int) employees.stream().filter(e -> "Active".equalsIgnoreCase(e.getStatus())).count();
            int pending = (int) tasks.stream().filter(t -> t.getStatus() == TaskStatus.PENDING).count();
            int inProgress = (int) tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
            int completed = (int) tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
            int attendanceCount = scopedAttendanceCount(user.getId());

            req.setAttribute("totalEmployees", employees.size());
            req.setAttribute("activeEmployees", active);
            req.setAttribute("totalTasks", tasks.size());
            req.setAttribute("pendingTasks", pending);
            req.setAttribute("inProgressTasks", inProgress);
            req.setAttribute("completedTasks", completed);
            req.setAttribute("attendanceCount", attendanceCount);

            // Last 5 recent tasks for the activity feed.
            req.setAttribute("recentTasks", tasks.subList(0, Math.min(tasks.size(), 5)));
            req.getRequestDispatcher("/pages/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }

    /** Count of attendance rows for employees that the current admin owns. */
    private int scopedAttendanceCount(int adminId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM attendance a JOIN employees e ON e.id = a.employee_id " +
                     "WHERE e.owner_admin_id = ?";
        try (Connection c = DBConfig.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        return 0;
    }
}
