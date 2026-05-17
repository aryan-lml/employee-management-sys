package com.Employeemanagementsystem.filter;

import com.Employeemanagementsystem.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

/**
 * AuthFilter enforces session-based authentication and role-based access.
 * Unauthenticated users are redirected to the login page.
 * Non-admin users hitting admin-only endpoints are sent to the employee dashboard.
 */
@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    // Endpoints reachable without a session.
    private static final Set<String> PUBLIC_PATHS = Set.of(
        "/pages/login.jsp",
        "/pages/register.jsp",
        "/pages/forgot-password.jsp",
        "/pages/reset-password.jsp",
        "/pages/error.jsp",
        "/auth",
        "/forgot-password",
        "/reset-password"
    );

    // Path prefixes that should never be filtered (static assets).
    private static final Set<String> STATIC_PREFIXES = Set.of("/css/", "/js/", "/images/", "/assets/");

    // Endpoints reserved for ADMIN role.
    private static final Set<String> ADMIN_PATHS = Set.of(
        "/employees", "/admin-tasks", "/analytics", "/admin-attendance"
    );

    @Override public void init(FilterConfig cfg) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpRes = (HttpServletResponse) res;

        String ctx = httpReq.getContextPath();
        String uri = httpReq.getRequestURI();
        String path = uri.substring(ctx.length());

        // Root → login
        if (path.equals("/") || path.isEmpty()) {
            httpRes.sendRedirect(ctx + "/pages/login.jsp");
            return;
        }
        // Static assets bypass auth
        for (String p : STATIC_PREFIXES) {
            if (path.startsWith(p)) { chain.doFilter(req, res); return; }
        }
        // Public endpoints bypass auth
        if (PUBLIC_PATHS.contains(path)) { chain.doFilter(req, res); return; }

        HttpSession session = httpReq.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("userObj");
        if (user == null) {
            httpRes.sendRedirect(ctx + "/pages/login.jsp");
            return;
        }

        // First-login gate: trap the user on /force-password-change until they pick a new password.
        if (user.isMustChangePassword()
                && !path.equals("/force-password-change")
                && !path.equals("/logout")
                && !path.equals("/pages/force-password-change.jsp")) {
            httpRes.sendRedirect(ctx + "/force-password-change");
            return;
        }

        // Role-based protection
        if (ADMIN_PATHS.contains(path) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            httpRes.sendRedirect(ctx + "/employee-dashboard");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override public void destroy() {}
}
