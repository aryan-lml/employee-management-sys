<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.Employeemanagementsystem.model.Employee" %>
<%
    String user = (String) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
        return;
    }
    Employee emp = (Employee) request.getAttribute("employee");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <title>Employee Profile</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
    <style>
        body{font-family:Arial;margin:0;background:#f5f7fb;padding:20px}
        .card{background:#fff;padding:20px;border-radius:10px;box-shadow:0 6px 18px rgba(25,30,50,0.06);max-width:900px;margin:0 auto}
        .actions{display:flex;gap:8px;justify-content:flex-end}
        table{width:100%;border-collapse:collapse}
        td{padding:8px;border-bottom:1px solid #eee}
    </style>
</head>
<body>
<div class="card">
    <div style="display:flex;align-items:center;justify-content:space-between">
        <h2>Your Profile</h2>
        <div>
            <a href="<%=request.getContextPath()%>/logout"><button>Logout</button></a>
        </div>
    </div>
    <p>Welcome, <strong><%= user %></strong></p>
    <%
        if (emp == null) {
    %>
        <p>No employee record found for your account. Contact administrator.</p>
    <%
        } else {
    %>
        <table>
            <tr><td>Name</td><td><%= emp.getName() %></td></tr>
            <tr><td>Email</td><td><%= emp.getEmail() %></td></tr>
            <tr><td>Phone</td><td><%= emp.getPhone() %></td></tr>
            <tr><td>Department</td><td><%= emp.getDepartment() %></td></tr>
            <tr><td>Role</td><td><%= emp.getRole() %></td></tr>
            <tr><td>Salary</td><td><%= emp.getSalary() %></td></tr>
            <tr><td>Status</td><td><%= emp.getStatus() %></td></tr>
            <tr><td>Created</td><td><%= emp.getCreatedAt() %></td></tr>
        </table>
    <%
        }
    %>
    <div style="margin-top:16px;">
        <a href="<%=request.getContextPath()%>/pages/login.jsp">Back to Login</a>
    </div>
</div>
</body>
</html>
