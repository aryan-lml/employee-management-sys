<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>EMS — Dashboard</title>
  <link rel="stylesheet" href="assets/styles.css">
</head>
<body class="ui-root">

  <%@ include file="navbar.jsp" %>
  <%@ include file="sidebar.jsp" %>

  <main class="main" role="main">
    <div class="nav-placeholder" style="height:6px"></div>

    <!-- Top sticky navbar is included above -->

    <!-- STAT CARDS -->
    <section aria-labelledby="stats-heading">
      <h2 id="stats-heading" style="display:none">Dashboard stats</h2>
      <div class="cards-grid">
        <article class="card total" aria-label="Total employees">
          <div class="icon">👥</div>
          <div class="body">
            <div class="value">1,542</div>
            <div class="label">Total Employees</div>
            <small style="color:var(--muted)">Overall headcount</small>
          </div>
        </article>

        <article class="card active" aria-label="Active employees">
          <div class="icon">✅</div>
          <div class="body">
            <div class="value">1,321</div>
            <div class="label">Active Employees</div>
            <small style="color:var(--muted)">Currently enabled accounts</small>
          </div>
        </article>

        <article class="card pending" aria-label="Tasks pending">
          <div class="icon">📋</div>
          <div class="body">
            <div class="value">87</div>
            <div class="label">Tasks Pending</div>
            <small style="color:var(--muted)">Open tasks assigned</small>
          </div>
        </article>

        <article class="card attendance" aria-label="Attendance today">
          <div class="icon">📅</div>
          <div class="body">
            <div class="value">78%</div>
            <div class="label">Attendance Today</div>
            <small style="color:var(--muted)">Present / Expected</small>
          </div>
        </article>
      </div>
    </section>

    <!-- CHARTS + ACTIVITY -->
    <section class="charts" aria-label="Charts and summaries">
      <div class="chart">
        <div class="title">Tasks Status</div>
        <div style="flex:1;display:flex;align-items:center;justify-content:center;color:var(--muted)">Chart placeholder (Pending vs Completed)</div>
      </div>
      <div class="small-chart">
        <div class="title">Attendance Summary</div>
        <div style="flex:1;display:flex;align-items:center;justify-content:center;color:var(--muted)">Minichart placeholder</div>
      </div>
    </section>

    <!-- RECENT ACTIVITY -->
    <section class="panel" aria-labelledby="recent-heading">
      <h3 id="recent-heading" style="margin:0 0 12px 0">Recent Activity</h3>
      <div style="overflow:auto">
        <table class="table" role="table">
          <thead>
            <tr>
              <th scope="col">Employee</th>
              <th scope="col">Action</th>
              <th scope="col">Time</th>
              <th scope="col">Status</th>
              <th scope="col">Manage</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Jordan Lee</td>
              <td>Updated Role</td>
              <td>2h ago</td>
              <td><span class="badge active">Active</span></td>
              <td>
                <button class="btn-edit btn-small">Edit</button>
                <button class="btn-delete btn-small">Delete</button>
              </td>
            </tr>
            <tr>
              <td>Priya Kumar</td>
              <td>Marked Attendance</td>
              <td>3h ago</td>
              <td><span class="badge active">Present</span></td>
              <td>
                <button class="btn-edit btn-small">Edit</button>
                <button class="btn-delete btn-small">Delete</button>
              </td>
            </tr>
            <tr>
              <td>Samuel Park</td>
              <td>Created Task</td>
              <td>Yesterday</td>
              <td><span class="badge inactive">Inactive</span></td>
              <td>
                <button class="btn-edit btn-small">Edit</button>
                <button class="btn-delete btn-small">Delete</button>
              </td>
            </tr>
            <!-- Additional rows would be populated server-side -->
          </tbody>
        </table>
      </div>
    </section>

  </main>

</body>
</html>