package controller;

import dao.EmployeeDAO;
import model.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "EmployeeServlet", urlPatterns = {"/employees"})
public class EmployeeServlet extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if (action == null || action.isEmpty()) {
                // list all
                List<Employee> employees = employeeDAO.getAllEmployees();
                req.setAttribute("employees", employees);
                req.getRequestDispatcher("/employees.jsp").forward(req, resp);
            } else if ("edit".equals(action)) {
                String id = req.getParameter("id");
                Employee e = employeeDAO.getEmployeeById(Integer.parseInt(id));
                req.setAttribute("employee", e);
                req.getRequestDispatcher("/employee-form.jsp").forward(req, resp);
            } else if ("delete".equals(action)) {
                String id = req.getParameter("id");
                employeeDAO.deleteEmployee(Integer.parseInt(id));
                resp.sendRedirect(req.getContextPath() + "/employees");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // handle create and update
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String dept = req.getParameter("department");
        String status = req.getParameter("status");

        try {
            if (id == null || id.isEmpty()) {
                // create
                Employee e = new Employee();
                e.setName(name);
                e.setEmail(email);
                e.setDepartment(dept);
                e.setStatus("ACTIVE".equalsIgnoreCase(status));
                employeeDAO.addEmployee(e);
            } else {
                Employee e = employeeDAO.getEmployeeById(Integer.parseInt(id));
                if (e != null) {
                    e.setName(name);
                    e.setEmail(email);
                    e.setDepartment(dept);
                    e.setStatus("ACTIVE".equalsIgnoreCase(status));
                    employeeDAO.updateEmployee(e);
                }
            }
            resp.sendRedirect(req.getContextPath() + "/employees");
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }
}
