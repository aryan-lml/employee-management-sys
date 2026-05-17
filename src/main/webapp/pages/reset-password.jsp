<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reset password — EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
</head>
<body>
<div class="auth-wrap">
    <div class="auth-card">
        <div class="brand">EMS</div>
        <div class="tagline">Choose a new password</div>
        <h2>Reset password</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <form method="post" action="<%=request.getContextPath()%>/reset-password">
            <input type="hidden" name="token" value="<%= request.getAttribute("token") != null ? request.getAttribute("token") : (request.getParameter("token") != null ? request.getParameter("token") : "") %>" />
            <div class="form-group">
                <label>New password</label>
                <input type="password" name="password" required minlength="6" />
            </div>
            <div class="form-group">
                <label>Confirm new password</label>
                <input type="password" name="confirm" required minlength="6" />
            </div>
            <button class="btn" type="submit">Set new password</button>
        </form>

        <div class="footer-link">
            <a href="<%=request.getContextPath()%>/pages/login.jsp">Back to sign in</a>
        </div>
    </div>
</div>
</body>
</html>
