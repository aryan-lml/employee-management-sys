<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    request.setAttribute("active", "attendance");
    request.setAttribute("pageTitle", "Attendance");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Attendance — EMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
</head>
<body>
<div class="app">
    <jsp:include page="/pages/layout/sidebar.jsp" />
    <jsp:include page="/pages/layout/topbar.jsp" />
    <main class="main">
        <c:if test="${not empty requestScope.errorMessage}">
            <div class="alert alert-error">${requestScope.errorMessage}</div>
        </c:if>
        <c:if test="${not empty requestScope.infoMessage}">
            <div class="alert alert-success">${requestScope.infoMessage}</div>
        </c:if>

        <div class="card">
            <div class="card-header">
                <div>
                    <h2 class="mb-0">Today</h2>
                    <div class="text-muted">Tap below to check in or out for the day.</div>
                </div>
            </div>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">&#9200;</div>
                    <div>
                        <div class="stat-label">Check in</div>
                        <div class="stat-value">${todayAttendance != null ? todayAttendance.checkIn : '—'}</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon green">&#10003;</div>
                    <div>
                        <div class="stat-label">Check out</div>
                        <div class="stat-value">${todayAttendance != null && todayAttendance.checkOut != null ? todayAttendance.checkOut : '—'}</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon amber">&#9201;</div>
                    <div>
                        <div class="stat-label">Hours worked</div>
                        <div class="stat-value">${todayAttendance != null && todayAttendance.workedHours != null ? todayAttendance.workedHours : '—'}</div>
                    </div>
                </div>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/attendance" class="flex flex-gap mt-2">
                <button class="btn btn-success" type="submit" name="action" value="checkin"
                        <c:if test="${todayAttendance != null}">disabled</c:if>>Check in</button>
                <button class="btn btn-warn" type="submit" name="action" value="checkout"
                        <c:if test="${todayAttendance == null || todayAttendance.checkOut != null}">disabled</c:if>>Check out</button>
            </form>
        </div>

        <div class="card">
            <div class="card-header"><h3 class="mb-0">History</h3></div>
            <div class="table-wrap">
                <table class="data">
                    <thead><tr><th>Check In</th><th>Check Out</th><th>Hours</th><th>Status</th></tr></thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty attendanceList}">
                            <tr class="empty-row"><td colspan="4">No attendance records yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${attendanceList}">
                                <tr>
                                    <td>${a.checkIn}</td>
                                    <td>${a.checkOut != null ? a.checkOut : '—'}</td>
                                    <td>${a.workedHours != null ? a.workedHours : '—'}</td>
                                    <td><span class="badge badge-${fn:toLowerCase(a.status)}">${a.status}</span></td>
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
