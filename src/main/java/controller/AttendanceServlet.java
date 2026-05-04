package controller;

import dao.AttendanceDAO;
import model.Attendance;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet(name = "AttendanceServlet", urlPatterns = {"/attendance"})
public class AttendanceServlet extends HttpServlet {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("today".equals(action)) {
                req.setAttribute("attendanceList", attendanceDAO.getTodayAttendance());
                req.getRequestDispatcher("/attendance.jsp").forward(req, resp);
            } else if ("byEmployee".equals(action)) {
                int empId = Integer.parseInt(req.getParameter("employeeId"));
                req.setAttribute("attendanceList", attendanceDAO.getAttendanceByEmployee(empId));
                req.getRequestDispatcher("/attendance.jsp").forward(req, resp);
            } else {
                req.setAttribute("attendanceList", attendanceDAO.getTodayAttendance());
                req.getRequestDispatcher("/attendance.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // mark attendance (check-in/out)
        String action = req.getParameter("action");
        try {
            if ("mark".equals(action)) {
                int empId = Integer.parseInt(req.getParameter("employeeId"));
                String status = req.getParameter("status"); // PRESENT/ABSENT/LATE
                Attendance att = new Attendance();
                att.setEmployeeId(empId);
                att.setStatus(status);
                att.setCheckIn(LocalDateTime.now());
                attendanceDAO.markAttendance(att);
                resp.sendRedirect(req.getContextPath() + "/attendance?action=today");
            } else if ("checkout".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                attendanceDAO.checkOut(id);
                resp.sendRedirect(req.getContextPath() + "/attendance?action=today");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
