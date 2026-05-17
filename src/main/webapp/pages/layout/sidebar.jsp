<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Sidebar fragment. Reads optional `active` request attribute to highlight the current item. --%>
<%
    String _active = (String) request.getAttribute("active");
    if (_active == null) _active = "";
    boolean _isAdmin = false;
    Object _u = session.getAttribute("userObj");
    if (_u instanceof com.Employeemanagementsystem.model.User) {
        _isAdmin = "ADMIN".equalsIgnoreCase(((com.Employeemanagementsystem.model.User) _u).getRole());
    }
%>
<aside class="sidebar">
    <div class="brand">EMS<span>.</span></div>
    <div class="nav-section">Workspace</div>

    <% if (_isAdmin) { %>
        <a class="nav-item <%= "dashboard".equals(_active) ? "active" : "" %>"
           href="<%=request.getContextPath()%>/dashboard">
            <span class="icon">&#9632;</span><span class="label">Dashboard</span>
        </a>
        <a class="nav-item <%= "employees".equals(_active) ? "active" : "" %>"
           href="<%=request.getContextPath()%>/employees">
            <span class="icon">&#128101;</span><span class="label">Employees</span>
        </a>
        <a class="nav-item <%= "admin-tasks".equals(_active) ? "active" : "" %>"
           href="<%=request.getContextPath()%>/admin-tasks">
            <span class="icon">&#9999;</span><span class="label">Tasks</span>
        </a>
        <a class="nav-item <%= "admin-attendance".equals(_active) ? "active" : "" %>"
           href="<%=request.getContextPath()%>/admin-attendance">
            <span class="icon">&#128197;</span><span class="label">Attendance</span>
        </a>
        <a class="nav-item <%= "analytics".equals(_active) ? "active" : "" %>"
           href="<%=request.getContextPath()%>/analytics">
            <span class="icon">&#9974;</span><span class="label">Reports</span>
        </a>
    <% } else { %>
        <a class="nav-item <%= "employee-dashboard".equals(_active) ? "active" : "" %>"
           href="<%=request.getContextPath()%>/employee-dashboard">
            <span class="icon">&#9632;</span><span class="label">Dashboard</span>
        </a>
        <a class="nav-item <%= "tasks".equals(_active) ? "active" : "" %>"
           href="<%=request.getContextPath()%>/tasks">
            <span class="icon">&#9999;</span><span class="label">My Tasks</span>
        </a>
        <a class="nav-item <%= "attendance".equals(_active) ? "active" : "" %>"
           href="<%=request.getContextPath()%>/attendance">
            <span class="icon">&#128197;</span><span class="label">Attendance</span>
        </a>
    <% } %>

    <div class="nav-section">Account</div>
    <a class="nav-item <%= "profile".equals(_active) ? "active" : "" %>"
       href="<%=request.getContextPath()%>/profile">
        <span class="icon">&#128100;</span><span class="label">Profile</span>
    </a>
    <a class="nav-item" href="<%=request.getContextPath()%>/logout">
        <span class="icon">&#8629;</span><span class="label">Log out</span>
    </a>
</aside>
