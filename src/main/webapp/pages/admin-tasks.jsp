<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    request.setAttribute("active", "admin-tasks");
    request.setAttribute("pageTitle", "Tasks");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Tasks — EMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
    <script>
        function openTaskModal(edit, row) {
            const m = document.getElementById('taskModal');
            const form = document.getElementById('taskForm');
            form.reset();
            document.getElementById('taskAction').value = edit ? 'update' : 'create';
            document.getElementById('taskId').value = edit ? row.dataset.id : '';
            if (edit) {
                document.getElementById('title').value = row.dataset.title;
                document.getElementById('description').value = row.dataset.description;
                document.getElementById('assignedTo').value = row.dataset.assigned;
                document.getElementById('dueDate').value = row.dataset.due;
                document.getElementById('status').value = row.dataset.status;
                document.getElementById('statusRow').style.display = '';
                document.getElementById('modalTitle').textContent = 'Edit task';
            } else {
                document.getElementById('statusRow').style.display = 'none';
                document.getElementById('modalTitle').textContent = 'Create task';
            }
            m.classList.add('open');
        }
        function closeTaskModal() { document.getElementById('taskModal').classList.remove('open'); }
        function deleteTask(id, title) {
            if (!confirm('Delete task "' + title + '"?')) return;
            const f = document.getElementById('deleteForm');
            f.querySelector('input[name=id]').value = id;
            f.submit();
        }
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
                    <h2 class="mb-0">Task management</h2>
                    <div class="text-muted">Assign work to employees and track progress.</div>
                </div>
                <button class="btn" onclick="openTaskModal(false)">+ New task</button>
            </div>

            <div class="table-wrap">
                <table class="data">
                    <thead><tr><th>Title</th><th>Assigned</th><th>Status</th><th>Due</th><th>Actions</th></tr></thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty tasks}">
                            <tr class="empty-row"><td colspan="5">No tasks yet — create one to get started.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="t" items="${tasks}">
                                <c:set var="assignedName" value="—"/>
                                <c:forEach var="e" items="${employees}">
                                    <c:if test="${e.id == t.assignedTo}"><c:set var="assignedName" value="${e.name}"/></c:if>
                                </c:forEach>
                                <tr>
                                    <td><strong><c:out value="${t.title}"/></strong>
                                        <div class="text-muted" style="font-size:12px"><c:out value="${t.description}"/></div>
                                    </td>
                                    <td><c:out value="${assignedName}"/></td>
                                    <td><span class="badge badge-${fn:toLowerCase(t.status)}">${t.status}</span></td>
                                    <td>${t.dueDate}</td>
                                    <td class="actions-cell">
                                        <button class="btn btn-secondary btn-sm"
                                                data-id="${t.id}"
                                                data-title="${fn:escapeXml(t.title)}"
                                                data-description="${fn:escapeXml(t.description)}"
                                                data-assigned="${t.assignedTo}"
                                                data-due="${t.dueDate}"
                                                data-status="${t.status}"
                                                onclick="openTaskModal(true, this)">Edit</button>
                                        <button class="btn btn-danger btn-sm" onclick="deleteTask(${t.id}, '${fn:escapeXml(t.title)}')">Delete</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <%-- Task modal --%>
        <div id="taskModal" class="modal-backdrop" onclick="if(event.target===this) closeTaskModal()">
            <div class="modal">
                <header>
                    <h3 id="modalTitle">Create task</h3>
                    <button type="button" class="close" onclick="closeTaskModal()">&times;</button>
                </header>
                <form id="taskForm" method="post" action="${pageContext.request.contextPath}/admin-tasks">
                    <div class="body">
                        <input type="hidden" id="taskAction" name="action" value="create"/>
                        <input type="hidden" id="taskId" name="id" value=""/>
                        <div class="form-grid">
                            <div class="form-group"><label>Title</label><input id="title" name="title" type="text" required></div>
                            <div class="form-group"><label>Assign to</label>
                                <select id="assignedTo" name="assignedTo" required>
                                    <option value="">Select employee…</option>
                                    <c:forEach var="e" items="${employees}">
                                        <option value="${e.id}">${e.name} (${e.email})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group"><label>Due date</label><input id="dueDate" name="dueDate" type="date"></div>
                            <div class="form-group" id="statusRow" style="display:none">
                                <label>Status</label>
                                <select id="status" name="status">
                                    <option value="PENDING">Pending</option>
                                    <option value="IN_PROGRESS">In Progress</option>
                                    <option value="COMPLETED">Completed</option>
                                </select>
                            </div>
                            <div class="form-group" style="grid-column: 1 / -1"><label>Description</label><textarea id="description" name="description" rows="3"></textarea></div>
                        </div>
                    </div>
                    <div class="footer">
                        <button type="button" class="btn btn-secondary" onclick="closeTaskModal()">Cancel</button>
                        <button type="submit" class="btn">Save</button>
                    </div>
                </form>
            </div>
        </div>

        <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin-tasks" style="display:none">
            <input type="hidden" name="action" value="delete"/>
            <input type="hidden" name="id" value=""/>
        </form>
    </main>
</div>
</body>
</html>
