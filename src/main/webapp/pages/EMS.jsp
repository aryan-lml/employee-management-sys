<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Employeemanagementsystem.model.Employee" %>
<%@ page import="com.Employeemanagementsystem.model.User" %>
<%
    User _user = (User) session.getAttribute("userObj");
    if (_user == null) {
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Employee Management System</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
    <style>
        /* Minimal clean styles to complement EMS.css */
        body { font-family: Arial, Helvetica, sans-serif; background:#f5f7fb; margin:0; padding:20px; }
        .container { max-width:1200px; margin:0 auto; }
        .card { background:white; border-radius:10px; box-shadow:0 6px 18px rgba(25,30,50,0.08); padding:20px; margin-bottom:20px; }
        .top-bar { display:flex; gap:12px; align-items:center; flex-wrap:wrap; }
        .title { font-size:22px; font-weight:600; }
        .spacer { flex:1; }
        input[type=text], select, input[type=number], input[type=email] { padding:8px 10px; border:1px solid #e0e6ef; border-radius:8px; }
        button { background:#2b7cff; color:white; border:none; padding:10px 14px; border-radius:8px; cursor:pointer; }
        button.secondary { background:#6c757d; }
        table { width:100%; border-collapse:collapse; margin-top:12px; }
        th, td { padding:12px 8px; text-align:left; border-bottom:1px solid #eef2f7; }
        th { background:transparent; color:#4b5563; font-weight:600; }
        .actions a { margin-right:8px; color:#2b7cff; text-decoration:none; }
        .modal { display:none; position:fixed; inset:0; background:rgba(0,0,0,0.4); align-items:center; justify-content:center; }
        .modal .dialog { width:520px; max-width:95%; }
        .form-row { display:flex; gap:10px; }
        .form-group { flex:1; display:flex; flex-direction:column; margin-bottom:10px; }
        @media (max-width:600px) {
            .form-row { flex-direction:column; }
        }
    </style>
    <script>
        function openModal(edit) {
            document.getElementById('employeeModal').style.display = 'flex';
            if (!edit) {
                document.getElementById('employeeForm').reset();
                document.getElementById('formAction').value = 'add';
                document.getElementById('idField').value = '';
                document.getElementById('password').value = '';
            }
        }
        function closeModal(){document.getElementById('employeeModal').style.display='none';}
        function editEmployee(e){
            // populate form and open
            document.getElementById('formAction').value = 'update';
            document.getElementById('idField').value = e.getAttribute('data-id');
            document.getElementById('name').value = e.getAttribute('data-name');
            document.getElementById('email').value = e.getAttribute('data-email');
            document.getElementById('password').value = '';
            document.getElementById('phone').value = e.getAttribute('data-phone');
            document.getElementById('department').value = e.getAttribute('data-department');
            document.getElementById('role').value = e.getAttribute('data-role');
            document.getElementById('salary').value = e.getAttribute('data-salary');
            document.getElementById('status').value = e.getAttribute('data-status');
            openModal(true);
        }
        function confirmDelete(id){
            if (confirm('Are you sure you want to delete this employee?')){
                window.location = '<%=request.getContextPath()%>/employees?action=delete&id=' + id;
            }
        }
    </script>
</head>
<body>
<div class="container">
    <div class="card top-bar">
        <div class="title">Employee Management System</div>
        <div class="spacer"></div>
        <div style="margin-right:12px;color:#374151">Welcome, <strong><%= _user.getUsername() %></strong></div>
        <div>
            <a href="<%=request.getContextPath()%>/logout" style="margin-right:8px; text-decoration:none;"><button class="secondary" type="button">Logout</button></a>
        </div>
        <form method="get" action="<%=request.getContextPath()%>/employees" style="display:flex; gap:8px; align-items:center;">
            <input type="text" name="search" placeholder="Search name or email" value="<%= request.getAttribute("search") != null ? request.getAttribute("search") : "" %>">
            <select name="department">
                <option value="">All Departments</option>
                <option value="HR" <%= "HR".equals(request.getAttribute("department"))?"selected":"" %>>HR</option>
                <option value="Engineering" <%= "Engineering".equals(request.getAttribute("department"))?"selected":"" %>>Engineering</option>
                <option value="Sales" <%= "Sales".equals(request.getAttribute("department"))?"selected":"" %>>Sales</option>
                <option value="Finance" <%= "Finance".equals(request.getAttribute("department"))?"selected":"" %>>Finance</option>
            </select>
            <select name="status">
                <option value="">Any Status</option>
                <option value="Active" <%= "Active".equals(request.getAttribute("status"))?"selected":"" %>>Active</option>
                <option value="Inactive" <%= "Inactive".equals(request.getAttribute("status"))?"selected":"" %>>Inactive</option>
            </select>
            <button type="submit">Search</button>
        </form>
        <div style="margin-left:12px;">
            <button onclick="openModal(false)">+ Add Employee</button>
        </div>
    </div>

    <div class="card">
        <table>
            <thead>
            <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Department</th>
                <th>Role</th>
                <th>Salary</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Employee> employees = (List<Employee>) request.getAttribute("employees");
                if (employees != null) {
                    for (Employee emp : employees) {
            %>
            <tr>
                <td><%= emp.getName() %></td>
                <td><%= emp.getEmail() %></td>
                <td><%= emp.getPhone() %></td>
                <td><%= emp.getDepartment() %></td>
                <td><%= emp.getRole() %></td>
                <td><%= emp.getSalary() %></td>
                <td><%= emp.getStatus() %></td>
                <td class="actions">
                    <a href="#" onclick="editEmployee(this); return false;"
                       data-id="<%=emp.getId()%>" data-name="<%=emp.getName()%>" data-email="<%=emp.getEmail()%>"
                       data-phone="<%=emp.getPhone()%>" data-department="<%=emp.getDepartment()%>" data-role="<%=emp.getRole()%>"
                       data-salary="<%=emp.getSalary()%>" data-status="<%=emp.getStatus()%>">Edit</a>
                    <a href="#" onclick="confirmDelete(<%=emp.getId()%>)">Delete</a>
                </td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr><td colspan="8">No employees found.</td></tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>

    <!-- Modal / Form -->
    <div id="employeeModal" class="modal" onclick="if(event.target==this) closeModal();">
        <div class="dialog card">
            <h3 id="modalTitle">Employee</h3>
            <form id="employeeForm" method="post" action="<%=request.getContextPath()%>/employees">
                <input type="hidden" id="formAction" name="action" value="add">
                <input type="hidden" id="idField" name="id" value="">
                <div class="form-row">
                    <div class="form-group"><label>Name</label><input id="name" name="name" type="text" required></div>
                    <div class="form-group"><label>Email</label><input id="email" name="email" type="email" required></div>
                    <div class="form-group"><label>Password (optional)</label><input id="password" name="password" type="password" placeholder="set user password"></div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Phone</label><input id="phone" name="phone" type="text"></div>
                    <div class="form-group"><label>Department</label>
                        <input id="department" name="department" type="text">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group"><label>Role</label><input id="role" name="role" type="text"></div>
                    <div class="form-group"><label>Salary</label><input id="salary" name="salary" type="number" step="0.01"></div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Status</label>
                        <select id="status" name="status">
                            <option value="Active">Active</option>
                            <option value="Inactive">Inactive</option>
                        </select>
                    </div>
                </div>
                <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:10px;">
                    <button type="button" class="secondary" onclick="closeModal()">Cancel</button>
                    <button type="submit">Save</button>
                </div>
            </form>
        </div>
    </div>

</div>
</body>
</html>