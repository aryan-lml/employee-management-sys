<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <title>Analytics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
    <style>.container{max-width:1100px;margin:20px auto;padding:0 16px}.card{background:#fff;padding:20px;border-radius:10px;box-shadow:0 6px 18px rgba(25,30,50,0.06);}</style>
</head>
<body>
    <jsp:include page="/pages/navbar.jsp" />
    <div class="container">
        <div class="card">
            <h2>Analytics</h2>
            <div>Total employees: <strong>${totalEmployees}</strong></div>
            <div>Attendance records: <strong>${attendanceCount}</strong></div>
        </div>

        <div class="card">
            <h3>Tasks by status</h3>
            <table>
                <thead><tr><th>Status</th><th>Count</th></tr></thead>
                <tbody>
                <c:forEach var="entry" items="${taskCounts}">
                    <tr>
                        <td><c:out value="${entry.key}"/></td>
                        <td><c:out value="${entry.value}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
