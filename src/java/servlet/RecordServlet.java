package servlet;

import model.Incident;
import model.User;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RecordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String search = req.getParameter("search");
        String offense = req.getParameter("offense");
        String status = req.getParameter("status");
        String editId = req.getParameter("editId");

        List<Incident> records = Incident.search(search, offense, status, null);
        List<User> counselors = User.getAllCounselors();

        if (editId != null && !editId.isEmpty()) {
            try {
                Incident editRecord = Incident.getById(Integer.parseInt(editId));
                req.setAttribute("editRecord", editRecord);
            } catch (Exception e) { /* bad id, ignore */ }
        }

        req.setAttribute("records", records);
        req.setAttribute("counselors", counselors);
        req.getRequestDispatcher("/hepJsp/recordEDisiplin.jsp").forward(req, resp);
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
                resp.sendRedirect(req.getContextPath() + "/hep/records");
                return;
            }
            int incidentId = Integer.parseInt(incidentIdStr);

            if ("delete".equals(action)) {
                Incident.delete(incidentId);
                resp.sendRedirect(req.getContextPath() + "/hep/records");
            } else if ("update".equals(action)) {
                Incident inc = Incident.getById(incidentId);
                if (inc != null) {
                    inc.setStudentId(req.getParameter("studentId"));
                    inc.setStudentName(req.getParameter("studentName"));
                    inc.setOffenseType(req.getParameter("offenseType"));
                    String dateStr = req.getParameter("incidentDate");
                    if (dateStr != null && !dateStr.isEmpty()) {
                        inc.setIncidentDate(Date.valueOf(dateStr));
                    }
                    inc.setDescription(req.getParameter("description"));
                    String assignedStr = req.getParameter("assignedTo");
                    if (assignedStr != null && !assignedStr.isEmpty()) {
                        inc.setAssignedTo(Integer.parseInt(assignedStr));
                    }
                    inc.setStatus(req.getParameter("status"));
                    String apt = req.getParameter("appointmentDate");
                    if (apt != null && !apt.isEmpty()) {
                        inc.setAppointmentDate(Date.valueOf(apt));
                    }
                    inc.update();
                }
                resp.sendRedirect(req.getContextPath() + "/hep/records");
            } else {
                resp.sendRedirect(req.getContextPath() + "/hep/records");
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/hep/records");
        }
    }
}
