package controller;

import dao.TaskDAO;
import model.Task;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "TaskServlet", urlPatterns = {"/tasks"})
public class TaskServlet extends HttpServlet {

    private final TaskDAO taskDAO = new TaskDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if (action == null || action.isEmpty()) {
                List<Task> tasks = taskDAO.getAllTasks();
                req.setAttribute("tasks", tasks);
                req.getRequestDispatcher("/tasks.jsp").forward(req, resp);
            } else if ("byEmployee".equals(action)) {
                int empId = Integer.parseInt(req.getParameter("employeeId"));
                List<Task> tasks = taskDAO.getTasksByEmployee(empId);
                req.setAttribute("tasks", tasks);
                req.getRequestDispatcher("/tasks.jsp").forward(req, resp);
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                taskDAO.deleteTask(id);
                resp.sendRedirect(req.getContextPath() + "/tasks");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // create or update status
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                int employeeId = Integer.parseInt(req.getParameter("employeeId"));
                String title = req.getParameter("title");
                String desc = req.getParameter("description");
                Task t = new Task();
                t.setEmployeeId(employeeId);
                t.setTitle(title);
                t.setDescription(desc);
                t.setStatus("PENDING");
                taskDAO.assignTask(t);
                resp.sendRedirect(req.getContextPath() + "/tasks");
            } else if ("status".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String status = req.getParameter("status");
                taskDAO.updateTaskStatus(id, status);
                resp.sendRedirect(req.getContextPath() + "/tasks");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
