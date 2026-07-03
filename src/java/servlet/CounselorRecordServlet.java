package servlet;

import model.DisciplinaryCase;
import model.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CounselorRecordServlet extends HttpServlet {

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
        String search = req.getParameter("search");
        String offense = req.getParameter("offense");
        String status = req.getParameter("status");

        List<DisciplinaryCase> records = DisciplinaryCase.search(search, offense, status, staffID);
        req.setAttribute("records", records);
        req.getRequestDispatcher("/counselorJsp/counselorRecord.jsp").forward(req, resp);
    }
}
