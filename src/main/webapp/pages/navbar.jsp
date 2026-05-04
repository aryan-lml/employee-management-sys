<!-- JSTL is not required here; use EL-only to avoid missing taglib errors -->
<nav style="background:#fff;border-bottom:1px solid #e6eef8;padding:12px 18px;display:flex;align-items:center;gap:12px;">
    <div style="font-weight:600;font-size:18px;color:#2b7cff">EMS</div>
    <div style="display:flex;gap:8px;align-items:center;margin-left:16px;">
        <a href="${pageContext.request.contextPath}/dashboard" style="text-decoration:none;color:#222;padding:6px 8px;">Dashboard</a>
        <a href="${pageContext.request.contextPath}/employees" style="text-decoration:none;color:#222;padding:6px 8px;${empty sessionScope.userObj || sessionScope.userObj.role ne 'ADMIN' ? 'display:none;' : ''}">Employees</a>
        <a href="${pageContext.request.contextPath}/tasks" style="text-decoration:none;color:#222;padding:6px 8px;">Tasks</a>
        <a href="${pageContext.request.contextPath}/attendance" style="text-decoration:none;color:#222;padding:6px 8px;">Attendance</a>
        <a href="${pageContext.request.contextPath}/profile" style="text-decoration:none;color:#222;padding:6px 8px;">Profile</a>
        <a href="${pageContext.request.contextPath}/admin-tasks" style="text-decoration:none;color:#222;padding:6px 8px;${empty sessionScope.userObj || sessionScope.userObj.role ne 'ADMIN' ? 'display:none;' : ''}">Admin Tasks</a>
    </div>

    <div style="margin-left:auto;display:flex;align-items:center;gap:12px;">
        <div style="color:#374151;${empty sessionScope.userObj ? 'display:none;' : ''}">Welcome, <strong>${sessionScope.userObj.username}</strong></div>
        <a href="${pageContext.request.contextPath}/logout" style="${empty sessionScope.userObj ? 'display:none;' : ''}"><button style="background:#f97316;color:#fff;border:none;padding:8px 10px;border-radius:6px;cursor:pointer;">Logout</button></a>
        <a href="${pageContext.request.contextPath}/pages/login.jsp" style="${not empty sessionScope.userObj ? 'display:none;' : ''}"><button style="background:#2b7cff;color:#fff;border:none;padding:8px 10px;border-radius:6px;cursor:pointer;">Login</button></a>
    </div>
</nav>
