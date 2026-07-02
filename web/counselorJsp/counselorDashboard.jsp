<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Counselor Dashboard</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <base href="${pageContext.request.contextPath}/">
        <link rel="stylesheet" href="eDisiplinStyle.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
    </head>
    <body>
        <div class="navbar">
            <span class="navbar-brand">Counselor ${sessionScope.user.fullName} welcome to E-Disiplin</span>
        </div>

        <div class="container-main">
            <div class="sidebar">
                <ul>
                    <li><a href="counselor/dashboard" class="active">Home</a></li>
                    <li><a href="counselor/schedule">Schedule</a></li>
                    <li><a href="counselor/records">Records</a></li>
                    <li><a href="logout">Logout</a></li>
                </ul>
            </div>

            <div class="content">
                <div class="dashboard-header">
                    <h2>Dashboard</h2>
                </div>

                <div class="stat-cards-container">
                    <div class="stat-card"><h4>Total Cases</h4><p>${totalCases}</p></div>
                    <div class="stat-card"><h4>Completed Cases</h4><p>${completedCases}</p></div>
                    <div class="stat-card"><h4>Pending Cases</h4><p>${pendingReferrals}</p></div>
                    <div class="stat-card"><h4>Completion Rate</h4><p>${completionRate}%</p></div>
                </div>

                <div class="charts-container">
                    <div class="chart-card">
                        <h3>Cases by Status</h3>
                        <canvas id="statusChart"></canvas>
                    </div>
                </div>

                <div class="dashboard-header"><h2>Assigned Student Cases</h2></div>

                <div class="table-container">
                    <table class="records-table">
                        <thead>
                            <tr>
                                <th>Student ID</th>
                                <th>Student Name</th>
                                <th>Offense Type</th>
                                <th>Date</th>
                                <th>Description</th>
                                <th>Counseling Date</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="inc" items="${latestCases}">
                                <tr>
                                    <td>${inc.studentId}</td>
                                    <td>${inc.studentName}</td>
                                    <td>${inc.offenseType}</td>
                                    <td>${inc.incidentDateDisplay}</td>
                                    <td>${inc.description}</td>
                                    <td>${inc.appointmentDateDisplay}</td>
                                    <c:set var="sClass" value="${inc.status == 'Not Set' ? 'notset' : fn:toLowerCase(inc.status)}"/>
                                    <td><span class="status-badge status-${sClass}">${inc.status}</span></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty latestCases}">
                                <tr><td colspan="7" style="text-align:center;padding:20px;color:#999;">No cases assigned.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <script>
        new Chart(document.getElementById('statusChart').getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: ['Completed', 'Pending', 'Not Set'],
                datasets: [{
                    data: [${completedCases}, ${pendingCases}, ${notSetCases}],
                    backgroundColor: ['#2ecc71', '#f39c12', '#e74c3c']
                }]
            },
            options: { responsive: true, plugins: { legend: { position: 'right' } } }
        });
        </script>
    </body>
</html>
