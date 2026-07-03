package servlet;

import model.DisciplinaryCase;
import model.CounselingSession;
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

        List<DisciplinaryCase> records = DisciplinaryCase.search(search, offense, status, null);
        List<User> counselors = User.getAllCounselors();

        if (editId != null && !editId.isEmpty()) {
            DisciplinaryCase editRecord = DisciplinaryCase.getById(editId);
            if (editRecord != null) req.setAttribute("editRecord", editRecord);
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
            String caseId = req.getParameter("caseId");
            if (caseId == null || caseId.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/hep/records");
                return;
            }

            if ("delete".equals(action)) {
                DisciplinaryCase.delete(caseId);
                resp.sendRedirect(req.getContextPath() + "/hep/records");
            } else if ("update".equals(action)) {
                DisciplinaryCase dc = DisciplinaryCase.getById(caseId);
                if (dc != null) {
                    String newStudentId = req.getParameter("studentId");
                    String newStudentName = req.getParameter("studentName");
                    if (newStudentId != null && !newStudentId.trim().isEmpty()) {
                        dc.setStudentId(newStudentId.trim());
                        if (newStudentName != null && !newStudentName.trim().isEmpty()) {
                            Student.findOrCreate(newStudentId.trim(), newStudentName.trim());
                        }
                    }
                    dc.setOffenseType(req.getParameter("offenseType"));
                    String dateStr = req.getParameter("incidentDate");
                    if (dateStr != null && !dateStr.isEmpty()) {
                        dc.setIncidentDate(Date.valueOf(dateStr));
                    }
                    dc.setDescription(req.getParameter("description"));
                    dc.update();

                    CounselingSession cs = CounselingSession.getByCaseId(caseId);
                    if (cs != null) {
                        String counselorStaffId = req.getParameter("assignedTo");
                        if (counselorStaffId != null && !counselorStaffId.trim().isEmpty()) {
                            cs.setStaffID(counselorStaffId);
                        }
                        String status = req.getParameter("status");
                        if (status != null) cs.setStatus(status);
                        String apt = req.getParameter("appointmentDate");
                        if (apt != null && !apt.isEmpty()) {
                            cs.setAppointmentDate(Date.valueOf(apt));
                            if ("Not Set".equals(cs.getStatus()) || cs.getStatus() == null) {
                                cs.setStatus("Pending");
                            }
                        }
                    }
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
