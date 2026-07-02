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
            String assignedToStr = req.getParameter("assignedTo");

            if (studentId == null || studentId.trim().isEmpty() ||
                studentName == null || studentName.trim().isEmpty() ||
                offenseType == null || offenseType.trim().isEmpty() ||
                incidentDateStr == null || incidentDateStr.trim().isEmpty() ||
                assignedToStr == null || assignedToStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/hep/add-discipline?error=1");
                return;
            }

            Incident inc = new Incident();
            inc.setStudentId(studentId.trim());
            inc.setStudentName(studentName.trim());
            inc.setOffenseType(offenseType);
            inc.setIncidentDate(Date.valueOf(incidentDateStr));
            inc.setDescription(description);
            inc.setAssignedTo(Integer.parseInt(assignedToStr));
            inc.setLoggedBy(loggedInUser.getUserId());
            inc.setStatus("Not Set");

            if (inc.add()) {
                resp.sendRedirect(req.getContextPath() + "/hep/add-discipline?success=1");
            } else {
                resp.sendRedirect(req.getContextPath() + "/hep/add-discipline?error=1");
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/hep/add-discipline?error=1");
        }
    }
}
