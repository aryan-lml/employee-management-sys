<%@ page import="dao.AttendanceDAO, model.Attendance" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>EMS — Attendance</title>
  <link rel="stylesheet" href="/ui/assets/styles.css" />
</head>
<body>
<%
    AttendanceDAO ad = new AttendanceDAO();
    List<Attendance> list = null;
    try { list = ad.getTodayAttendance(); } catch (SQLException e) { list = java.util.Collections.emptyList(); }
%>
<div style="padding:24px;max-width:1100px;margin:24px auto;">
  <h1>Attendance — Today</h1>
  <div style="margin-top:12px;background:#fff;padding:12px;border-radius:12px;box-shadow:0 6px 18px rgba(2,6,23,0.04)">
    <table class="table" style="width:100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>Employee ID</th>
          <th>Status</th>
          <th>Check In</th>
          <th>Check Out</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
      <% for (Attendance a : list) { %>
        <tr>
          <td><%= a.getId() %></td>
          <td><%= a.getEmployeeId() %></td>
          <td><%= a.getStatus() %></td>
          <td><%= a.getCheckIn() %></td>
          <td><%= a.getCheckOut() %></td>
          <td>
            <% if (a.getCheckOut() == null) { %>
              <form action="/attendance" method="post" style="display:inline">
                <input type="hidden" name="action" value="checkout" />
                <input type="hidden" name="id" value="<%= a.getId() %>" />
                <button class="btn btn-outline btn-small">Check-out</button>
              </form>
            <% } %>
          </td>
        </tr>
      <% } %>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>