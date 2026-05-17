<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign in — EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
</head>
<body>
<div class="auth-wrap">
    <div class="auth-card">
        <div class="brand">EMS</div>
        <div class="tagline">Employee Management System</div>
        <h2>Welcome back</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("infoMessage") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("infoMessage") %></div>
        <% } %>

        <form method="post" action="<%=request.getContextPath()%>/auth" autocomplete="on">
            <input type="hidden" name="action" value="login" />
            <div class="form-group">
                <label for="username">Username</label>
                <input id="username" type="text" name="username" required autofocus />
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input id="password" type="password" name="password" required />
            </div>
            <button class="btn" type="submit">Sign in</button>
        </form>

        <div class="footer-link">
            <a href="<%=request.getContextPath()%>/pages/forgot-password.jsp">Forgot password?</a>
            &nbsp;·&nbsp;
            <a href="<%=request.getContextPath()%>/pages/register.jsp">Create account</a>
        </div>
    </div>
</div>
</body>
</html>
