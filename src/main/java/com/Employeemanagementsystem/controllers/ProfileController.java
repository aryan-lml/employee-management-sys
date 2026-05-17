package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.UserDao;
import com.Employeemanagementsystem.model.Employee;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;
import com.Employeemanagementsystem.util.PasswordUtils;
import com.Employeemanagementsystem.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Self-service profile and password management.
 *
 * Works for both admins (no employee row) and employees (full record).
 * Admins get the password-change form only.
 */
@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }
        try {
            req.setAttribute("employee", employeeDao.getEmployeeByEmail(user.getUsername()));
            req.getRequestDispatcher("/pages/profile.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }

        String action = req.getParameter("action");
        try {
            if ("updateProfile".equalsIgnoreCase(action)) {
                Employee emp = employeeDao.getEmployeeByEmail(user.getUsername());
                if (emp != null) {
                    String phone = trim(req.getParameter("phone"));
                    String name = trim(req.getParameter("name"));
                    if (phone != null && !phone.isBlank() && !ValidationUtils.isValidPhone(phone)) {
                        req.setAttribute("error", "Phone number must contain 7-15 digits.");
                    } else if (name != null && name.matches(".*\\d.*")) {
                        req.setAttribute("error", "Full name must not contain numbers.");
                    } else {
                        if (name != null && !name.isBlank()) emp.setName(name);
                        emp.setPhone(phone);
                        employeeDao.updateEmployee(emp);
                        req.setAttribute("infoMessage", "Profile updated.");
                    }
                }
            } else if ("changePassword".equalsIgnoreCase(action)) {
                String current = req.getParameter("currentPassword");
                String next    = req.getParameter("newPassword");
                String confirm = req.getParameter("confirmPassword");
                if (current == null || next == null || confirm == null
                        || !next.equals(confirm) || next.length() < 6) {
                    req.setAttribute("error", "New password must be at least 6 characters and match the confirmation.");
                } else if (!userDao.validateCredentials(user.getUsername(), current)) {
                    req.setAttribute("error", "Current password is incorrect.");
                } else {
                    userDao.updatePassword(user.getUsername(), PasswordUtils.hashPassword(next));
                    req.setAttribute("infoMessage", "Password changed.");
                }
            }
            req.setAttribute("employee", employeeDao.getEmployeeByEmail(user.getUsername()));
            req.getRequestDispatcher("/pages/profile.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }

    private String trim(String s) { return s == null ? null : s.trim(); }
}
