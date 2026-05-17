package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.TaskDao;
import com.Employeemanagementsystem.model.Employee;
import com.Employeemanagementsystem.model.Task;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin analytics page, scoped to the current admin's data.
 */
@WebServlet("/analytics")
public class AnalyticsController extends HttpServlet {

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final TaskDao taskDao = new TaskDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = AuthUtils.getUser(req.getSession());
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
        }
        try {
            List<Employee> employees = employeeDao.getAllEmployees(admin.getId(), null, null, null);
            List<Task> tasks = taskDao.findByCreator(admin.getId());

            // Status distribution
            Map<String, Integer> taskCounts = new HashMap<>();
            for (Task t : tasks) {
                String s = t.getStatus() == null ? "PENDING" : t.getStatus().name();
                taskCounts.merge(s, 1, Integer::sum);
            }

            // Department distribution
            Map<String, Integer> deptCounts = new HashMap<>();
            for (Employee e : employees) {
                String d = e.getDepartment() == null || e.getDepartment().isBlank() ? "Unassigned" : e.getDepartment();
                deptCounts.merge(d, 1, Integer::sum);
            }

            // Attendance status distribution for owned employees
            Map<String, Integer> attendanceByStatus = new HashMap<>();
            String sql = "SELECT a.status, COUNT(*) c FROM attendance a JOIN employees e ON e.id = a.employee_id " +
                         "WHERE e.owner_admin_id = ? GROUP BY a.status";
            int attendanceTotal = 0;
            try (Connection c = DBConfig.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, admin.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String s = rs.getString("status"); if (s == null) s = "PRESENT";
                        int cnt = rs.getInt("c");
                        attendanceByStatus.put(s, cnt);
                        attendanceTotal += cnt;
                    }
                }
            }

            req.setAttribute("totalEmployees", employees.size());
            req.setAttribute("totalTasks", tasks.size());
            req.setAttribute("attendanceCount", attendanceTotal);
            req.setAttribute("taskCounts", taskCounts);
            req.setAttribute("deptCounts", deptCounts);
            req.setAttribute("attendanceByStatus", attendanceByStatus);
            req.getRequestDispatcher("/pages/analytics.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }
}
