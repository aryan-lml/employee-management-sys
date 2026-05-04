package com.Employeemanagementsystem.controllers;

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
import java.sql.SQLException;

@WebServlet("/employee")
public class EmployeeViewController extends HttpServlet {

    private EmployeeDao employeeDao = new EmployeeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = AuthUtils.getUser(req.getSession());
        if (sessionUser == null) {
            resp.sendRedirect(req.getContextPath() + "/pages/login.jsp");
            return;
        }
        // Admin users should use admin dashboard
        if (AuthUtils.isAdmin(req.getSession())) {
            resp.sendRedirect(req.getContextPath() + "/employees");
            return;
        }

        try {
            // Try to find employee by email matching username
            Employee emp = employeeDao.getEmployeeByEmail(sessionUser.getUsername());
            req.setAttribute("employee", emp);
            req.getRequestDispatcher("/pages/employee.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/pages/error.jsp").forward(req, resp);
        }
    }
}
