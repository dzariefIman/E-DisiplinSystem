package servlet;

import model.Incident;
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

        int counselorId = user.getUserId();
        List<Incident> myCases = Incident.getByCounselor(counselorId);
        req.setAttribute("myCases", myCases);
        req.setAttribute("myCasesJson", buildJson(myCases));
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
            String incidentIdStr = req.getParameter("incidentId");
            if (incidentIdStr == null || incidentIdStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/counselor/schedule");
                return;
            }
            int incidentId = Integer.parseInt(incidentIdStr);

            if ("setAppointment".equals(action)) {
                String dateStr = req.getParameter("appointmentDate");
                if (dateStr != null && !dateStr.isEmpty()) {
                    Date aptDate = Date.valueOf(dateStr);
                    Incident.setAppointment(incidentId, aptDate);
                }
            } else if ("markComplete".equals(action)) {
                Incident.updateStatus(incidentId, "Completed");
            }
        } catch (Exception e) {
            // ignore bad input, just redirect
        }

        resp.sendRedirect(req.getContextPath() + "/counselor/schedule");
    }

    private String buildJson(List<Incident> records) {
        return records.stream().map(inc -> {
            StringBuilder sb = new StringBuilder("{");
            sb.append("incidentId:").append(inc.getIncidentId());
            sb.append(",studentId:'").append(js(inc.getStudentId())).append("'");
            sb.append(",studentName:'").append(js(inc.getStudentName())).append("'");
            sb.append(",offenseType:'").append(js(inc.getOffenseType())).append("'");
            sb.append(",incidentDate:'").append(inc.getIncidentDate()).append("'");
            sb.append(",incidentDateDisplay:'").append(inc.getIncidentDateDisplay()).append("'");
            sb.append(",description:'").append(js(inc.getDescription())).append("'");
            sb.append(",status:'").append(js(inc.getStatus())).append("'");
            sb.append(",appointmentDate:'").append(inc.getAppointmentDate() != null ? inc.getAppointmentDate() : "").append("'");
            sb.append(",appointmentDateDisplay:'").append(inc.getAppointmentDateDisplay()).append("'");
            sb.append("}");
            return sb.toString();
        }).collect(Collectors.joining(",\n", "[", "]"));
    }

    private String js(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("'", "\\'");
    }
}
