package servlet;

import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/homeAndAuth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String staffId = req.getParameter("staffId");
        String password = req.getParameter("password");
        String userType = req.getParameter("userType");

        User user = User.login(staffId, password);
        if (user != null && user.getUserType().equals(userType)) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            if (user.getUserType().equals("HEP")) {
                resp.sendRedirect(req.getContextPath() + "/hep/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/counselor/dashboard");
            }
        } else {
            req.setAttribute("error", "Invalid credentials or user type mismatch.");
            req.getRequestDispatcher("/homeAndAuth/login.jsp").forward(req, resp);
        }
    }
}
