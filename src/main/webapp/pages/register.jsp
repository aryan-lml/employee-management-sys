<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register — EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
</head>
<body>
<div class="auth-wrap">
    <div class="auth-card">
        <div class="brand">EMS</div>
        <div class="tagline">Employee Management System</div>
        <h2>Create your account</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <form method="post" action="<%=request.getContextPath()%>/auth">
            <input type="hidden" name="action" value="register" />
            <div class="form-group">
                <label>Email (used as username)</label>
                <input type="text" name="username" required />
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" required minlength="6" />
                <small>At least 6 characters.</small>
            </div>
            <button class="btn" type="submit">Register</button>
        </form>

        <div class="footer-link">
            Already have an account?
            <a href="<%=request.getContextPath()%>/pages/login.jsp">Sign in</a>
        </div>
    </div>
</div>
</body>
</html>
