package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.model.Employee;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.Employeemanagementsystem.dao.UserDao;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/employees")
public class EmployeeController extends HttpServlet {

    private EmployeeDao employeeDao = new EmployeeDao();
    private UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = (String) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/pages/login.jsp");
            return;
        }
        // only admin may access this servlet
        if (!"admin".equalsIgnoreCase(user)) {
            resp.sendRedirect(req.getContextPath() + "/employee");
            return;
        }
        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "delete":
                    deleteEmployee(req, resp);
                    break;
                case "edit":
                    showEditForm(req, resp);
                    break;
                default:
                    listEmployees(req, resp);
            }
        } catch (SQLException e) {
            // handle DB errors gracefully and show an error page instead of letting a 500 bubble up
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/pages/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = (String) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/pages/login.jsp");
            return;
        }
        // non-admins cannot post to admin endpoints
        if (!"admin".equalsIgnoreCase(user)) {
            resp.sendRedirect(req.getContextPath() + "/employee");
            return;
        }
        String action = req.getParameter("action");
        if (action == null) action = "add";

        try {
            switch (action) {
                case "add":
                    addEmployee(req, resp);
                    break;
                case "update":
                    updateEmployee(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/employees");
            }
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/pages/error.jsp").forward(req, resp);
        }
    }

    private void listEmployees(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        String search = req.getParameter("search");
        String department = req.getParameter("department");
        String status = req.getParameter("status");
        List<Employee> employees = employeeDao.getAllEmployees(search, department, status);
        req.setAttribute("employees", employees);
        req.setAttribute("search", search);
        req.setAttribute("department", department);
        req.setAttribute("status", status);
        req.getRequestDispatcher("/pages/EMS.jsp").forward(req, resp);
    }

    private void addEmployee(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        Employee e = extractEmployeeFromRequest(req);
        String password = req.getParameter("password");
        employeeDao.addEmployee(e);
        // If admin provided a password, create a corresponding user account (username=email) with role 'user'
        if (password != null && !password.isBlank()) {
            try {
                String username = e.getEmail();
                if (username != null && !username.isBlank()) {
                    // delegate hashing and creation to UserDao
                    userDao.createUser(username, password, "user");
                }
            } catch (SQLException ex) {
                // log but don't fail the whole operation
                System.err.println("[EmployeeController] Failed to create user for employee: " + ex.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + "/employees");
    }

    private void updateEmployee(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        Employee e = extractEmployeeFromRequest(req);
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            e.setId(Integer.parseInt(idStr));
            employeeDao.updateEmployee(e);
        }
        resp.sendRedirect(req.getContextPath() + "/employees");
    }

    private void deleteEmployee(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            employeeDao.deleteEmployee(id);
        }
        resp.sendRedirect(req.getContextPath() + "/employees");
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            Employee e = employeeDao.getEmployeeById(id);
            if (e != null) {
                req.setAttribute("employeeToEdit", e);
            }
        }
        listEmployees(req, resp);
    }

    private Employee extractEmployeeFromRequest(HttpServletRequest req) {
        Employee e = new Employee();
        e.setName(trim(req.getParameter("name")));
        e.setEmail(trim(req.getParameter("email")));
        e.setPhone(trim(req.getParameter("phone")));
        e.setDepartment(trim(req.getParameter("department")));
        e.setRole(trim(req.getParameter("role")));
        String salaryStr = trim(req.getParameter("salary"));
        double salary = 0.0;
        try {
            if (salaryStr != null && !salaryStr.isEmpty()) salary = Double.parseDouble(salaryStr);
        } catch (NumberFormatException ignored) {}
        e.setSalary(salary);
        String status = trim(req.getParameter("status"));
        e.setStatus((status == null || status.isEmpty()) ? "Active" : status);
        return e;
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
