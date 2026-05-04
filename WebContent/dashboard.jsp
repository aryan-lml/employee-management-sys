<%@ page import="dao.EmployeeDAO, dao.TaskDAO, dao.AttendanceDAO" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>EMS — Dashboard</title>
  <link rel="stylesheet" href="/ui/assets/styles.css" />
</head>
<body>
<%
    EmployeeDAO ed = new EmployeeDAO();
    TaskDAO td = new TaskDAO();
    AttendanceDAO ad = new AttendanceDAO();
    int total = 0, active = 0, pending = 0, todayAtt = 0;
    try {
        total = ed.totalEmployees();
        active = ed.activeEmployees();
        pending = td.pendingTasks();
        todayAtt = ad.todayAttendanceCount();
    } catch (SQLException e) { /* log */ }
%>
<div style="padding:24px;max-width:1100px;margin:24px auto;">
  <h1>Dashboard</h1>
  <div style="display:grid;grid-template-columns:repeat(4,1fr);gap:12px;margin-top:12px">
    <div style="background:#fff;padding:16px;border-radius:12px;box-shadow:0 8px 20px rgba(2,6,23,0.06);border-left:6px solid #4f46e5">
      <div style="font-size:28px;font-weight:700"><%= total %></div>
      <div style="color:#6b7280">Total Employees</div>
    </div>
    <div style="background:#fff;padding:16px;border-radius:12px;box-shadow:0 8px 20px rgba(2,6,23,0.06);border-left:6px solid #22c55e">
      <div style="font-size:28px;font-weight:700"><%= active %></div>
      <div style="color:#6b7280">Active Employees</div>
    </div>
    <div style="background:#fff;padding:16px;border-radius:12px;box-shadow:0 8px 20px rgba(2,6,23,0.06);border-left:6px solid #f59e0b">
      <div style="font-size:28px;font-weight:700"><%= pending %></div>
      <div style="color:#6b7280">Tasks Pending</div>
    </div>
    <div style="background:#fff;padding:16px;border-radius:12px;box-shadow:0 8px 20px rgba(2,6,23,0.06);border-left:6px solid #ef4444">
      <div style="font-size:28px;font-weight:700"><%= todayAtt %></div>
      <div style="color:#6b7280">Attendance Today</div>
    </div>
  </div>

  <div style="margin-top:18px;background:#fff;padding:16px;border-radius:12px;box-shadow:0 8px 20px rgba(2,6,23,0.04)">
    <h3>Recent activity</h3>
    <p style="color:#6b7280">This area can be populated by server-side events</p>
  </div>
</div>
</body>
</html>