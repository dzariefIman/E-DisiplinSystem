<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <title>HEP Dashboard</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <base href="${pageContext.request.contextPath}/">
        <link rel="stylesheet" href="eDisiplinStyle.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
    </head>
    <body>
        <div class="navbar">
            <span class="navbar-brand">HEP ${sessionScope.user.fullName} welcome to E-Disiplin</span>
        </div>

        <div class="container-main">
            <div class="sidebar">
                <ul>
                    <li><a href="hep/dashboard" class="active">Home</a></li>
                    <li><a href="hep/add-discipline">Log Discipline</a></li>
                    <li><a href="hep/records">Records</a></li>
                    <li><a href="logout">Logout</a></li>
                </ul>
            </div>

            <div class="content">
                <div class="dashboard-header">
                    <h2>Dashboard</h2>
                </div>

                <div class="stat-cards-container">
                    <div class="stat-card">
                        <h4>View total disciplinary cases</h4>
                        <p>${totalCases}</p>
                    </div>
                    <div class="stat-card">
                        <h4>View most common offenses</h4>
                        <p>${commonOffense}</p>
                    </div>
                    <div class="stat-card">
                        <h4>View pending counseling sessions</h4>
                        <p>${pendingCount} cases</p>
                    </div>
                    <div class="stat-card">
                        <h4>View charts and reports</h4>
                        <p>System tracking active records</p>
                    </div>
                </div>

                <div class="charts-container">
                    <div class="chart-card">
                        <h3>Cases by Offense Type</h3>
                        <canvas id="offenseChart"></canvas>
                    </div>
                    <div class="chart-card">
                        <h3>Counseling Session Success Rate</h3>
                        <canvas id="successRateChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <script>
        const labels = '${offenseLabels}'.split(',').filter(Boolean);
        const data = '${offenseData}'.split(',').filter(Boolean).map(Number);
        const colors = ['#3498db','#2ecc71','#f39c12','#e74c3c','#9b59b6','#1abc9c','#34495e','#e67e22','#7f8c8d'];

        if (labels.length > 0) {
            new Chart(document.getElementById('offenseChart').getContext('2d'), {
                type: 'doughnut',
                data: { labels: labels, datasets: [{ data: data, backgroundColor: colors }] },
                options: { responsive: true, plugins: { legend: { position: 'right' } } }
            });
        }

        const mLabels = '${monthLabels}'.split(',').filter(Boolean);
        const mData = '${monthRates}'.split(',').filter(Boolean).map(Number);

        new Chart(document.getElementById('successRateChart').getContext('2d'), {
            type: 'line',
            data: {
                labels: mLabels,
                datasets: [{
                    label: 'Success Rate (%)',
                    data: mData,
                    borderColor: '#2ecc71',
                    backgroundColor: 'rgba(46,204,113,0.1)',
                    tension: 0.3, fill: true, pointRadius: 5, pointBackgroundColor: '#2ecc71'
                }]
            },
            options: {
                responsive: true,
                scales: { y: { beginAtZero: true, max: 100, ticks: { callback: v => v + '%' } } },
                plugins: { legend: { display: false } }
            }
        });
        </script>
    </body>
</html>
