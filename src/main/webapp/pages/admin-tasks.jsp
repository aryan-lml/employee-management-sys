<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <title>Admin Tasks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
    <style>.container{max-width:1100px;margin:20px auto;padding:0 16px}.card{background:#fff;padding:20px;border-radius:10px;box-shadow:0 6px 18px rgba(25,30,50,0.06);}</style>
</head>
<body>
    <jsp:include page="/pages/navbar.jsp" />
    <div class="container">
        <div class="card">
            <h2>Admin Task Management</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin-tasks">
                <div style="display:flex;gap:8px;align-items:center">
                    <input type="text" name="title" placeholder="Title" required />
                    <input type="text" name="assignedToEmail" placeholder="Assign to (employee email)" />
                    <input type="date" name="dueDate" />
                    <button type="submit" name="action" value="create">Create</button>
                </div>
            </form>
        </div>
        <div class="card">
            <h3>All Tasks</h3>
            <table style="width:100%;border-collapse:collapse">
                <thead><tr><th>Title</th><th>Assigned To</th><th>Status</th><th>Due</th></tr></thead>
                <tbody>
                <c:forEach var="t" items="${tasks}">
                    <tr>
                        <td><c:out value="${t.title}"/></td>
                        <td><c:out value="${t.assignedTo}"/></td>
                        <td><c:out value="${t.status}"/></td>
                        <td><c:out value="${t.dueDate}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
