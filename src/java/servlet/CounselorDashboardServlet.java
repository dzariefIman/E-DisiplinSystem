package servlet;

import model.Incident;
import model.User;
import java.io.IOException;
import java.util.List;
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

        int counselorId = user.getUserId();
        int totalCases = Incident.getTotalByCounselor(counselorId);
        int completedCases = Incident.getCompletedByCounselor(counselorId);
        int pendingCases = Incident.getPendingByCounselor(counselorId);
        int notSetCases = Incident.getNotSetByCounselor(counselorId);
        int pendingReferrals = pendingCases + notSetCases;
        int completionRate = totalCases > 0 ? Math.round((float) completedCases / totalCases * 100) : 0;

        List<Incident> latestCases = Incident.getLatestByCounselor(counselorId, 5);

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
