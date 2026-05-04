<%@ page import="dao.TaskDAO, model.Task" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>EMS — Tasks</title>
  <link rel="stylesheet" href="/ui/assets/styles.css" />
</head>
<body>
<%
    TaskDAO td = new TaskDAO();
    List<Task> tasks = null;
    try { tasks = td.getAllTasks(); } catch (SQLException e) { tasks = java.util.Collections.emptyList(); }
%>
<div style="padding:24px;max-width:1100px;margin:24px auto;">
  <h1>Tasks</h1>
  <div style="margin-top:12px;background:#fff;padding:12px;border-radius:12px;box-shadow:0 6px 18px rgba(2,6,23,0.04)">
    <table class="table" style="width:100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>Title</th>
          <th>Employee ID</th>
          <th>Status</th>
          <th>Created</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
      <% for (Task t : tasks) { %>
        <tr>
          <td><%= t.getId() %></td>
          <td><%= t.getTitle() %></td>
          <td><%= t.getEmployeeId() %></td>
          <td><%= t.getStatus() %></td>
          <td><%= t.getCreatedAt() %></td>
          <td>
            <form action="/tasks" method="post" style="display:inline">
              <input type="hidden" name="action" value="status" />
              <input type="hidden" name="id" value="<%= t.getId() %>" />
              <select name="status" onchange="this.form.submit()">
                <option value="PENDING" <%= "PENDING".equals(t.getStatus())?"selected":"" %>>PENDING</option>
                <option value="IN_PROGRESS" <%= "IN_PROGRESS".equals(t.getStatus())?"selected":"" %>>IN_PROGRESS</option>
                <option value="COMPLETED" <%= "COMPLETED".equals(t.getStatus())?"selected":"" %>>COMPLETED</option>
              </select>
            </form>
            <a href="/tasks?action=delete&id=<%= t.getId() %>" class="btn-delete btn-small" onclick="return confirm('Delete task?')">Delete</a>
          </td>
        </tr>
      <% } %>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>