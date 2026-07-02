package servlet;

import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/homeAndAuth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String staffId = req.getParameter("staffId");
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String userType = req.getParameter("userType");
            String ageStr = req.getParameter("age");
            String gender = req.getParameter("gender");

            if (staffId == null || staffId.trim().isEmpty() || fullName == null || fullName.trim().isEmpty() ||
                password == null || password.trim().isEmpty() || userType == null || userType.trim().isEmpty() ||
                ageStr == null || ageStr.trim().isEmpty() || gender == null || gender.trim().isEmpty()) {
                req.setAttribute("error", "All fields are required.");
                req.getRequestDispatcher("/homeAndAuth/register.jsp").forward(req, resp);
                return;
            }

            int age = Integer.parseInt(ageStr);
            if (age < 1 || age > 120) {
                req.setAttribute("error", "Age must be between 1 and 120.");
                req.getRequestDispatcher("/homeAndAuth/register.jsp").forward(req, resp);
                return;
            }

            if (User.existsByStaffId(staffId.trim())) {
                req.setAttribute("error", "Staff ID already registered.");
                req.getRequestDispatcher("/homeAndAuth/register.jsp").forward(req, resp);
                return;
            }

            User user = new User(staffId.trim(), fullName.trim(), email, password, userType, age, gender);
            if (User.register(user)) {
                resp.sendRedirect(req.getContextPath() + "/login?registered=true");
            } else {
                req.setAttribute("error", "Registration failed. Try again.");
                req.getRequestDispatcher("/homeAndAuth/register.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid age value.");
            req.getRequestDispatcher("/homeAndAuth/register.jsp").forward(req, resp);
        }
    }
}
