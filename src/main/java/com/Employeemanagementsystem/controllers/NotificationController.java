package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.NotificationDao;
import com.Employeemanagementsystem.model.Employee;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Marks notifications read and redirects to the optional link target.
 * Single endpoint keeps the JSP markup simple.
 */
@WebServlet("/notifications")
public class NotificationController extends HttpServlet {

    private final NotificationDao notificationDao = new NotificationDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }
        try {
            Employee emp = employeeDao.getEmployeeByEmail(user.getUsername());
            if (emp != null) notificationDao.markAllRead(emp.getId());
            String to = req.getParameter("to");
            if (to != null && !to.isBlank()) resp.sendRedirect(req.getContextPath() + to);
            else resp.sendRedirect(req.getContextPath() + "/employee-dashboard");
        } catch (SQLException e) { throw new ServletException(e); }
    }
}
