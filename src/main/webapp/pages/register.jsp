<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register - EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
    <style>
        body{display:flex;align-items:center;justify-content:center;height:100vh;background:#f5f7fb;margin:0}
        .card{background:#fff;padding:24px;border-radius:10px;box-shadow:0 6px 18px rgba(25,30,50,0.08);width:360px}
        input{width:100%;padding:10px;margin:8px 0;border-radius:6px;border:1px solid #e0e6ef}
        button{width:100%;padding:10px;border-radius:6px;border:none;background:#2b7cff;color:#fff}
        .muted{font-size:13px;color:#6b7280;margin-top:8px}
    </style>
    </head>
<body>
<div class="card">
    <h2>Create account</h2>
    <form method="post" action="<%=request.getContextPath()%>/auth">
        <input type="hidden" name="action" value="register" />
        <label>Username</label>
        <input type="text" name="username" required />
        <label>Password</label>
        <input type="password" name="password" required />
        <div style="color:red"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></div>
        <button type="submit">Register</button>
    </form>
    <div class="muted">Already have an account? <a href="<%=request.getContextPath()%>/pages/login.jsp">Login</a></div>
</div>
</body>
</html>
