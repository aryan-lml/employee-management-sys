package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.NotificationDao;
import com.Employeemanagementsystem.dao.TaskDao;
import com.Employeemanagementsystem.model.Employee;
import com.Employeemanagementsystem.model.Task;
import com.Employeemanagementsystem.model.TaskStatus;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Admin task management, scoped to the current admin:
 *   - Lists tasks the admin has created.
 *   - "Assign to" dropdown is populated only with the admin's own employees.
 *   - On create, a notification is queued for the assigned employee.
 */
@WebServlet("/admin-tasks")
public class AdminTaskController extends HttpServlet {

    private final TaskDao taskDao = new TaskDao();
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final NotificationDao notificationDao = new NotificationDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = AuthUtils.getUser(req.getSession());
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
        }
        try {
            List<Task> tasks = taskDao.findByCreator(admin.getId());
            List<Employee> employees = employeeDao.getAllEmployees(admin.getId(), null, null, null);
            req.setAttribute("tasks", tasks);
            req.setAttribute("employees", employees);
            req.getRequestDispatcher("/pages/admin-tasks.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = AuthUtils.getUser(req.getSession());
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
        }
        String action = req.getParameter("action");
        try {
            switch (action == null ? "" : action.toLowerCase()) {
                case "create" -> createTask(req, admin);
                case "update" -> updateTask(req, admin);
                case "delete" -> deleteTask(req, admin);
            }
            resp.sendRedirect(req.getContextPath() + "/admin-tasks");
        } catch (SQLException e) { throw new ServletException(e); }
    }

    // ----- handlers -----
    private void createTask(HttpServletRequest req, User admin) throws SQLException {
        String title = trim(req.getParameter("title"));
        if (title == null || title.isBlank()) return;
        Integer assignedId = parseInt(req.getParameter("assignedTo"));
        // Only allow assigning to employees this admin owns.
        if (assignedId == null || employeeDao.getEmployeeById(assignedId, admin.getId()) == null) return;

        Task t = new Task();
        t.setTitle(title);
        t.setDescription(req.getParameter("description"));
        t.setAssignedTo(assignedId);
        t.setStatus(TaskStatus.PENDING);
        String due = req.getParameter("dueDate");
        if (due != null && !due.isBlank()) t.setDueDate(Date.valueOf(due));
        t.setCreatedBy(admin.getId());
        taskDao.createTask(t);
        notificationDao.create(assignedId, "New task assigned: " + title, "/tasks");
    }

    private void updateTask(HttpServletRequest req, User admin) throws SQLException {
        Integer id = parseInt(req.getParameter("id"));
        if (id == null) return;
        Task t = taskDao.findById(id, admin.getId()); // ownership-checked
        if (t == null) return;
        t.setTitle(trim(req.getParameter("title")));
        t.setDescription(req.getParameter("description"));
        Integer assignedId = parseInt(req.getParameter("assignedTo"));
        if (assignedId != null && employeeDao.getEmployeeById(assignedId, admin.getId()) == null) assignedId = null;
        t.setAssignedTo(assignedId);
        String status = req.getParameter("status");
        if (status != null) try { t.setStatus(TaskStatus.valueOf(status)); } catch (Exception ignore) {}
        String due = req.getParameter("dueDate");
        t.setDueDate((due == null || due.isBlank()) ? null : Date.valueOf(due));
        taskDao.updateTask(t);
    }

    private void deleteTask(HttpServletRequest req, User admin) throws SQLException {
        Integer id = parseInt(req.getParameter("id"));
        if (id != null) taskDao.deleteTask(id, admin.getId());
    }

    private Integer parseInt(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return null; }
    }
    private String trim(String s) { return s == null ? null : s.trim(); }
}
