<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Forgot password — EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
</head>
<body>
<div class="auth-wrap">
    <div class="auth-card">
        <div class="brand">EMS</div>
        <div class="tagline">Reset your password</div>
        <h2>Forgot password</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("infoMessage") != null) { %>
            <div class="alert alert-success">
                <%= request.getAttribute("infoMessage") %>
                <% if (request.getAttribute("resetLink") != null) { %>
                    <div class="mt-1"><a href="<%= request.getAttribute("resetLink") %>"><%= request.getAttribute("resetLink") %></a></div>
                <% } %>
            </div>
        <% } %>

        <form method="post" action="<%=request.getContextPath()%>/forgot-password">
            <div class="form-group">
                <label>Username</label>
                <input type="text" name="username" required />
            </div>
            <button class="btn" type="submit">Send reset link</button>
        </form>

        <div class="footer-link">
            <a href="<%=request.getContextPath()%>/pages/login.jsp">Back to sign in</a>
        </div>
    </div>
</div>
</body>
</html>
