package servlet;

import model.DisciplinaryCase;
import model.CounselingSession;
import model.User;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CounselorScheduleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (!user.getUserType().equals("Counselor")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String staffID = user.getStaffID();
        List<CounselingSession> mySessions = CounselingSession.getByCounselor(staffID);
        req.setAttribute("mySessions", mySessions);
        req.setAttribute("mySessionsJson", buildJson(mySessions));
        req.getRequestDispatcher("/counselorJsp/counselorSchedule.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            String action = req.getParameter("action");
            String sessionId = req.getParameter("sessionId");
            if (sessionId == null || sessionId.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/counselor/schedule");
                return;
            }

            if ("setAppointment".equals(action)) {
                String dateStr = req.getParameter("appointmentDate");
                if (dateStr != null && !dateStr.isEmpty()) {
                    Date aptDate = Date.valueOf(dateStr);
                    CounselingSession.setAppointment(sessionId, aptDate);
                }
            } else if ("updateDate".equals(action)) {
                String dateStr = req.getParameter("appointmentDate");
                if (dateStr != null && !dateStr.isEmpty()) {
                    Date aptDate = Date.valueOf(dateStr);
                    CounselingSession.updateDate(sessionId, aptDate);
                }
            } else if ("markComplete".equals(action)) {
                CounselingSession.markComplete(sessionId);
            } else if ("reopen".equals(action)) {
                CounselingSession.reopen(sessionId);
            }
        } catch (Exception e) {
            // ignore bad input, just redirect
        }

        resp.sendRedirect(req.getContextPath() + "/counselor/schedule");
    }

    private String buildJson(List<CounselingSession> sessions) {
        return sessions.stream().map(cs -> {
            DisciplinaryCase dc = cs.getDisciplinaryCase();
            StringBuilder sb = new StringBuilder("{");
            sb.append("sessionId:'").append(js(cs.getSessionId())).append("'");
            sb.append(",caseId:'").append(js(cs.getCaseId())).append("'");
            sb.append(",studentId:'").append(js(dc != null ? dc.getStudentId() : "")).append("'");
            sb.append(",studentName:'").append(js(dc != null ? dc.getStudentName() : "")).append("'");
            sb.append(",offenseType:'").append(js(dc != null ? dc.getOffenseType() : "")).append("'");
            sb.append(",incidentDate:'").append(dc != null ? dc.getIncidentDate() : "").append("'");
            sb.append(",incidentDateDisplay:'").append(dc != null ? dc.getIncidentDateDisplay() : "").append("'");
            sb.append(",description:'").append(js(dc != null ? dc.getDescription() : "")).append("'");
            sb.append(",status:'").append(js(cs.getStatus())).append("'");
            sb.append(",appointmentDate:'").append(cs.getAppointmentDate() != null ? cs.getAppointmentDate() : "").append("'");
            sb.append(",appointmentDateDisplay:'").append(cs.getAppointmentDateDisplay()).append("'");
            sb.append("}");
            return sb.toString();
        }).collect(Collectors.joining(",\n", "[", "]"));
    }

    private String js(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("'", "\\'");
    }
}
