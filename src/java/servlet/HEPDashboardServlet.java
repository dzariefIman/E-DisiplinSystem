package servlet;

import model.Incident;
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

        int totalCases = Incident.getTotalCases();
        String commonOffense = Incident.getMostCommonOffense();
        int pendingCount = Incident.getPendingCount();
        List<Incident> records = Incident.getAll();

        Map<String, Integer> offenseCount = new LinkedHashMap<>();
        for (Incident inc : records) {
            String type = inc.getOffenseType();
            offenseCount.put(type, offenseCount.getOrDefault(type, 0) + 1);
        }

        String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        int currentMonth = YearMonth.now().getMonthValue();
        List<String> monthLabels = new ArrayList<>();
        List<Integer> monthRates = new ArrayList<>();

        for (int m = 1; m <= currentMonth; m++) {
            monthLabels.add(monthNames[m - 1]);
            int total = 0, completed = 0;
            for (Incident inc : records) {
                if (inc.getIncidentDate() != null) {
                    int incMonth = inc.getIncidentDate().toLocalDate().getMonthValue();
                    if (incMonth == m) {
                        total++;
                        if ("Completed".equals(inc.getStatus())) completed++;
                    }
                }
            }
            monthRates.add(total > 0 ? Math.round((float) completed / total * 100) : 0);
        }

        req.setAttribute("totalCases", totalCases);
        req.setAttribute("commonOffense", commonOffense);
        req.setAttribute("pendingCount", pendingCount);
        req.setAttribute("records", records);
        req.setAttribute("offenseLabels", String.join(",", offenseCount.keySet()));
        req.setAttribute("offenseData", String.join(",", offenseCount.values().stream().map(String::valueOf).toArray(String[]::new)));
        req.setAttribute("monthLabels", String.join(",", monthLabels));
        req.setAttribute("monthRates", String.join(",", monthRates.stream().map(String::valueOf).toArray(String[]::new)));

        req.getRequestDispatcher("/hepJsp/hepDashboard.jsp").forward(req, resp);
    }
}
