<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Choose a password — EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
</head>
<body>
<div class="auth-wrap">
    <div class="auth-card">
        <div class="brand">EMS</div>
        <div class="tagline">Welcome — please set your own password</div>
        <h2>Replace your temporary password</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <div class="alert alert-info">
            Your administrator created this account with a temporary password. Choose a new one
            to continue. You'll use the new password on every future sign-in.
        </div>

        <form method="post" action="<%=request.getContextPath()%>/force-password-change" autocomplete="off">
            <div class="form-group">
                <label>Temporary password (from your admin)</label>
                <input type="password" name="currentPassword" required>
            </div>
            <div class="form-group">
                <label>New password</label>
                <input type="password" name="newPassword" minlength="6" required>
                <small>At least 6 characters. Must differ from the temporary password.</small>
            </div>
            <div class="form-group">
                <label>Confirm new password</label>
                <input type="password" name="confirmPassword" minlength="6" required>
            </div>
            <button class="btn" type="submit">Set new password</button>
        </form>

        <div class="footer-link">
            <a href="<%=request.getContextPath()%>/logout">Cancel and sign out</a>
        </div>
    </div>
</div>
</body>
</html>
