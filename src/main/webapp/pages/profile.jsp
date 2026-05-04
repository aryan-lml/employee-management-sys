<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <title>Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
    <style>.container{max-width:900px;margin:20px auto}.card{background:#fff;padding:20px;border-radius:10px;box-shadow:0 6px 18px rgba(25,30,50,0.06);}</style>
</head>
<body>
    <jsp:include page="/pages/navbar.jsp" />
    <div class="container">
        <div class="card">
            <h2>Your Profile</h2>
            <c:choose>
                <c:when test="${not empty employee}">
                    <table>
                        <tr><td>Name</td><td><c:out value="${employee.name}"/></td></tr>
                        <tr><td>Email</td><td><c:out value="${employee.email}"/></td></tr>
                        <tr><td>Phone</td><td><c:out value="${employee.phone}"/></td></tr>
                        <tr><td>Department</td><td><c:out value="${employee.department}"/></td></tr>
                    </table>
                    <form method="post" action="${pageContext.request.contextPath}/profile">
                        <label>Phone</label>
                        <input type="text" name="phone" value="${employee.phone}" />
                        <button type="submit" name="action" value="updatePhone">Update</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <p>No employee record found for your account.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>
