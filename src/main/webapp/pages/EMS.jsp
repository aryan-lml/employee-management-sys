<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Employeemanagementsystem.model.Employee" %>
<%@ page import="com.Employeemanagementsystem.model.User" %>
<%
    request.setAttribute("active", "employees");
    request.setAttribute("pageTitle", "Employees");
    User _user = (User) session.getAttribute("userObj");
    String _search     = request.getAttribute("search")     != null ? (String) request.getAttribute("search")     : "";
    String _department = request.getAttribute("department") != null ? (String) request.getAttribute("department") : "";
    String _status     = request.getAttribute("status")     != null ? (String) request.getAttribute("status")     : "";
    String _formError  = (String) request.getAttribute("formError");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Employees — EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
    <script>
        function openModal(edit) {
            document.getElementById('employeeModal').classList.add('open');
            if (!edit) {
                document.getElementById('employeeForm').reset();
                document.getElementById('formAction').value = 'add';
                document.getElementById('idField').value = '';
                document.getElementById('modalTitle').textContent = 'Add Employee';
                document.getElementById('passwordRow').style.display = '';
                document.getElementById('email').readOnly = false;
            }
        }
        function closeModal() { document.getElementById('employeeModal').classList.remove('open'); }
        function editEmployee(e) {
            document.getElementById('formAction').value = 'update';
            document.getElementById('idField').value = e.dataset.id;
            document.getElementById('name').value = e.dataset.name || '';
            document.getElementById('email').value = e.dataset.email || '';
            document.getElementById('email').readOnly = true;
            document.getElementById('phone').value = e.dataset.phone || '';
            document.getElementById('department').value = e.dataset.department || '';
            document.getElementById('role').value = e.dataset.role || '';
            document.getElementById('salary').value = e.dataset.salary || '';
            document.getElementById('status').value = e.dataset.status || 'Active';
            document.getElementById('modalTitle').textContent = 'Edit Employee';
            document.getElementById('passwordRow').style.display = 'none';
            document.getElementById('employeeModal').classList.add('open');
        }
        function confirmDelete(id, name) {
            if (confirm('Delete employee "' + name + '"? This cannot be undone.')) {
                window.location = '<%=request.getContextPath()%>/employees?action=delete&id=' + id;
            }
        }
    </script>
</head>
<body>
<div class="app">
    <jsp:include page="/pages/layout/sidebar.jsp" />
    <jsp:include page="/pages/layout/topbar.jsp" />
    <main class="main">
        <% if (_formError != null) { %>
            <div class="alert alert-error"><%= _formError %></div>
        <% } %>

        <div class="card">
            <div class="card-header">
                <div>
                    <h2 class="mb-0">Employees</h2>
                    <div class="text-muted">Employees you manage. Adding a password issues login credentials to the employee.</div>
                </div>
                <button class="btn" onclick="openModal(false)">+ Add Employee</button>
            </div>

            <form method="get" action="<%=request.getContextPath()%>/employees" class="form-inline">
                <div class="form-group">
                    <label>Search</label>
                    <input type="search" name="search" placeholder="Name or email" value="<%= _search %>"/>
                </div>
                <div class="form-group">
                    <label>Department</label>
                    <select name="department">
                        <option value="">All</option>
                        <% for (String d : new String[]{"HR","Engineering","Sales","Finance","Operations"}) { %>
                            <option value="<%=d%>" <%= d.equals(_department) ? "selected" : "" %>><%= d %></option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Status</label>
                    <select name="status">
                        <option value="">Any</option>
                        <option value="Active"   <%= "Active".equals(_status)   ? "selected" : "" %>>Active</option>
                        <option value="Inactive" <%= "Inactive".equals(_status) ? "selected" : "" %>>Inactive</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>&nbsp;</label>
                    <button class="btn btn-secondary" type="submit">Filter</button>
                </div>
            </form>
        </div>

        <div class="card">
            <div class="table-wrap">
                <table class="data">
                    <thead>
                        <tr><th>Name</th><th>Email</th><th>Phone</th><th>Department</th><th>Role</th><th>Salary</th><th>Status</th><th>Login</th><th>Actions</th></tr>
                    </thead>
                    <tbody>
                    <%
                        List<Employee> employees = (List<Employee>) request.getAttribute("employees");
                        if (employees == null || employees.isEmpty()) {
                    %>
                        <tr class="empty-row"><td colspan="9">You have no employees yet. Click "+ Add Employee" to create one.</td></tr>
                    <%
                        } else {
                            for (Employee emp : employees) {
                                String statusClass = "Active".equalsIgnoreCase(emp.getStatus()) ? "badge-active" : "badge-inactive";
                    %>
                        <tr>
                            <td><strong><%= emp.getName() %></strong></td>
                            <td><%= emp.getEmail() %></td>
                            <td><%= emp.getPhone() != null ? emp.getPhone() : "—" %></td>
                            <td><%= emp.getDepartment() != null ? emp.getDepartment() : "—" %></td>
                            <td><%= emp.getRole() != null ? emp.getRole() : "—" %></td>
                            <td><%= String.format("%,.2f", emp.getSalary()) %></td>
                            <td><span class="badge <%= statusClass %>"><%= emp.getStatus() != null ? emp.getStatus() : "—" %></span></td>
                            <td><%= emp.getUserId() != null ? "<span class='badge badge-user'>Enabled</span>" : "—" %></td>
                            <td class="actions-cell">
                                <button class="btn btn-secondary btn-sm"
                                        data-id="<%=emp.getId()%>"
                                        data-name="<%=emp.getName()%>"
                                        data-email="<%=emp.getEmail()%>"
                                        data-phone="<%=emp.getPhone() != null ? emp.getPhone() : ""%>"
                                        data-department="<%=emp.getDepartment() != null ? emp.getDepartment() : ""%>"
                                        data-role="<%=emp.getRole() != null ? emp.getRole() : ""%>"
                                        data-salary="<%=emp.getSalary()%>"
                                        data-status="<%=emp.getStatus() != null ? emp.getStatus() : "Active"%>"
                                        onclick="editEmployee(this)">Edit</button>
                                <button class="btn btn-danger btn-sm" onclick="confirmDelete(<%=emp.getId()%>, '<%=emp.getName().replace("'","\\'")%>')">Delete</button>
                            </td>
                        </tr>
                    <% }} %>
                    </tbody>
                </table>
            </div>
        </div>

        <%-- Modal --%>
        <div id="employeeModal" class="modal-backdrop" onclick="if(event.target===this) closeModal()">
            <div class="modal">
                <header>
                    <h3 id="modalTitle">Add Employee</h3>
                    <button type="button" class="close" onclick="closeModal()">&times;</button>
                </header>
                <form id="employeeForm" method="post" action="<%=request.getContextPath()%>/employees">
                    <div class="body">
                        <input type="hidden" id="formAction" name="action" value="add">
                        <input type="hidden" id="idField" name="id" value="">
                        <div class="form-grid">
                            <div class="form-group"><label>Name *</label><input id="name" name="name" type="text" required></div>
                            <div class="form-group"><label>Email *</label><input id="email" name="email" type="email" required></div>
                            <div class="form-group" id="passwordRow"><label>Password (creates login)</label><input id="password" name="password" type="password" placeholder="Min 6 chars — optional"></div>
                            <div class="form-group"><label>Phone</label><input id="phone" name="phone" type="text" placeholder="e.g. 9812345678"></div>
                            <div class="form-group"><label>Department</label>
                                <select id="department" name="department">
                                    <option value="">Select…</option>
                                    <option value="HR">HR</option>
                                    <option value="Engineering">Engineering</option>
                                    <option value="Sales">Sales</option>
                                    <option value="Finance">Finance</option>
                                    <option value="Operations">Operations</option>
                                </select>
                            </div>
                            <div class="form-group"><label>Role / Title</label><input id="role" name="role" type="text"></div>
                            <div class="form-group"><label>Salary</label><input id="salary" name="salary" type="number" step="0.01" min="0"></div>
                            <div class="form-group"><label>Status</label>
                                <select id="status" name="status">
                                    <option value="Active">Active</option>
                                    <option value="Inactive">Inactive</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="footer">
                        <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                        <button type="submit" class="btn">Save</button>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>
</body>
</html>
