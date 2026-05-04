<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <title>Tasks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
    <style>.container{max-width:1100px;margin:20px auto;padding:0 16px}.card{background:#fff;padding:20px;border-radius:10px;box-shadow:0 6px 18px rgba(25,30,50,0.06);}</style>
</head>
<body>
    <jsp:include page="/pages/navbar.jsp" />
    <div class="container">
        <div class="card">
            <h2>Tasks</h2>
            <table style="width:100%;border-collapse:collapse">
                <thead><tr><th>Title</th><th>Description</th><th>Status</th><th>Due</th><th>Action</th></tr></thead>
                <tbody>
                <c:forEach var="t" items="${tasks}">
                    <tr>
                        <td><c:out value="${t.title}"/></td>
                        <td><c:out value="${t.description}"/></td>
                        <td><c:out value="${t.status}"/></td>
                        <td><c:out value="${t.dueDate}"/></td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/tasks" style="display:inline">
                                <input type="hidden" name="taskId" value="${t.id}"/>
                                <select name="status">
                                    <option value="PENDING" ${t.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="IN_PROGRESS" ${t.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                                    <option value="COMPLETED" ${t.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                                </select>
                                <button type="submit" name="action" value="updateStatus">Update</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
