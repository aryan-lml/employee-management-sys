package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.EmployeeDao;
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
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

@WebServlet("/admin-tasks")
public class AdminTaskController extends HttpServlet {

    private final TaskDao taskDao = new TaskDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) { resp.sendRedirect(req.getContextPath() + "/dashboard"); return; }

        try {
            List<Task> tasks = taskDao.findAll();
            req.setAttribute("tasks", tasks);
            req.getRequestDispatcher("/pages/admin-tasks.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) { resp.sendRedirect(req.getContextPath() + "/dashboard"); return; }

        String action = req.getParameter("action");
        try {
            if ("create".equalsIgnoreCase(action)) {
                String title = req.getParameter("title");
                String assignedEmail = req.getParameter("assignedToEmail");
                String due = req.getParameter("dueDate");
                Integer assignedId = null;
                if (assignedEmail != null && !assignedEmail.isBlank()) {
                    Employee e = employeeDao.getEmployeeByEmail(assignedEmail.trim());
                    if (e != null) assignedId = e.getId();
                }
                Task t = new Task();
                t.setTitle(title);
                t.setDescription(req.getParameter("description"));
                t.setAssignedTo(assignedId);
                t.setStatus(TaskStatus.PENDING);
                if (due != null && !due.isBlank()) t.setDueDate(Date.valueOf(due));
                t.setCreatedBy(user.getId());
                taskDao.createTask(t);
            }
            doGet(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }
}
