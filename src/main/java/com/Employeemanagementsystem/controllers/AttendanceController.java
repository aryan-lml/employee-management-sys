package com.Employeemanagementsystem.controllers;

import com.Employeemanagementsystem.dao.AttendanceDao;
import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.model.Attendance;
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
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@WebServlet("/attendance")
public class AttendanceController extends HttpServlet {

    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }

        try {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                List<Attendance> all = attendanceDao.getAllAttendance();
                req.setAttribute("attendanceList", all);
            } else {
                Employee emp = employeeDao.getEmployeeByEmail(user.getUsername());
                if (emp != null) {
                    Attendance today = attendanceDao.getTodayAttendanceForEmployee(emp.getId());
                    List<Attendance> history = attendanceDao.getAttendanceForEmployee(emp.getId());
                    req.setAttribute("todayAttendance", today);
                    req.setAttribute("attendanceList", history);
                }
            }
            req.getRequestDispatcher("/pages/attendance.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = AuthUtils.getUser(req.getSession());
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/pages/login.jsp"); return; }

        String action = req.getParameter("action");
        try {
            Employee emp = employeeDao.getEmployeeByEmail(user.getUsername());
            if (emp == null) { req.setAttribute("errorMessage", "Employee record not found."); doGet(req, resp); return; }

            if ("checkin".equalsIgnoreCase(action)) {
                Attendance existing = attendanceDao.getTodayAttendanceForEmployee(emp.getId());
                if (existing != null) {
                    req.setAttribute("errorMessage", "Already checked in today.");
                } else {
                    Attendance a = new Attendance();
                    a.setEmployeeId(emp.getId());
                    a.setCheckIn(new Timestamp(System.currentTimeMillis()));
                    a.setStatus(null);
                    attendanceDao.checkIn(a);
                    req.setAttribute("infoMessage", "Checked in successfully.");
                }
            } else if ("checkout".equalsIgnoreCase(action)) {
                Attendance today = attendanceDao.getTodayAttendanceForEmployee(emp.getId());
                if (today == null) {
                    req.setAttribute("errorMessage", "No check-in found for today.");
                } else if (today.getCheckOut() != null) {
                    req.setAttribute("errorMessage", "Already checked out.");
                } else {
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    long millis = now.getTime() - today.getCheckIn().getTime();
                    double hours = millis / 1000.0 / 3600.0;
                    String status = "PRESENT";
                    // simple late detection: check-in after 09:30 local
                    // (not timezone-aware here)
                    attendanceDao.checkOut(today.getId(), now, hours, status);
                    req.setAttribute("infoMessage", "Checked out successfully. Worked hours: " + String.format("%.2f", hours));
                }
            }
            doGet(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }
}
