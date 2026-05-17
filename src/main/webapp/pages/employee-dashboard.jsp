<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Employeemanagementsystem.model.Employee" %>
<%@ page import="com.Employeemanagementsystem.model.Task" %>
<%@ page import="com.Employeemanagementsystem.model.TaskStatus" %>
<%@ page import="com.Employeemanagementsystem.model.Attendance" %>
<%@ page import="com.Employeemanagementsystem.model.Notification" %>
<%
    request.setAttribute("active", "employee-dashboard");
    request.setAttribute("pageTitle", "My Dashboard");
    Employee emp = (Employee) request.getAttribute("employee");
    List<Task> tasks = (List<Task>) request.getAttribute("tasks");
    List<Attendance> attendance = (List<Attendance>) request.getAttribute("attendanceList");
    List<Notification> notifs = (List<Notification>) request.getAttribute("notifications");
    Attendance today = (Attendance) request.getAttribute("todayAttendance");
    String errorMessage = (String) request.getAttribute("errorMessage");

    int openCount = 0;
    if (tasks != null) for (Task t : tasks) if (t.getStatus() != TaskStatus.COMPLETED) openCount++;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Dashboard — EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
</head>
<body>
<div class="app">
    <jsp:include page="/pages/layout/sidebar.jsp" />
    <jsp:include page="/pages/layout/topbar.jsp" />
    <main class="main">
        <% if (errorMessage != null) { %>
            <div class="alert alert-error"><%= errorMessage %></div>
        <% } %>

        <div class="card">
            <h2>Hello, <%= emp != null && emp.getName() != null ? emp.getName() : (session.getAttribute("userObj") != null ? ((com.Employeemanagementsystem.model.User) session.getAttribute("userObj")).getUsername() : "") %></h2>
            <p class="text-muted mb-0">Here's a snapshot of your work today.</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">&#9999;</div>
                <div><div class="stat-label">Open Tasks</div><div class="stat-value"><%= openCount %></div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon green">&#128197;</div>
                <div><div class="stat-label">Today</div><div class="stat-value"><%= today == null ? "Not in" : (today.getCheckOut() == null ? "Checked in" : "Done") %></div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon amber">&#9200;</div>
                <div><div class="stat-label">Attendance Records</div><div class="stat-value"><%= attendance != null ? attendance.size() : 0 %></div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128276;</div>
                <div><div class="stat-label">Notifications</div><div class="stat-value"><%= notifs != null ? notifs.size() : 0 %></div></div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h3 class="mb-0">My tasks</h3>
                <a class="btn btn-secondary btn-sm" href="<%=request.getContextPath()%>/tasks">View all</a>
            </div>
            <div class="table-wrap">
                <table class="data">
                    <thead><tr><th>Title</th><th>Description</th><th>Status</th><th>Due</th></tr></thead>
                    <tbody>
                    <% if (tasks == null || tasks.isEmpty()) { %>
                        <tr class="empty-row"><td colspan="4">No tasks assigned yet.</td></tr>
                    <% } else {
                        int rows = 0;
                        for (Task t : tasks) {
                            if (rows++ >= 5) break;
                            String statusLower = t.getStatus() != null ? t.getStatus().name().toLowerCase() : "pending";
                    %>
                        <tr>
                            <td><strong><%= t.getTitle() %></strong></td>
                            <td><%= t.getDescription() != null ? t.getDescription() : "—" %></td>
                            <td><span class="badge badge-<%= statusLower %>"><%= t.getStatus() %></span></td>
                            <td><%= t.getDueDate() != null ? t.getDueDate().toString() : "—" %></td>
                        </tr>
                    <% }} %>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="card">
            <div class="card-header"><h3 class="mb-0">Recent notifications</h3></div>
            <% if (notifs == null || notifs.isEmpty()) { %>
                <p class="text-muted mb-0">No notifications yet.</p>
            <% } else { for (Notification n : notifs) { %>
                <div class="notif-item <%= n.isRead() ? "" : "unread" %>">
                    <div class="msg"><%= n.getMessage() %></div>
                    <div class="meta"><%= n.getCreatedAt() %></div>
                </div>
            <% }} %>
        </div>
    </main>
</div>
</body>
</html>
