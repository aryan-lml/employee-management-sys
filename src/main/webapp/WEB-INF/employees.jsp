<%@ include file="../WEB-INF/sidebar.jsp" %>
<div class="main-content">
    <div class="top-navbar">
        <div class="welcome-message">
            <h3>Employees</h3>
        </div>
        <div class="user-actions">
            <a href="/add-employee" class="btn-primary">+ Add Employee</a>
        </div>
    </div>

    <div class="employees-table">
        <table class="activity-table">
            <thead>
                <tr>
                    <th>Employee ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Department</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>EMP001</td>
                    <td>John Doe</td>
                    <td>john.doe@example.com</td>
                    <td>Engineering</td>
                    <td><span class="status-badge active">Active</span></td>
                    <td>
                        <button class="btn-edit">Edit</button>
                        <button class="btn-delete">Delete</button>
                    </td>
                </tr>
                <tr>
                    <td>EMP002</td>
                    <td>Jane Smith</td>
                    <td>jane.smith@example.com</td>
                    <td>Marketing</td>
                    <td><span class="status-badge inactive">Inactive</span></td>
                    <td>
                        <button class="btn-edit">Edit</button>
                        <button class="btn-delete">Delete</button>
                    </td>
                </tr>
                <!-- More rows -->
            </tbody>
        </table>
    </div>
</div>