package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.TaskDao;
import com.Employeemanagementsystem.model.Employee;
import com.Employeemanagementsystem.model.Task;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.AuthUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/tasks")
public class TaskController extends HttpServlet {

    private final TaskDao taskDao = new TaskDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }

        try {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                List<Task> all = taskDao.findAll();
                req.setAttribute("tasks", all);
            } else {
                Employee emp = employeeDao.getEmployeeByEmail(user.getUsername());
                if (emp != null) {
                    List<Task> tasks = taskDao.findByEmployee(emp.getId());
                    req.setAttribute("tasks", tasks);
                }
            }
            req.getRequestDispatcher("/pages/tasks.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }

        String action = req.getParameter("action");
        try {
            if ("updateStatus".equalsIgnoreCase(action)) {
                int taskId = Integer.parseInt(req.getParameter("taskId"));
                String status = req.getParameter("status");
                Task existing = taskDao.findById(taskId);
                if (existing != null && status != null) {
                    try {
                        existing.setStatus(com.Employeemanagementsystem.model.TaskStatus.valueOf(status));
                        taskDao.updateTask(existing);
                    } catch (IllegalArgumentException ignore) {
                        // invalid status value
                    }
                }
            }
            doGet(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }
}
