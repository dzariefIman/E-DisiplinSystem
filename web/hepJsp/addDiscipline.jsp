<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Add Discipline</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <base href="${pageContext.request.contextPath}/">
        <link rel="stylesheet" href="eDisiplinStyle.css">
    </head>
    <body>
        <div class="navbar">
            <span class="navbar-brand">HEP ${sessionScope.user.fullName} welcome to E-Disiplin</span>
        </div>

        <div class="container-main">
            <div class="sidebar">
                <ul>
                    <li><a href="hep/dashboard">Home</a></li>
                    <li><a href="hep/add-discipline" class="active">Log Discipline</a></li>
                    <li><a href="hep/records">Records</a></li>
                    <li><a href="logout">Logout</a></li>
                </ul>
            </div>

            <div class="content">
                <div class="dashboard-header">
                    <h2>Log Discipline</h2>
                </div>

                <% if (request.getAttribute("success") != null) { %>
                    <div class="notification show"><%= request.getAttribute("success") %></div>
                <% } %>
                <% if (request.getAttribute("error") != null) { %>
                    <p style="color:#e74c3c;margin-bottom:16px;"><%= request.getAttribute("error") %></p>
                <% } %>

                <div class="form-card">
                    <form action="hep/add-discipline" method="POST">
                        <div class="form-group">
                            <label>Student ID</label>
                            <input type="text" name="studentId" placeholder="Enter student ID" required>
                        </div>
                        <div class="form-group">
                            <label>Student Name</label>
                            <input type="text" name="studentName" placeholder="Enter student name" required>
                        </div>
                        <div class="form-group">
                            <label>Offense Type</label>
                            <select name="offenseType" required>
                                <option value="">-- Select offense type --</option>
                                <option>Smoking</option>
                                <option>Bullying</option>
                                <option>Fighting</option>
                                <option>Cheating</option>
                                <option>Vandalism</option>
                                <option>Late Attendance</option>
                                <option>Dress Code Violation</option>
                                <option>Plagiarism</option>
                                <option>Hostel Misconduct</option>
                                <option>Disrespect to Staff</option>
                                <option>Skipping Classes</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Description</label>
                            <textarea name="description" placeholder="Enter description" required></textarea>
                        </div>
                        <div class="form-group">
                            <label>Date of Incident</label>
                            <input type="date" name="incidentDate" required>
                        </div>
                        <div class="form-group">
                            <label>Assign Counselor</label>
                            <select name="assignedTo" required>
                                <option value="">-- Select counselor --</option>
                                <c:forEach var="c" items="${counselors}">
                                    <option value="${c.userId}">${c.fullName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <button type="submit" class="btn-submit">Add Log Incident</button>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
