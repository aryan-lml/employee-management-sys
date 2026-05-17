<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    request.setAttribute("active", "admin-attendance");
    request.setAttribute("pageTitle", "Attendance — Admin");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Attendance — Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
    <script>
        function openEdit(row) {
            document.getElementById('editId').value = row.dataset.id;
            document.getElementById('editStatus').value = row.dataset.status || 'PRESENT';
            document.getElementById('editNote').value = row.dataset.note || '';
            document.getElementById('attendanceModal').classList.add('open');
        }
        function closeEdit() { document.getElementById('attendanceModal').classList.remove('open'); }
    </script>
</head>
<body>
<div class="app">
    <jsp:include page="/pages/layout/sidebar.jsp" />
    <jsp:include page="/pages/layout/topbar.jsp" />
    <main class="main">
        <div class="card">
            <div class="card-header">
                <div>
                    <h2 class="mb-0">Attendance records</h2>
                    <div class="text-muted">Filter by employee or date range. Click "Edit" to override a record.</div>
                </div>
                <a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/admin-attendance">Reset filters</a>
            </div>
            <form method="get" action="${pageContext.request.contextPath}/admin-attendance" class="form-inline">
                <div class="form-group">
                    <label>Employee</label>
                    <select name="employeeId">
                        <option value="">All</option>
                        <c:forEach var="e" items="${employees}">
                            <option value="${e.id}" <c:if test="${fEmployeeId == e.id.toString()}">selected</c:if>>${e.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group"><label>From</label><input type="date" name="from" value="${fFrom}"></div>
                <div class="form-group"><label>To</label><input type="date" name="to" value="${fTo}"></div>
                <div class="form-group"><label>&nbsp;</label><button class="btn" type="submit">Filter</button></div>
            </form>
        </div>

        <div class="card">
            <div class="table-wrap">
                <table class="data">
                    <thead><tr><th>Employee</th><th>Check In</th><th>Check Out</th><th>Hours</th><th>Status</th><th>Note</th><th>Actions</th></tr></thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty rows}">
                            <tr class="empty-row"><td colspan="7">No attendance records match the filter.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="r" items="${rows}">
                                <tr>
                                    <td><strong>${r.employeeName}</strong></td>
                                    <td>${r.checkIn}</td>
                                    <td>${r.checkOut != null ? r.checkOut : '—'}</td>
                                    <td>${r.workedHours != null ? r.workedHours : '—'}</td>
                                    <td><span class="badge badge-${fn:toLowerCase(r.status)}">${r.status}</span></td>
                                    <td>${r.note != null ? r.note : '—'}</td>
                                    <td class="actions-cell">
                                        <button class="btn btn-secondary btn-sm"
                                                data-id="${r.id}" data-status="${r.status}" data-note="${fn:escapeXml(r.note)}"
                                                onclick="openEdit(this)">Edit</button>
                                        <form method="post" action="${pageContext.request.contextPath}/admin-attendance" onsubmit="return confirm('Delete this attendance record?')">
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="id" value="${r.id}"/>
                                            <button class="btn btn-danger btn-sm" type="submit">Delete</button>
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

        <div id="attendanceModal" class="modal-backdrop" onclick="if(event.target===this) closeEdit()">
            <div class="modal">
                <header><h3>Edit attendance</h3><button class="close" type="button" onclick="closeEdit()">&times;</button></header>
                <form method="post" action="${pageContext.request.contextPath}/admin-attendance">
                    <div class="body">
                        <input type="hidden" name="action" value="update"/>
                        <input type="hidden" id="editId" name="id" value=""/>
                        <div class="form-group">
                            <label>Status</label>
                            <select id="editStatus" name="status">
                                <option value="PRESENT">Present</option>
                                <option value="LATE">Late</option>
                                <option value="HALF_DAY">Half day</option>
                                <option value="ABSENT">Absent</option>
                            </select>
                        </div>
                        <div class="form-group"><label>Note</label><input id="editNote" name="note" type="text"></div>
                    </div>
                    <div class="footer">
                        <button type="button" class="btn btn-secondary" onclick="closeEdit()">Cancel</button>
                        <button type="submit" class="btn">Save</button>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>
</body>
</html>
