<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.Employeemanagementsystem.model.Task" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("active", "dashboard");
    request.setAttribute("pageTitle", "Dashboard");
    @SuppressWarnings("unchecked")
    List<Task> recentTasks = (List<Task>) request.getAttribute("recentTasks");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard — EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
</head>
<body>
<div class="app">
    <jsp:include page="/pages/layout/sidebar.jsp" />
    <jsp:include page="/pages/layout/topbar.jsp" />
    <main class="main">

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">&#128101;</div>
                <div>
                    <div class="stat-label">Total Employees</div>
                    <div class="stat-value">${totalEmployees}</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon green">&#10003;</div>
                <div>
                    <div class="stat-label">Active Employees</div>
                    <div class="stat-value">${activeEmployees}</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon amber">&#128203;</div>
                <div>
                    <div class="stat-label">Pending Tasks</div>
                    <div class="stat-value">${pendingTasks}</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128197;</div>
                <div>
                    <div class="stat-label">Attendance Today</div>
                    <div class="stat-value">${attendanceCount}</div>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <div>
                    <h2 class="mb-0">Recent Tasks</h2>
                    <div class="text-muted">Last 5 tasks assigned by you</div>
                </div>
            </div>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Status</th>
                            <th>Due Date</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% if (recentTasks == null || recentTasks.isEmpty()) { %>
                        <tr class="empty-row"><td colspan="3">No tasks yet — create one to get started.</td></tr>
                    <% } else {
                        for (Task t : recentTasks) {
                            String statusClass = "badge badge-" + t.getStatus().name().toLowerCase();
                    %>
                        <tr>
                            <td><strong><%= t.getTitle() %></strong></td>
                            <td><span class="<%= statusClass %>"><%= t.getStatus() %></span></td>
                            <td><%= t.getDueDate() != null ? t.getDueDate() : "—" %></td>
                        </tr>
                    <%  }
                    } %>
                    </tbody>
                </table>
            </div>
        </div>

    </main>
</div>
</body>
</html>
