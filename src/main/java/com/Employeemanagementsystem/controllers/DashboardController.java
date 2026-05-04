package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.TaskDao;
import com.Employeemanagementsystem.dao.AttendanceDao;
import com.Employeemanagementsystem.model.Employee;
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

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final TaskDao taskDao = new TaskDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/pages/login.jsp");
            return;
        }

        try {
            List<Employee> all = employeeDao.getAllEmployees(null, null, null);
            req.setAttribute("totalEmployees", all == null ? 0 : all.size());

            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                // admin-specific quick stats
                req.setAttribute("yourTasks", "-" );
                req.setAttribute("todaysAttendance", "-" );
            } else {
                // employee stats
                Employee emp = employeeDao.getEmployeeByEmail(user.getUsername());
                int yourTasks = 0;
                String todays = "Not checked in";
                if (emp != null) {
                    yourTasks = taskDao.findByEmployee(emp.getId()).size();
                    if (attendanceDao.getTodayAttendanceForEmployee(emp.getId()) != null) todays = "Checked in";
                }
                req.setAttribute("yourTasks", yourTasks);
                req.setAttribute("todaysAttendance", todays);
            }

            req.getRequestDispatcher("/pages/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
