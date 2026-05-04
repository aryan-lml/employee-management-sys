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

@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    private final EmployeeDao employeeDao = new EmployeeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/pages/login.jsp");
            return;
        }

        try {
            Employee emp = employeeDao.getEmployeeByEmail(user.getUsername());
            req.setAttribute("employee", emp);
            req.getRequestDispatcher("/pages/profile.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

}
