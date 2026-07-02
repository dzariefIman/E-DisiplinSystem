<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Counselor Records</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <base href="${pageContext.request.contextPath}/">
        <link rel="stylesheet" href="eDisiplinStyle.css">
    </head>
    <body>
        <div class="navbar">
            <span class="navbar-brand">Counselor ${sessionScope.user.fullName} welcome to E-Disiplin</span>
        </div>

        <div class="container-main">
            <div class="sidebar">
                <ul>
                    <li><a href="counselor/dashboard">Home</a></li>
                    <li><a href="counselor/schedule">Schedule</a></li>
                    <li><a href="counselor/records" class="active">Records</a></li>
                    <li><a href="logout">Logout</a></li>
                </ul>
            </div>

            <div class="content">
                <div class="dashboard-header">
                    <h2>My Counseling Records</h2>
                    <p>All cases assigned to you.</p>
                </div>

                <div class="filter-bar">
                    <form action="counselor/records" method="GET" style="display:flex;gap:12px;flex-wrap:wrap;width:100%;">
                        <input type="text" name="search" placeholder="Search by name or ID..." value="${param.search}" class="filter-input" style="flex:1;min-width:200px;">
                        <select name="offense" class="filter-select">
                            <option value="">All Offenses</option>
                            <c:set var="offenseList" value="Smoking,Cheating,Bullying,Fighting,Vandalism,Late Attendance,Disrespect to Staff,Skipping Classes,Dress Code Violation,Plagiarism,Hostel Misconduct"/>
                            <c:forTokens var="o" items="${offenseList}" delims=",">
                                <option value="${o}" ${param.offense == o ? 'selected' : ''}>${o}</option>
                            </c:forTokens>
                        </select>
                        <select name="status" class="filter-select">
                            <option value="">All Statuses</option>
                            <option value="Pending" ${param.status == 'Pending' ? 'selected' : ''}>Pending</option>
                            <option value="Completed" ${param.status == 'Completed' ? 'selected' : ''}>Completed</option>
                            <option value="Not Set" ${param.status == 'Not Set' ? 'selected' : ''}>Not Set</option>
                        </select>
                        <button type="submit" class="btn btn-primary" style="padding:10px 20px;">Filter</button>
                    </form>
                </div>

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
                            <c:forEach var="inc" items="${records}">
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
                            <c:if test="${empty records}">
                                <tr><td colspan="7" style="text-align:center;padding:20px;color:#999;">No matching records found.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>
