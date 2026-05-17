package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.UserDao;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;
import com.Employeemanagementsystem.util.PasswordUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Forces a user to choose a new password before they can use the system.
 *
 * Reached automatically after a successful login when
 * {@code users.must_change_password = 1}. Once the user submits a valid
 * new password, the flag is cleared (handled by {@link UserDao#updatePassword})
 * and they're redirected to the appropriate dashboard.
 */
@WebServlet("/force-password-change")
public class ForcePasswordChangeController extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }
        // If somehow the flag is already cleared, send them home.
        if (!user.isMustChangePassword()) { redirectHome(user, req, resp); return; }
        req.getRequestDispatcher("/pages/force-password-change.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }

        String current = req.getParameter("currentPassword");
        String next = req.getParameter("newPassword");
        String confirm = req.getParameter("confirmPassword");

        try {
            if (current == null || next == null || confirm == null
                    || current.isBlank() || next.isBlank() || confirm.isBlank()) {
                fail(req, resp, "Please fill in every field.");
                return;
            }
            if (!next.equals(confirm)) { fail(req, resp, "New password and confirmation don't match."); return; }
            if (next.length() < 6) { fail(req, resp, "New password must be at least 6 characters."); return; }
            if (next.equals(current)) { fail(req, resp, "New password must be different from the temporary one."); return; }
            if (!userDao.validateCredentials(user.getUsername(), current)) {
                fail(req, resp, "The temporary password is incorrect.");
                return;
            }

            userDao.updatePassword(user.getUsername(), PasswordUtils.hashPassword(next));
            // Refresh session copy so the filter no longer redirects.
            user.setMustChangePassword(false);
            req.getSession().setAttribute("userObj", user);
            redirectHome(user, req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String msg) throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.getRequestDispatcher("/pages/force-password-change.jsp").forward(req, resp);
    }

    private void redirectHome(User user, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String target = "ADMIN".equalsIgnoreCase(user.getRole()) ? "/dashboard" : "/employee-dashboard";
        resp.sendRedirect(req.getContextPath() + target);
    }
}
