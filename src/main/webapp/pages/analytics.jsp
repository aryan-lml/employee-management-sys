<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    request.setAttribute("active", "analytics");
    request.setAttribute("pageTitle", "Reports");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reports — EMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
</head>
<body>
<div class="app">
    <jsp:include page="/pages/layout/sidebar.jsp" />
    <jsp:include page="/pages/layout/topbar.jsp" />
    <main class="main">
        <div class="card">
            <div class="card-header"><div><h2 class="mb-0">Reports & analytics</h2><div class="text-muted">Your workforce at a glance.</div></div></div>
            <div class="stats-grid">
                <div class="stat-card"><div class="stat-icon">&#128101;</div><div><div class="stat-label">Employees</div><div class="stat-value">${totalEmployees}</div></div></div>
                <div class="stat-card"><div class="stat-icon amber">&#9999;</div><div><div class="stat-label">Tasks</div><div class="stat-value">${totalTasks}</div></div></div>
                <div class="stat-card"><div class="stat-icon">&#128197;</div><div><div class="stat-label">Attendance Rows</div><div class="stat-value">${attendanceCount}</div></div></div>
            </div>
        </div>

        <div class="card">
            <h3>Tasks by status</h3>
            <c:choose>
                <c:when test="${empty taskCounts}">
                    <p class="text-muted">No tasks yet.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="data">
                            <thead><tr><th>Status</th><th>Count</th></tr></thead>
                            <tbody>
                            <c:forEach var="entry" items="${taskCounts}">
                                <tr>
                                    <td><span class="badge badge-${fn:toLowerCase(entry.key)}">${entry.key}</span></td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="card">
            <h3>Employees by department</h3>
            <c:choose>
                <c:when test="${empty deptCounts}">
                    <p class="text-muted">No employees yet.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="data">
                            <thead><tr><th>Department</th><th>Headcount</th></tr></thead>
                            <tbody>
                            <c:forEach var="entry" items="${deptCounts}">
                                <tr><td><c:out value="${entry.key}"/></td><td>${entry.value}</td></tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="card">
            <h3>Attendance by status</h3>
            <c:choose>
                <c:when test="${empty attendanceByStatus}">
                    <p class="text-muted">No attendance recorded yet.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="data">
                            <thead><tr><th>Status</th><th>Count</th></tr></thead>
                            <tbody>
                            <c:forEach var="entry" items="${attendanceByStatus}">
                                <tr>
                                    <td><span class="badge badge-${fn:toLowerCase(entry.key)}">${entry.key}</span></td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>
</body>
</html>
