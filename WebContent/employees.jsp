<%@ page import="dao.EmployeeDAO, model.Employee" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>EMS — Employees</title>
  <link rel="stylesheet" href="/ui/assets/styles.css" />
</head>
<body>
<%
    EmployeeDAO ed = new EmployeeDAO();
    List<Employee> employees = null;
    try { employees = ed.getAllEmployees(); } catch (SQLException e) { employees = java.util.Collections.emptyList(); }
%>
<div style="padding:24px;max-width:1100px;margin:24px auto;">
  <div style="display:flex;align-items:center;justify-content:space-between;gap:12px">
    <h1>Employees</h1>
    <div>
      <a href="/add-employee" class="btn-primary" style="padding:10px 14px;border-radius:10px;color:#fff;background:#4f46e5;text-decoration:none">+ Add Employee</a>
    </div>
  </div>

  <div style="margin-top:12px;background:#fff;padding:12px;border-radius:12px;box-shadow:0 6px 18px rgba(2,6,23,0.04)">
    <table class="table" style="width:100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Email</th>
          <th>Department</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
      <% for (Employee e : employees) { %>
        <tr>
          <td><%= e.getEmployeeCode() %></td>
          <td><%= e.getName() %></td>
          <td><%= e.getEmail() %></td>
          <td><%= e.getDepartment() %></td>
          <td>
            <% if (e.isStatus()) { %>
              <span class="badge active">Active</span>
            <% } else { %>
              <span class="badge inactive">Inactive</span>
            <% } %>
          </td>
          <td>
            <a href="/employees?action=edit&id=<%= e.getId() %>" class="btn-edit btn-small">Edit</a>
            <a href="/employees?action=delete&id=<%= e.getId() %>" class="btn-delete btn-small" onclick="return confirm('Delete employee?')">Delete</a>
          </td>
        </tr>
      <% } %>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>