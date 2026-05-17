<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    request.setAttribute("active", "tasks");
    request.setAttribute("pageTitle", "My Tasks");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Tasks — EMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
</head>
<body>
<div class="app">
    <jsp:include page="/pages/layout/sidebar.jsp" />
    <jsp:include page="/pages/layout/topbar.jsp" />
    <main class="main">
        <div class="card">
            <div class="card-header">
                <div>
                    <h2 class="mb-0">My tasks</h2>
                    <div class="text-muted">Update task status as you make progress.</div>
                </div>
            </div>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                        <tr><th>Title</th><th>Description</th><th>Status</th><th>Due</th><th>Update</th></tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty tasks}">
                            <tr class="empty-row"><td colspan="5">You have no tasks assigned.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="t" items="${tasks}">
                                <c:set var="cls">${fn:toLowerCase(t.status)}</c:set>
                                <tr>
                                    <td><strong><c:out value="${t.title}"/></strong></td>
                                    <td><c:out value="${t.description}"/></td>
                                    <td><span class="badge badge-${fn:toLowerCase(t.status)}">${t.status}</span></td>
                                    <td>${t.dueDate}</td>
                                    <td>
                                        <form method="post" action="${pageContext.request.contextPath}/tasks" class="flex flex-gap">
                                            <input type="hidden" name="taskId" value="${t.id}"/>
                                            <select name="status">
                                                <option value="PENDING"     ${t.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                                <option value="IN_PROGRESS" ${t.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                                                <option value="COMPLETED"   ${t.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                                            </select>
                                            <button class="btn btn-sm" type="submit" name="action" value="updateStatus">Update</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>
</body>
</html>
