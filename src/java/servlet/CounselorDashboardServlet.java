package servlet;

import model.DisciplinaryCase;
import model.CounselingSession;
import model.User;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CounselorDashboardServlet extends HttpServlet {

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
        int totalCases = CounselingSession.getTotalCount(staffID);
        int completedCases = CounselingSession.getCompletedCount(staffID);
        int pendingCases = CounselingSession.getPendingCount(staffID);
        int notSetCases = CounselingSession.getNotSetCount(staffID);
        int pendingReferrals = pendingCases + notSetCases;
        int completionRate = totalCases > 0 ? Math.round((float) completedCases / totalCases * 100) : 0;

        List<CounselingSession> latestSessions = CounselingSession.getLatestByCounselor(staffID, 5);
        List<DisciplinaryCase> latestCases = latestSessions.stream()
            .map(CounselingSession::getDisciplinaryCase)
            .collect(Collectors.toList());

        req.setAttribute("totalCases", totalCases);
        req.setAttribute("completedCases", completedCases);
        req.setAttribute("pendingCases", pendingCases);
        req.setAttribute("notSetCases", notSetCases);
        req.setAttribute("pendingReferrals", pendingReferrals);
        req.setAttribute("completionRate", completionRate);
        req.setAttribute("latestCases", latestCases);

        req.getRequestDispatcher("/counselorJsp/counselorDashboard.jsp").forward(req, resp);
    }
}
