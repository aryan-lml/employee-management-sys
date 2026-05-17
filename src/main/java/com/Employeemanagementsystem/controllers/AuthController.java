package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.UserDao;
import com.Employeemanagementsystem.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Authentication endpoint.
 *
 * Business rules (per product spec):
 *   - Anyone who registers becomes an ADMIN with their own multi-tenant scope.
 *   - Employees never self-register; an admin adds them and the credentials
 *     are issued to them by the admin (a USER account is created with role=USER).
 *   - Login is open to both ADMINs (→ /dashboard) and USERs (→ /employee-dashboard).
 *   - Five failed attempts → 15-minute lockout.
 */
@WebServlet("/auth")
public class AuthController extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        super.init();
        userDao = new UserDao();
        // No seed user — the first person to register becomes the first admin.
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("logout".equals(req.getParameter("action"))) req.getSession().invalidate();
        resp.sendRedirect(req.getContextPath() + "/pages/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "login";
        try {
            if ("register".equals(action)) handleRegister(req, resp);
            else handleLogin(req, resp);
        } catch (SQLException se) {
            throw new ServletException(se);
        }
    }

    // -------- Registration (always creates ADMIN) --------
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String username = trim(req.getParameter("username"));
        String password = req.getParameter("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            failTo(req, resp, "/pages/register.jsp", "Username and password are required.");
            return;
        }
        if (username.length() < 3) {
            failTo(req, resp, "/pages/register.jsp", "Username must be at least 3 characters.");
            return;
        }
        if (password.length() < 6) {
            failTo(req, resp, "/pages/register.jsp", "Password must be at least 6 characters.");
            return;
        }
        if (userDao.findByUsername(username) != null) {
            failTo(req, resp, "/pages/register.jsp", "An account with that username already exists.");
            return;
        }
        // Always ADMIN per spec
        userDao.createUser(username, password, "ADMIN");
        User created = userDao.findByUsername(username);
        req.getSession().setAttribute("userObj", created);
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }

    // -------- Login --------
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String username = trim(req.getParameter("username"));
        String password = req.getParameter("password");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            failTo(req, resp, "/pages/login.jsp", "Username and password are required.");
            return;
        }
        if (userDao.isLocked(username)) {
            failTo(req, resp, "/pages/login.jsp", "Account locked after repeated failures. Try again in 15 minutes.");
            return;
        }
        if (userDao.validateCredentials(username, password)) {
            User found = userDao.findByUsername(username);
            userDao.resetFailedAttempts(username);
            req.getSession().setAttribute("userObj", found);
            // First-login flow: admin issued the password, user must pick their own.
            if (found.isMustChangePassword()) {
                resp.sendRedirect(req.getContextPath() + "/force-password-change");
                return;
            }
            String target = "ADMIN".equalsIgnoreCase(found.getRole()) ? "/dashboard" : "/employee-dashboard";
            resp.sendRedirect(req.getContextPath() + target);
            return;
        }
        if (userDao.findByUsername(username) != null) userDao.recordFailedAttempt(username);
        failTo(req, resp, "/pages/login.jsp", "Invalid username or password.");
    }

    private void failTo(HttpServletRequest req, HttpServletResponse resp, String page, String msg)
            throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.getRequestDispatcher(page).forward(req, resp);
    }

    private String trim(String s) { return s == null ? null : s.trim(); }
}
