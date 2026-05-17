package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.AttendanceDao;
import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.NotificationDao;
import com.Employeemanagementsystem.dao.TaskDao;
import com.Employeemanagementsystem.model.Attendance;
import com.Employeemanagementsystem.model.Employee;
import com.Employeemanagementsystem.model.Notification;
import com.Employeemanagementsystem.model.Task;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Personalized landing page for employees:
 * own tasks, attendance state and recent notifications.
 */
@WebServlet("/employee-dashboard")
public class EmployeeDashboardController extends HttpServlet {

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final TaskDao taskDao = new TaskDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final NotificationDao notificationDao = new NotificationDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }
        // Admins land on the admin dashboard, not here.
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
        }

        try {
            Employee emp = employeeDao.getEmployeeByEmail(user.getUsername());
            if (emp == null) {
                req.setAttribute("errorMessage", "No employee record is linked to your account. Please contact the administrator.");
                req.getRequestDispatcher("/pages/employee-dashboard.jsp").forward(req, resp);
                return;
            }
            List<Task> tasks = taskDao.findByEmployee(emp.getId());
            List<Attendance> attendance = attendanceDao.getAttendanceForEmployee(emp.getId());
            List<Notification> notifications = notificationDao.forEmployee(emp.getId(), 10);
            Attendance today = attendanceDao.getTodayAttendanceForEmployee(emp.getId());

            req.setAttribute("employee", emp);
            req.setAttribute("tasks", tasks);
            req.setAttribute("attendanceList", attendance);
            req.setAttribute("notifications", notifications);
            req.setAttribute("todayAttendance", today);
            req.getRequestDispatcher("/pages/employee-dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
