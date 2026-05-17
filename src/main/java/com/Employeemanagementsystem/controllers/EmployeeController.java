package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.UserDao;
import com.Employeemanagementsystem.model.Employee;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;
import com.Employeemanagementsystem.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Admin-only CRUD for employees that the current admin owns.
 *
 * Ownership is enforced at the DAO layer by always passing the
 * current admin's id as {@code ownerAdminId}. A non-owner admin
 * cannot read, edit or delete another admin's employees.
 *
 * When a password is supplied, the controller also creates a USER
 * account whose username equals the employee email, and links
 * {@code employees.user_id} to the new user so the employee can
 * log in and see their own dashboard.
 */
@WebServlet("/employees")
public class EmployeeController extends HttpServlet {

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = AuthUtils.getUser(req.getSession());
        if (admin == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }
        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/employee-dashboard"); return;
        }
        String action = req.getParameter("action");
        if (action == null) action = "list";
        try {
            switch (action) {
                case "delete" -> deleteEmployee(req, resp, admin);
                default -> listEmployees(req, resp, admin);
            }
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/pages/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = AuthUtils.getUser(req.getSession());
        if (admin == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }
        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/employee-dashboard"); return;
        }
        String action = req.getParameter("action");
        if (action == null) action = "add";
        try {
            switch (action) {
                case "add" -> addEmployee(req, resp, admin);
                case "update" -> updateEmployee(req, resp, admin);
                default -> resp.sendRedirect(req.getContextPath() + "/employees");
            }
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/pages/error.jsp").forward(req, resp);
        }
    }

    // -------- handlers --------
    private void listEmployees(HttpServletRequest req, HttpServletResponse resp, User admin)
            throws SQLException, ServletException, IOException {
        String search     = req.getParameter("search");
        String department = req.getParameter("department");
        String status     = req.getParameter("status");
        List<Employee> employees = employeeDao.getAllEmployees(admin.getId(), search, department, status);
        req.setAttribute("employees", employees);
        req.setAttribute("search", search);
        req.setAttribute("department", department);
        req.setAttribute("status", status);
        req.getRequestDispatcher("/pages/EMS.jsp").forward(req, resp);
    }

    private void addEmployee(HttpServletRequest req, HttpServletResponse resp, User admin)
            throws SQLException, ServletException, IOException {
        Employee e = extract(req);
        e.setOwnerAdminId(admin.getId());

        String error = validate(e);
        if (error != null) {
            req.setAttribute("formError", error);
            listEmployees(req, resp, admin);
            return;
        }
        // Guard against duplicate email globally (the column is unique).
        if (employeeDao.getEmployeeByEmail(e.getEmail()) != null) {
            req.setAttribute("formError", "An employee with that email already exists.");
            listEmployees(req, resp, admin);
            return;
        }
        employeeDao.addEmployee(e);

        // Optional: provision a user login for this employee.
        String password = req.getParameter("password");
        if (password != null && !password.isBlank()) {
            if (password.length() < 6) {
                req.setAttribute("formError", "Employee created, but password must be at least 6 characters — no login was provisioned.");
                listEmployees(req, resp, admin);
                return;
            }
            try {
                if (userDao.findByUsername(e.getEmail()) == null) {
                    // mustChangePassword=true → employee must replace the admin-issued
                    // password the first time they sign in.
                    userDao.createUser(e.getEmail(), password, "USER", true);
                    User u = userDao.findByUsername(e.getEmail());
                    if (u != null) employeeDao.linkUserId(e.getId(), u.getId());
                }
            } catch (SQLException ex) {
                System.err.println("[EmployeeController] Could not create login: " + ex.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + "/employees");
    }

    private void updateEmployee(HttpServletRequest req, HttpServletResponse resp, User admin)
            throws SQLException, ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isBlank()) { resp.sendRedirect(req.getContextPath() + "/employees"); return; }
        int id = Integer.parseInt(idStr);
        Employee existing = employeeDao.getEmployeeById(id, admin.getId());
        if (existing == null) {
            // Not owned by this admin — refuse silently.
            resp.sendRedirect(req.getContextPath() + "/employees");
            return;
        }
        Employee submitted = extract(req);
        submitted.setId(id);
        submitted.setUserId(existing.getUserId());
        submitted.setOwnerAdminId(admin.getId());

        String error = validate(submitted);
        if (error != null) {
            req.setAttribute("formError", error);
            listEmployees(req, resp, admin);
            return;
        }
        employeeDao.updateEmployee(submitted);
        resp.sendRedirect(req.getContextPath() + "/employees");
    }

    private void deleteEmployee(HttpServletRequest req, HttpServletResponse resp, User admin)
            throws SQLException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.isBlank()) {
            employeeDao.deleteEmployee(Integer.parseInt(idStr), admin.getId());
        }
        resp.sendRedirect(req.getContextPath() + "/employees");
    }

    // -------- helpers --------
    private Employee extract(HttpServletRequest req) {
        Employee e = new Employee();
        e.setName(trim(req.getParameter("name")));
        e.setEmail(trim(req.getParameter("email")));
        e.setPhone(trim(req.getParameter("phone")));
        e.setDepartment(trim(req.getParameter("department")));
        e.setRole(trim(req.getParameter("role")));
        String salaryStr = trim(req.getParameter("salary"));
        double salary = 0.0;
        try { if (salaryStr != null && !salaryStr.isEmpty()) salary = Double.parseDouble(salaryStr); }
        catch (NumberFormatException ignored) {}
        e.setSalary(salary);
        String status = trim(req.getParameter("status"));
        e.setStatus((status == null || status.isEmpty()) ? "Active" : status);
        return e;
    }

    private String validate(Employee e) {
        if (e.getName() == null || e.getName().isBlank()) return "Full name is required.";
        if (e.getName().matches(".*\\d.*")) return "Full name must not contain numbers.";
        if (!ValidationUtils.isValidEmail(e.getEmail())) return "A valid email address is required.";
        if (e.getPhone() != null && !e.getPhone().isBlank() && !ValidationUtils.isValidPhone(e.getPhone()))
            return "Phone must contain 7-15 digits.";
        if (!ValidationUtils.isValidSalary(e.getSalary())) return "Salary must be a non-negative number.";
        return null;
    }

    private String trim(String s) { return s == null ? null : s.trim(); }
}
