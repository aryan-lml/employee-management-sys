<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <title>Attendance</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
    <style>.container{max-width:1100px;margin:20px auto;padding:0 16px}.card{background:#fff;padding:20px;border-radius:10px;box-shadow:0 6px 18px rgba(25,30,50,0.06);}</style>
</head>
<body>
    <jsp:include page="/pages/navbar.jsp" />
    <div class="container">
        <div class="card">
            <h2>Attendance</h2>
            <form method="post" action="${pageContext.request.contextPath}/attendance">
                <button type="submit" name="action" value="checkin">Check In</button>
                <button type="submit" name="action" value="checkout">Check Out</button>
            </form>
            <c:if test="${not empty requestScope.errorMessage}">
                <div style="color:#b91c1c">${requestScope.errorMessage}</div>
            </c:if>
            <c:if test="${not empty requestScope.infoMessage}">
                <div style="color:#059669">${requestScope.infoMessage}</div>
            </c:if>
        </div>

        <div class="card">
            <h3>History</h3>
            <table style="width:100%;border-collapse:collapse">
                <thead><tr><th>Check In</th><th>Check Out</th><th>Hours</th><th>Status</th></tr></thead>
                <tbody>
                <c:forEach var="a" items="${attendanceList}">
                    <tr>
                        <td><c:out value="${a.checkIn}"/></td>
                        <td><c:out value="${a.checkOut}"/></td>
                        <td><c:out value="${a.workedHours}"/></td>
                        <td><c:out value="${a.status}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
