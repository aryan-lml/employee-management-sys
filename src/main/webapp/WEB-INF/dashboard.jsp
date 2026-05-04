<%@ include file="sidebar.jsp" %>
<div class="main-content">
    <div class="top-navbar">
        <div class="welcome-message">
            <h3>Welcome, <span id="user-name">User</span>!</h3>
        </div>
        <div class="user-actions">
            <img src="/path/to/avatar.png" alt="User Avatar" class="user-avatar">
            <a href="/logout" class="logout-button">Logout</a>
        </div>
    </div>

    <div class="dashboard">
        <div class="stats-cards">
            <div class="card">
                <div class="card-icon">👥</div>
                <div class="card-content">
                    <h2>150</h2>
                    <p>Total Employees</p>
                </div>
            </div>
            <div class="card">
                <div class="card-icon">✅</div>
                <div class="card-content">
                    <h2>120</h2>
                    <p>Active Employees</p>
                </div>
            </div>
            <div class="card">
                <div class="card-icon">📋</div>
                <div class="card-content">
                    <h2>25</h2>
                    <p>Tasks Pending</p>
                </div>
            </div>
            <div class="card">
                <div class="card-icon">📅</div>
                <div class="card-content">
                    <h2>80%</h2>
                    <p>Attendance Today</p>
                </div>
            </div>
        </div>

        <div class="charts-section">
            <div class="chart-placeholder">Tasks Status Chart</div>
            <div class="chart-placeholder">Attendance Summary Chart</div>
        </div>

        <div class="recent-activity">
            <h3>Recent Activity</h3>
            <table class="activity-table">
                <thead>
                    <tr>
                        <th>Employee</th>
                        <th>Action</th>
                        <th>Status</th>
                        <th>Options</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>John Doe</td>
                        <td>Updated Profile</td>
                        <td><span class="status-badge active">Active</span></td>
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
</div>