<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>EMS — Employees</title>
  <link rel="stylesheet" href="assets/styles.css">
</head>
<body class="ui-root">

  <%@ include file="navbar.jsp" %>
  <%@ include file="sidebar.jsp" %>

  <main class="main" role="main">
    <div class="nav-placeholder" style="height:6px"></div>

    <div style="display:flex;align-items:center;justify-content:space-between;gap:12px;flex-wrap:wrap">
      <div class="top-controls" style="margin-bottom:10px;">
        <input type="search" class="input-search" placeholder="Search employees, email or id" aria-label="Search employees">
        <select class="select-filter" aria-label="Filter by status">
          <option value="all">All statuses</option>
          <option value="active">Active</option>
          <option value="inactive">Inactive</option>
        </select>
      </div>

      <div style="margin-left:auto">
        <a href="/add-employee" class="btn btn-primary">+ Add Employee</a>
      </div>
    </div>

    <section class="panel" aria-label="Employee list">
      <table class="table" role="table">
        <thead>
          <tr>
            <th scope="col">ID</th>
            <th scope="col">Name</th>
            <th scope="col">Email</th>
            <th scope="col">Department</th>
            <th scope="col">Status</th>
            <th scope="col">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>EMP-001</td>
            <td>John Doe</td>
            <td>john.doe@example.com</td>
            <td>Engineering</td>
            <td><span class="badge active">Active</span></td>
            <td>
              <button class="btn-edit btn-small">Edit</button>
              <button class="btn-delete btn-small">Delete</button>
            </td>
          </tr>

          <tr>
            <td>EMP-002</td>
            <td>Jane Smith</td>
            <td>jane.smith@example.com</td>
            <td>Marketing</td>
            <td><span class="badge inactive">Inactive</span></td>
            <td>
              <button class="btn-edit btn-small">Edit</button>
              <button class="btn-delete btn-small">Delete</button>
            </td>
          </tr>

          <tr>
            <td>EMP-003</td>
            <td>Mohammed Ali</td>
            <td>mohammed.ali@example.com</td>
            <td>Product</td>
            <td><span class="badge active">Active</span></td>
            <td>
              <button class="btn-edit btn-small">Edit</button>
              <button class="btn-delete btn-small">Delete</button>
            </td>
          </tr>

          <!-- Rows to be generated server-side for real data -->
        </tbody>
      </table>
    </section>

  </main>

</body>
</html>