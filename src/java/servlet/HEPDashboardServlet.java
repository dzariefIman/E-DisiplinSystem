package servlet;

import model.DisciplinaryCase;
import model.CounselingSession;
import model.User;
import java.io.IOException;
import java.util.*;
import java.time.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HEPDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (!user.getUserType().equals("HEP")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int totalCases = DisciplinaryCase.getTotalCases();
        String commonOffense = DisciplinaryCase.getMostCommonOffense();
        int pendingCount = CounselingSession.getGlobalPendingCount();
        int notSetCount = CounselingSession.getGlobalNotSetCount();
        int pendingNotSetCount = pendingCount + notSetCount;
        List<DisciplinaryCase> records = DisciplinaryCase.getAll();

        Map<String, Integer> offenseCount = new LinkedHashMap<>();
        for (DisciplinaryCase c : records) {
            String type = c.getOffenseType();
            offenseCount.put(type, offenseCount.getOrDefault(type, 0) + 1);
        }

        String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        int currentMonth = YearMonth.now().getMonthValue();
        List<String> monthLabels = new ArrayList<>();
        List<Integer> monthRates = new ArrayList<>();

        for (int m = 1; m <= currentMonth; m++) {
            monthLabels.add(monthNames[m - 1]);
            int total = 0, completed = 0;
            for (DisciplinaryCase c : records) {
                if (c.getIncidentDate() != null) {
                    int incMonth = c.getIncidentDate().toLocalDate().getMonthValue();
                    if (incMonth == m) {
                        total++;
                        if ("Completed".equals(c.getStatus())) completed++;
                    }
                }
            }
            monthRates.add(total > 0 ? Math.round((float) completed / total * 100) : 0);
        }

        req.setAttribute("totalCases", totalCases);
        req.setAttribute("commonOffense", commonOffense);
        req.setAttribute("pendingNotSetCount", pendingNotSetCount);
        req.setAttribute("records", records);
        req.setAttribute("offenseLabels", String.join(",", offenseCount.keySet()));
        req.setAttribute("offenseData", String.join(",", offenseCount.values().stream().map(String::valueOf).toArray(String[]::new)));
        req.setAttribute("monthLabels", String.join(",", monthLabels));
        req.setAttribute("monthRates", String.join(",", monthRates.stream().map(String::valueOf).toArray(String[]::new)));

        req.getRequestDispatcher("/hepJsp/hepDashboard.jsp").forward(req, resp);
    }
}
