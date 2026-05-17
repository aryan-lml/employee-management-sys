<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    request.setAttribute("active", "profile");
    request.setAttribute("pageTitle", "Profile");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Profile — EMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
</head>
<body>
<div class="app">
    <jsp:include page="/pages/layout/sidebar.jsp" />
    <jsp:include page="/pages/layout/topbar.jsp" />
    <main class="main">
        <c:if test="${not empty requestScope.error}">
            <div class="alert alert-error">${requestScope.error}</div>
        </c:if>
        <c:if test="${not empty requestScope.infoMessage}">
            <div class="alert alert-success">${requestScope.infoMessage}</div>
        </c:if>

        <div class="card">
            <div class="profile-head">
                <div class="profile-avatar">${fn:toUpperCase(fn:substring(sessionScope.userObj.username,0,1))}</div>
                <div class="profile-info">
                    <h2 class="mb-0">${employee != null ? employee.name : sessionScope.userObj.username}</h2>
                    <div class="muted">
                        ${employee != null ? employee.email : sessionScope.userObj.username}
                        <c:if test="${employee != null}"> · <c:out value="${employee.department}"/> · <c:out value="${employee.role}"/></c:if>
                        <c:if test="${sessionScope.userObj.role == 'ADMIN'}">
                            &nbsp;<span class="badge badge-admin">ADMIN</span>
                        </c:if>
                    </div>
                </div>
            </div>

            <c:choose>
                <c:when test="${employee != null}">
                    <h3>Update contact details</h3>
                    <form method="post" action="${pageContext.request.contextPath}/profile">
                        <input type="hidden" name="action" value="updateProfile"/>
                        <div class="form-grid">
                            <div class="form-group"><label>Full name</label><input type="text" name="name" value="${fn:escapeXml(employee.name)}" required></div>
                            <div class="form-group"><label>Phone</label><input type="text" name="phone" value="${fn:escapeXml(employee.phone)}"></div>
                            <div class="form-group"><label>Email</label><input type="text" value="${employee.email}" disabled></div>
                            <div class="form-group"><label>Department</label><input type="text" value="${employee.department}" disabled></div>
                        </div>
                        <div class="form-actions">
                            <button class="btn" type="submit">Save</button>
                        </div>
                    </form>
                </c:when>
                <c:otherwise>
                    <p class="text-muted">You are signed in as an administrator. Use the form below to change your password.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="card">
            <h3>Change password</h3>
            <form method="post" action="${pageContext.request.contextPath}/profile">
                <input type="hidden" name="action" value="changePassword"/>
                <div class="form-grid">
                    <div class="form-group"><label>Current password</label><input type="password" name="currentPassword" required></div>
                    <div class="form-group"><label>New password</label><input type="password" name="newPassword" minlength="6" required></div>
                    <div class="form-group"><label>Confirm</label><input type="password" name="confirmPassword" minlength="6" required></div>
                </div>
                <div class="form-actions">
                    <button class="btn" type="submit">Change password</button>
                </div>
            </form>
        </div>
    </main>
</div>
</body>
</html>
