<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.Employeemanagementsystem.dao.EmployeeDao" %>
<%@ page import="com.Employeemanagementsystem.dao.NotificationDao" %>
<%@ page import="com.Employeemanagementsystem.model.Employee" %>
<%@ page import="com.Employeemanagementsystem.model.Notification" %>
<%@ page import="com.Employeemanagementsystem.model.User" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%
    String _pageTitle = (String) request.getAttribute("pageTitle");
    if (_pageTitle == null) _pageTitle = "Employee Management System";

    User _user = (User) session.getAttribute("userObj");
    int _unread = 0;
    java.util.List<Notification> _notifs = java.util.Collections.emptyList();
    if (_user != null && !"ADMIN".equalsIgnoreCase(_user.getRole())) {
        try {
            EmployeeDao _ed = new EmployeeDao();
            Employee _emp = _ed.getEmployeeByEmail(_user.getUsername());
            if (_emp != null) {
                NotificationDao _nd = new NotificationDao();
                _unread = _nd.unreadCount(_emp.getId());
                _notifs = _nd.forEmployee(_emp.getId(), 8);
            }
        } catch (Exception ignore) { /* swallow — topbar must never break the page */ }
    }
    String _initial = (_user != null && _user.getUsername() != null && !_user.getUsername().isEmpty())
            ? _user.getUsername().substring(0,1).toUpperCase() : "?";
    SimpleDateFormat _fmt = new SimpleDateFormat("MMM dd, HH:mm");
%>
<header class="topbar">
    <div class="page-title"><%= _pageTitle %></div>
    <div class="spacer"></div>

    <% if (_user != null && !"ADMIN".equalsIgnoreCase(_user.getRole())) { %>
    <div class="notif-wrap">
        <button class="notif-btn" type="button" onclick="document.getElementById('notifPanel').classList.toggle('open')">
            &#128276;
            <% if (_unread > 0) { %><span class="notif-count"><%= _unread %></span><% } %>
        </button>
        <div id="notifPanel" class="notif-panel">
            <header>
                <span>Notifications</span>
                <% if (_unread > 0) { %>
                    <a class="btn-ghost" href="<%=request.getContextPath()%>/notifications">Mark all read</a>
                <% } %>
            </header>
            <% if (_notifs.isEmpty()) { %>
                <div class="notif-empty">You're all caught up.</div>
            <% } else {
                for (Notification n : _notifs) { %>
                <a class="notif-item <%= n.isRead() ? "" : "unread" %>"
                   style="display:block;color:inherit;text-decoration:none;"
                   href="<%=request.getContextPath()%>/notifications<%= n.getLink() != null ? "?to=" + n.getLink() : "" %>">
                    <div class="msg"><%= n.getMessage() %></div>
                    <div class="meta"><%= n.getCreatedAt() != null ? _fmt.format(n.getCreatedAt()) : "" %></div>
                </a>
            <%  }
            } %>
        </div>
    </div>
    <% } %>

    <div class="user-chip">
        <div class="avatar"><%= _initial %></div>
        <span class="uname"><%= _user != null ? _user.getUsername() : "" %></span>
    </div>
</header>
