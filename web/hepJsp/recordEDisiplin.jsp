<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Incident, model.User, java.util.List"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
  Incident editRecord = (Incident) request.getAttribute("editRecord");
  List<User> counselors = (List<User>) request.getAttribute("counselors");
  String[] offenses = {"Smoking","Bullying","Fighting","Cheating","Vandalism","Late Attendance","Dress Code Violation","Plagiarism","Hostel Misconduct","Disrespect to Staff","Skipping Classes"};
  String[] statuses = {"Pending","Completed","Not Set"};
%>
<!DOCTYPE html>
<html>
    <head>
        <title>E-Disiplin Records</title>
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
                    <li><a href="hep/add-discipline">Log Discipline</a></li>
                    <li><a href="hep/records" class="active">Records</a></li>
                    <li><a href="logout">Logout</a></li>
                </ul>
            </div>

            <div class="content">
                <div class="dashboard-header">
                    <h2>Disciplinary Records</h2>
                </div>

                <div class="filter-bar">
                    <form action="hep/records" method="GET" style="display:flex;gap:12px;flex-wrap:wrap;width:100%;">
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
                                <th>Assigned Counselor</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="inc" items="${records}">
                                <tr data-id="${inc.incidentId}">
                                    <td>${inc.studentId}</td>
                                    <td>${inc.studentName}</td>
                                    <td>${inc.offenseType}</td>
                                    <td>${inc.incidentDateDisplay}</td>
                                    <td>${inc.description}</td>
                                    <td>${inc.assignedCounselorName}</td>
                                    <c:set var="sClass" value="${inc.status == 'Not Set' ? 'notset' : fn:toLowerCase(inc.status)}"/>
                                    <td><span class="status-badge status-${sClass}">${inc.status}</span></td>
                                    <td>
                                        <div class="action-buttons">
                                            <form action="hep/records" method="GET" style="display:inline;">
                                                <input type="hidden" name="editId" value="${inc.incidentId}">
                                                <button type="submit" class="action-button edit-button">Edit</button>
                                            </form>
                                            <form action="hep/records" method="POST" style="display:inline;" onsubmit="return confirm('Delete this record?')">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="incidentId" value="${inc.incidentId}">
                                                <button type="submit" class="action-button delete-button">Delete</button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty records}">
                                <tr><td colspan="8" style="text-align:center;padding:20px;color:#999;">No records found.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

<%
  if (editRecord != null) {
%>
        <div class="modal-overlay show">
            <div class="modal-card">
                <div class="modal-header">
                    <h3>Edit Disciplinary Record</h3>
                    <a href="hep/records" class="close-modal">&times;</a>
                </div>
                <form action="hep/records" method="POST">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="incidentId" value="<%= editRecord.getIncidentId() %>">
                    <div class="modal-form-grid">
                        <div class="modal-field">
                            <label>Student ID</label>
                            <input type="text" name="studentId" value="<%= editRecord.getStudentId() != null ? editRecord.getStudentId() : "" %>">
                        </div>
                        <div class="modal-field">
                            <label>Student Name</label>
                            <input type="text" name="studentName" value="<%= editRecord.getStudentName() != null ? editRecord.getStudentName() : "" %>">
                        </div>
                        <div class="modal-field">
                            <label>Offense Type</label>
                            <select name="offenseType">
                                <% for (String o : offenses) { %>
                                    <option value="<%= o %>" <%= o.equals(editRecord.getOffenseType()) ? "selected" : "" %>><%= o %></option>
                                <% } %>
                            </select>
                        </div>
                        <div class="modal-field">
                            <label>Date of Incident</label>
                            <input type="date" name="incidentDate" value="<%= editRecord.getIncidentDate() != null ? editRecord.getIncidentDate().toString() : "" %>">
                        </div>
                        <div class="modal-field full">
                            <label>Description</label>
                            <textarea name="description" rows="4"><%= editRecord.getDescription() != null ? editRecord.getDescription() : "" %></textarea>
                        </div>
                        <div class="modal-field">
                            <label>Assigned Counselor</label>
                            <select name="assignedTo">
                                <% if (counselors != null) {
                                    for (User c : counselors) { %>
                                    <option value="<%= c.getUserId() %>" <%= c.getUserId() == editRecord.getAssignedTo() ? "selected" : "" %>><%= c.getFullName() %></option>
                                <%  }
                                } %>
                            </select>
                        </div>
                        <div class="modal-field">
                            <label>Status</label>
                            <select name="status">
                                <% for (String s : statuses) { %>
                                    <option value="<%= s %>" <%= s.equals(editRecord.getStatus()) ? "selected" : "" %>><%= s %></option>
                                <% } %>
                            </select>
                        </div>
                        <div class="modal-field">
                            <label>Appointment Date</label>
                            <input type="date" name="appointmentDate" value="<%= editRecord.getAppointmentDate() != null ? editRecord.getAppointmentDate().toString() : "" %>">
                        </div>
                    </div>
                    <div class="modal-actions">
                        <a href="hep/records" class="action-button modal-cancel">Cancel</a>
                        <button type="submit" class="action-button modal-save">Save Changes</button>
                    </div>
                </form>
            </div>
        </div>
<%
  }
%>
    </body>
</html>