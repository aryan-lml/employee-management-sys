package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.TaskDao;
import com.Employeemanagementsystem.dao.AttendanceDao;
import com.Employeemanagementsystem.model.Task;
import com.Employeemanagementsystem.model.Attendance;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/analytics")
public class AnalyticsController extends HttpServlet {

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final TaskDao taskDao = new TaskDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) { resp.sendRedirect(req.getContextPath() + "/dashboard"); return; }

        try {
            int totalEmployees = employeeDao.getAllEmployees(null, null, null).size();
            List<Task> tasks = taskDao.findAll();
            Map<String, Integer> taskCounts = new HashMap<>();
            for (Task t : tasks) {
                String s = t.getStatus() == null ? "PENDING" : t.getStatus().name();
                taskCounts.put(s, taskCounts.getOrDefault(s, 0) + 1);
            }
            List<Attendance> allAttendance = attendanceDao.getAllAttendance();
            req.setAttribute("totalEmployees", totalEmployees);
            req.setAttribute("taskCounts", taskCounts);
            req.setAttribute("attendanceCount", allAttendance == null ? 0 : allAttendance.size());
            req.getRequestDispatcher("/pages/analytics.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }
}
