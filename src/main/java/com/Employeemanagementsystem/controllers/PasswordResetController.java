package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.UserDao;
import com.Employeemanagementsystem.util.PasswordUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;

/**
 * Minimal password-reset flow.
 * - /forgot-password (POST): issues a token good for 30 minutes and displays it to the user.
 *   In production this would be e-mailed; here we render it on screen for the demo.
 * - /reset-password (GET/POST): validates the token and lets the user pick a new password.
 */
@WebServlet({"/forgot-password", "/reset-password"})
public class PasswordResetController extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private static final SecureRandom RNG = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/reset-password".equals(path)) {
            req.setAttribute("token", req.getParameter("token"));
            req.getRequestDispatcher("/pages/reset-password.jsp").forward(req, resp);
            return;
        }
        req.getRequestDispatcher("/pages/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            if ("/forgot-password".equals(path)) {
                String username = req.getParameter("username");
                if (username == null || username.isBlank()) {
                    fail(req, resp, "/pages/forgot-password.jsp", "Please enter your username.");
                    return;
                }
                if (userDao.findByUsername(username.trim()) == null) {
                    // Don't disclose account existence; still show the success-style page.
                    req.setAttribute("infoMessage", "If the account exists, a reset link has been generated.");
                    req.getRequestDispatcher("/pages/forgot-password.jsp").forward(req, resp);
                    return;
                }
                byte[] buf = new byte[24];
                RNG.nextBytes(buf);
                String token = Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
                Timestamp expires = new Timestamp(System.currentTimeMillis() + 30L * 60_000L);
                userDao.setResetToken(username.trim(), token, expires);

                String link = req.getContextPath() + "/reset-password?token=" + token;
                req.setAttribute("resetLink", link);
                req.setAttribute("infoMessage", "Reset link generated. In production this would be e-mailed to you.");
                req.getRequestDispatcher("/pages/forgot-password.jsp").forward(req, resp);
                return;
            }

            if ("/reset-password".equals(path)) {
                String token = req.getParameter("token");
                String pw = req.getParameter("password");
                String confirm = req.getParameter("confirm");
                if (token == null || pw == null || confirm == null) {
                    fail(req, resp, "/pages/reset-password.jsp", "All fields are required.");
                    return;
                }
                if (pw.length() < 6) {
                    req.setAttribute("token", token);
                    fail(req, resp, "/pages/reset-password.jsp", "Password must be at least 6 characters.");
                    return;
                }
                if (!pw.equals(confirm)) {
                    req.setAttribute("token", token);
                    fail(req, resp, "/pages/reset-password.jsp", "Passwords do not match.");
                    return;
                }
                String username = userDao.findUsernameByValidToken(token);
                if (username == null) {
                    fail(req, resp, "/pages/reset-password.jsp", "Reset token is invalid or has expired.");
                    return;
                }
                userDao.updatePassword(username, PasswordUtils.hashPassword(pw));
                req.setAttribute("infoMessage", "Password updated. You can now sign in.");
                req.getRequestDispatcher("/pages/login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String page, String msg)
            throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.getRequestDispatcher(page).forward(req, resp);
    }
}
