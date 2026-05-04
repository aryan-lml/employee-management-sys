<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>EMS — Login</title>
  <style>
    body{font-family:Arial,Helvetica,sans-serif;background:#f5f7fa}
    .wrap{max-width:380px;margin:80px auto;padding:28px;background:#fff;border-radius:12px;box-shadow:0 8px 24px rgba(2,6,23,0.08)}
    input{width:100%;padding:10px;margin:8px 0;border-radius:8px;border:1px solid #e6e9ef}
    button{width:100%;padding:10px;border-radius:8px;background:#4f46e5;color:#fff;border:none}
    .error{color:#ef4444;margin-bottom:10px}
  </style>
</head>
<body>
  <div class="wrap">
    <h2>Sign in to EMS</h2>
    <form action="${pageContext.request.contextPath}/auth" method="post">
      <c:if test="${not empty error}">
        <div class="error">${error}</div>
      </c:if>
      <label>Username</label>
      <input type="text" name="username" required />
      <label>Password</label>
      <input type="password" name="password" required />
      <button type="submit">Sign in</button>
    </form>
  </div>
</body>
</html>