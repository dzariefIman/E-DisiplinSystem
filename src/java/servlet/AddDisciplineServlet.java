package servlet;

import model.Student;
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

public class AddDisciplineServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if ("1".equals(req.getParameter("success"))) {
            req.setAttribute("success", "Record saved successfully.");
        } else if ("1".equals(req.getParameter("error"))) {
            req.setAttribute("error", "Failed to save record. Check all fields.");
        }
        List<User> counselors = User.getAllCounselors();
        req.setAttribute("counselors", counselors);
        req.getRequestDispatcher("/hepJsp/addDiscipline.jsp").forward(req, resp);
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
            User loggedInUser = (User) session.getAttribute("user");

            String studentId = req.getParameter("studentId");
            String studentName = req.getParameter("studentName");
            String offenseType = req.getParameter("offenseType");
            String incidentDateStr = req.getParameter("incidentDate");
            String description = req.getParameter("description");
            String counselorStaffId = req.getParameter("assignedTo");

            if (studentId == null || studentId.trim().isEmpty() ||
                studentName == null || studentName.trim().isEmpty() ||
                offenseType == null || offenseType.trim().isEmpty() ||
                incidentDateStr == null || incidentDateStr.trim().isEmpty() ||
                counselorStaffId == null || counselorStaffId.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/hep/add-discipline?error=1");
                return;
            }

            Student.findOrCreate(studentId.trim(), studentName.trim());

            String caseId = DisciplinaryCase.getNextCaseId();
            DisciplinaryCase dc = new DisciplinaryCase();
            dc.setCaseId(caseId);
            dc.setStudentId(studentId.trim());
            dc.setOffenseType(offenseType);
            dc.setDescription(description);
            dc.setIncidentDate(Date.valueOf(incidentDateStr));
            dc.setStaffID(loggedInUser.getStaffID());

            if (dc.add()) {
                String sessionId = CounselingSession.getNextSessionId();
                CounselingSession cs = new CounselingSession();
                cs.setSessionId(sessionId);
                cs.setCaseId(caseId);
                cs.setStaffID(counselorStaffId);
                cs.setStatus("Not Set");
                cs.add();
                resp.sendRedirect(req.getContextPath() + "/hep/add-discipline?success=1");
            } else {
                resp.sendRedirect(req.getContextPath() + "/hep/add-discipline?error=1");
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/hep/add-discipline?error=1");
        }
    }
}
