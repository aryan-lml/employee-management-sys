<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard - EMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EMS.css">
    <style>
        .container{max-width:1100px;margin:20px auto;padding:0 16px}
        .card{background:#fff;border-radius:10px;padding:20px;box-shadow:0 6px 18px rgba(25,30,50,0.06);margin-bottom:16px}
        .grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:12px}
        .stat{padding:14px;border-radius:8px;background:#f8fafc}
    </style>
</head>
<body>
    <%-- Include navbar --%>
    <jsp:include page="/pages/navbar.jsp" />

    <div class="container">
        <div class="card">
            <h2>Dashboard</h2>
            <p>Welcome back, <strong>${sessionScope.userObj.username}</strong></p>
        </div>

        <div class="card">
            <h3>Quick Stats</h3>
            <div class="grid">
                <div class="stat">
                    <div style="font-size:12px;color:#6b7280">Total employees</div>
                    <div style="font-weight:600;font-size:20px">${requestScope.totalEmployees != null ? requestScope.totalEmployees : 'N/A'}</div>
                </div>
                <div class="stat">
                    <div style="font-size:12px;color:#6b7280">Your tasks</div>
                    <div style="font-weight:600;font-size:20px">${requestScope.yourTasks != null ? requestScope.yourTasks : 'N/A'}</div>
                </div>
                <div class="stat">
                    <div style="font-size:12px;color:#6b7280">Today's attendance</div>
                    <div style="font-weight:600;font-size:20px">${requestScope.todaysAttendance != null ? requestScope.todaysAttendance : 'N/A'}</div>
                </div>
            </div>
        </div>

        <div class="card">
            <h3>Quick Actions</h3>
            <div style="display:flex;gap:8px;flex-wrap:wrap">
                <a href="${pageContext.request.contextPath}/employees"><button>Employees</button></a>
                <a href="${pageContext.request.contextPath}/tasks"><button>Tasks</button></a>
                <a href="${pageContext.request.contextPath}/attendance"><button>Attendance</button></a>
                <a href="${pageContext.request.contextPath}/admin-tasks" style="${empty sessionScope.userObj || sessionScope.userObj.role ne 'ADMIN' ? 'display:none;' : ''}"><button>Admin Tasks</button></a>
                <a href="${pageContext.request.contextPath}/analytics" style="${empty sessionScope.userObj || sessionScope.userObj.role ne 'ADMIN' ? 'display:none;' : ''}"><button>Analytics</button></a>
            </div>
        </div>

        <div class="card">
            <h3>Recent Activity</h3>
            ${not empty requestScope.recentActivities ? 'Recent activity available. Check activity logs.' : 'No recent activity available.'}
            <!-- If recentActivities is provided as List<String>, consider rendering in controller or add JSTL to iterate -->
        </div>

    </div>
</body>
</html>
