<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Counselor Schedule</title>
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
                    <li><a href="counselor/schedule" class="active">Schedule</a></li>
                    <li><a href="counselor/records">Records</a></li>
                    <li><a href="logout">Logout</a></li>
                </ul>
            </div>

            <div class="content">
                <div class="dashboard-header">
                    <h2>Counseling Schedule & Status</h2>
                </div>

                <div class="schedule-container">
                    <div class="calendar-section">
                        <div class="calendar">
                            <div class="calendar-header">
                                <button onclick="previousMonth()" class="nav-btn">&lt;</button>
                                <div class="cal-header-center">
                                    <label class="cal-label">Month:</label>
                                    <select id="monthSelect" onchange="onMonthChange()" class="cal-select">
                                        <option value="0">January</option>
                                        <option value="1">February</option>
                                        <option value="2">March</option>
                                        <option value="3">April</option>
                                        <option value="4">May</option>
                                        <option value="5">June</option>
                                        <option value="6">July</option>
                                        <option value="7">August</option>
                                        <option value="8">September</option>
                                        <option value="9">October</option>
                                        <option value="10">November</option>
                                        <option value="11">December</option>
                                    </select>
                                    <label class="cal-label-offset">Year:</label>
                                    <select id="yearSelect" onchange="onYearChange()" class="cal-select">
                                        <c:set var="curYear" value="<%= java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) %>"/>
                                        <c:forEach var="y" begin="${curYear - 1}" end="${curYear + 3}">
                                            <option value="${y}" ${y == curYear ? 'selected' : ''}>${y}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <button onclick="nextMonth()" class="nav-btn">&gt;</button>
                            </div>
                            <div class="calendar-grid" id="calendarGrid"></div>
                        </div>
                    </div>

                    <div class="status-section">
                        <div class="status-cards">
                            <div class="status-card">
                                <h4>[COMPLETED]</h4>
                                <ul class="status-list" id="completedList"></ul>
                            </div>
                            <div class="status-card">
                                <h4>PENDING (Waiting to Done)</h4>
                                <ul class="status-list" id="pendingList"></ul>
                            </div>
                            <div class="status-card">
                                <h4>NOT SET DATE YET</h4>
                                <ul class="status-list" id="notSetList"></ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal-overlay" id="caseDetailsModal">
            <div class="modal-card schedule-modal-card">
                <div class="modal-header">
                    <h3 id="caseModalTitle">Cases for Date</h3>
                    <button type="button" class="close-modal" onclick="closeCaseModal()">&times;</button>
                </div>
                <div id="caseDetailsContainer"></div>
            </div>
        </div>

        <div class="notification" id="scheduleNotification"></div>

        <script>
        const myCases = ${mySessionsJson};
        const now = new Date(); let currentYear = now.getFullYear(); let currentMonth = now.getMonth();
        let datesCases = {};

        document.addEventListener("DOMContentLoaded", function () {
            document.getElementById("monthSelect").value = currentMonth;
            document.getElementById("yearSelect").value = currentYear;
            generateCalendar(myCases);
            populateStatusSections(myCases);
        });

        function generateCalendar(cases) {
            const grid = document.getElementById("calendarGrid");
            grid.innerHTML = '';
            ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'].forEach(d => {
                const h = document.createElement('div'); h.className = 'day-header'; h.textContent = d; grid.appendChild(h);
            });
            const firstDay = new Date(currentYear, currentMonth, 1).getDay();
            const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
            const daysInPrev = new Date(currentYear, currentMonth, 0).getDate();

            for (let i = firstDay - 1; i >= 0; i--) {
                const d = document.createElement('div'); d.className = 'calendar-day other-month'; d.textContent = daysInPrev - i; grid.appendChild(d);
            }
            for (let date = 1; date <= daysInMonth; date++) {
                const day = document.createElement('div'); day.className = 'calendar-day';
                const dateStr = currentYear + '-' + String(currentMonth + 1).padStart(2,'0') + '-' + String(date).padStart(2,'0');
                const casesOnDate = cases.filter(c => c.appointmentDate === dateStr);
                if (casesOnDate.length > 0) { day.className += ' has-appointment'; datesCases[dateStr] = casesOnDate; }
                const num = document.createElement('div'); num.className = 'calendar-day-number'; num.textContent = date; day.appendChild(num);
                if (casesOnDate.length > 0) {
                    const tip = document.createElement('div'); tip.className = 'calendar-day-tooltip';
                    tip.textContent = casesOnDate.length > 1 ? casesOnDate.length + ' cases' : casesOnDate[0].studentName;
                    day.appendChild(tip); day.style.cursor = 'pointer';
                    day.addEventListener('click', () => showCaseModal(dateStr));
                }
                grid.appendChild(day);
            }
            const remaining = 42 - grid.children.length;
            for (let d = 1; d <= remaining; d++) {
                const day = document.createElement('div'); day.className = 'calendar-day other-month'; day.textContent = d; grid.appendChild(day);
            }
        }

        function previousMonth() {
            if (currentMonth === 0) { currentMonth = 11; currentYear--; } else { currentMonth--; }
            document.getElementById("monthSelect").value = currentMonth; document.getElementById("yearSelect").value = currentYear;
            generateCalendar(myCases);
        }
        function nextMonth() {
            if (currentMonth === 11) { currentMonth = 0; currentYear++; } else { currentMonth++; }
            document.getElementById("monthSelect").value = currentMonth; document.getElementById("yearSelect").value = currentYear;
            generateCalendar(myCases);
        }
        function onMonthChange() { currentMonth = parseInt(document.getElementById("monthSelect").value); generateCalendar(myCases); }
        function onYearChange() { currentYear = parseInt(document.getElementById("yearSelect").value); generateCalendar(myCases); }

        function showCaseModal(dateStr) {
            const cases = datesCases[dateStr] || [];
            document.getElementById("caseModalTitle").textContent = 'Cases for ' + new Date(dateStr + 'T00:00:00').toLocaleDateString('en-US', { weekday:'long', year:'numeric', month:'long', day:'numeric' });
            const container = document.getElementById("caseDetailsContainer"); container.innerHTML = '';
            const today = new Date(); today.setHours(0,0,0,0);
            const aptDate = new Date(dateStr + 'T00:00:00');
            const canComplete = aptDate <= today;

            cases.forEach(c => {
                const div = document.createElement('div'); div.style.cssText = 'margin-bottom:20px;padding:15px;background:#f5f5f5;border-radius:4px;';
                const stCls = c.status === 'Completed' ? 'status-completed' : (c.status === 'Not Set' ? 'status-notset' : 'status-pending');
                div.innerHTML = '<p><strong>Student:</strong> ' + c.studentName + '</p><p><strong>ID:</strong> ' + c.studentId + '</p><p><strong>Offense:</strong> ' + c.offenseType + '</p><p><strong>Description:</strong> ' + (c.description || '') + '</p><p><strong>Status:</strong> <span class="status-badge ' + stCls + '">' + c.status + '</span></p>';
                if (c.status === 'Pending' || c.status === 'Not Set') {
                    if (canComplete) {
                        div.innerHTML += '<form action="counselor/schedule" method="POST" style="margin-top:10px;"><input type="hidden" name="action" value="markComplete"><input type="hidden" name="sessionId" value="' + c.sessionId + '"><button type="submit" class="btn-complete">✓ Mark as Completed</button></form>';
                    } else {
                        div.innerHTML += '<p class="complete-disabled">Appointment date hasn\'t arrived yet.</p>';
                    }
                }
                container.appendChild(div);
            });
            document.getElementById("caseDetailsModal").classList.add('show');
        }

        function closeCaseModal() { document.getElementById("caseDetailsModal").classList.remove('show'); }
        window.onclick = function(e) { const m = document.getElementById("caseDetailsModal"); if (e.target === m) m.classList.remove('show'); };

        function populateStatusSections(cases) {
            const completedList = document.getElementById("completedList");
            const pendingList = document.getElementById("pendingList");
            const notSetList = document.getElementById("notSetList");
            completedList.innerHTML = ''; pendingList.innerHTML = ''; notSetList.innerHTML = '';

            const completed = cases.filter(c => c.status === 'Completed');
            const pending = cases.filter(c => c.status === 'Pending');
            const notSet = cases.filter(c => c.status === 'Not Set');

            if (completed.length === 0) { completedList.innerHTML = '<li class="empty-message">No completed cases yet.</li>'; }
            else {
                const sorted = [...completed].sort((a,b) => (b.appointmentDate||'').localeCompare(a.appointmentDate||''));
                sorted.slice(0,3).forEach(c => {
                    const li = document.createElement('li'); li.className = 'student-item';
                    li.innerHTML = '<strong>' + c.studentName + '</strong><br><span class="student-item-id">' + c.studentId + ' — ' + c.appointmentDateDisplay + '</span>';
                    completedList.appendChild(li);
                });
                if (completed.length > 3) {
                    const li = document.createElement('li'); li.style.cssText = 'text-align:center;padding:10px 0 0;border-bottom:none';
                    li.innerHTML = '<a href="counselor/records?status=Completed" class="view-link small">View all completed records →</a>';
                    completedList.appendChild(li);
                }
            }

            if (pending.length === 0) { pendingList.innerHTML = '<li class="empty-message">No pending cases.</li>'; }
            else {
                pending.forEach(c => {
                    const li = document.createElement('li'); li.className = 'student-item';
                    li.innerHTML = '<strong>' + c.studentName + '</strong><br><span class="student-item-id">' + c.studentId + ' — ' + c.appointmentDateDisplay + '</span>';
                    pendingList.appendChild(li);
                });
            }

            if (notSet.length === 0) { notSetList.innerHTML = '<li class="empty-message">All cases have dates set.</li>'; }
            else {
                notSet.forEach(c => {
                    const li = document.createElement('li'); li.className = 'student-item';
                    li.style.cssText = 'border-bottom:1px solid #f0f0f0;padding:10px 0';
                    li.innerHTML = '<strong>' + c.studentName + '</strong><br><span class="student-item-id">' + c.studentId + ' — ' + c.offenseType + '</span>';
                    const row = document.createElement('div'); row.className = 'date-row';
                    row.innerHTML = '<input type="date" id="dateInput-' + c.sessionId + '" class="date-picker" min="' + new Date().toISOString().split('T')[0] + '"><button onclick="setAppointmentDate(\'' + c.sessionId + '\')" class="set-date-btn">Set Date</button>';
                    li.appendChild(row);
                    notSetList.appendChild(li);
                });
            }
        }

        function setAppointmentDate(sessionId) {
            const input = document.getElementById('dateInput-' + sessionId);
            const date = input.value;
            if (!date) { alert("Please select a date first."); return; }
            const form = document.createElement('form'); form.method = 'POST'; form.action = 'counselor/schedule';
            form.innerHTML = '<input type="hidden" name="action" value="setAppointment"><input type="hidden" name="sessionId" value="' + sessionId + '"><input type="hidden" name="appointmentDate" value="' + date + '">';
            document.body.appendChild(form); form.submit();
        }

        function showToast(msg) {
            const n = document.getElementById('scheduleNotification'); n.textContent = msg; n.classList.add('show');
            setTimeout(() => n.classList.remove('show'), 2500);
        }
        </script>
    </body>
</html>
