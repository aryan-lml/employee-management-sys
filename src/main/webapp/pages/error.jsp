<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Error - EMS</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/EMS.css">
    <style>
        body{display:flex;align-items:center;justify-content:center;height:100vh;background:#f5f7fb;margin:0}
        .card{background:#fff;padding:24px;border-radius:10px;box-shadow:0 6px 18px rgba(25,30,50,0.08);width:720px}
        .muted{font-size:13px;color:#6b7280;margin-top:8px}
    </style>
</head>
<body>
<div class="card">
    <h2>An error occurred</h2>
    <p style="color:#b91c1c;"><%= request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") : "Unexpected error." %></p>
    <div class="muted">Go back to <a href="<%=request.getContextPath()%>/pages/login.jsp">login</a> or <a href="<%=request.getContextPath()%>/employees">dashboard</a>.</div>
</div>
</body>
</html>
