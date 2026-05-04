package com.Employeemanagementsystem.util;

import jakarta.servlet.http.HttpSession;
import com.Employeemanagementsystem.model.User;

/**
 * Small authentication helper utilities for working with the session-stored User object.
 */
public final class AuthUtils {

    private AuthUtils() {}

    public static User getUser(HttpSession session) {
        if (session == null) return null;
        Object o = session.getAttribute("userObj");
        return (o instanceof User) ? (User) o : null;
    }

    public static String getUsername(HttpSession session) {
        User u = getUser(session);
        return u == null ? null : u.getUsername();
    }

    public static boolean isAdmin(HttpSession session) {
        User u = getUser(session);
        return u != null && "admin".equalsIgnoreCase(u.getRole());
    }
}
