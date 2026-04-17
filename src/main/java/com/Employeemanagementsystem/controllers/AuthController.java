package com.Employeemanagementsystem.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import com.Employeemanagementsystem.dao.UserDao;
import com.Employeemanagementsystem.model.User;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/auth")
public class AuthController extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        super.init();
        userDao = new UserDao();
        // ensure default admin exists
        try {
            User admin = userDao.findByUsername("admin");
            if (admin == null) {
                userDao.createUser("admin", "admin", "admin");
            }
        } catch (Exception e) {
            throw new ServletException("Failed to initialize user store: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("logout".equals(action)) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/pages/login.jsp");
            return;
        }
        // default: show login page
        resp.sendRedirect(req.getContextPath() + "/pages/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "login";

        if ("register".equals(action)) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                req.setAttribute("error", "Username and password are required.");
                req.getRequestDispatcher("/pages/register.jsp").forward(req, resp);
                return;
            }
                try {
                    User existing = userDao.findByUsername(username);
                    if (existing != null) {
                        req.setAttribute("error", "User already exists.");
                        req.getRequestDispatcher("/pages/register.jsp").forward(req, resp);
                        return;
                    }
                    // create user and hash password inside DAO
                    userDao.createUser(username, password, "user");
                    req.getSession().setAttribute("user", username);
                    if ("admin".equalsIgnoreCase(username)) resp.sendRedirect(req.getContextPath() + "/employees");
                    else resp.sendRedirect(req.getContextPath() + "/employee");
                    return;
                } catch (SQLException se) {
                    throw new ServletException(se);
                }
        }

        // login
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if (username == null || password == null) {
            req.setAttribute("error", "Invalid credentials.");
            req.getRequestDispatcher("/pages/login.jsp").forward(req, resp);
            return;
        }
        try {
            boolean ok = userDao.validateCredentials(username, password);
            if (ok) {
                User found = userDao.findByUsername(username);
                req.getSession().setAttribute("user", username);
                if (found != null && "admin".equalsIgnoreCase(found.getRole())) resp.sendRedirect(req.getContextPath() + "/employees");
                else resp.sendRedirect(req.getContextPath() + "/employee");
                return;
            } else {
                req.setAttribute("error", "Invalid username or password.");
                req.getRequestDispatcher("/pages/login.jsp").forward(req, resp);
                return;
            }
        } catch (SQLException se) {
            throw new ServletException(se);
        }
    }
}
