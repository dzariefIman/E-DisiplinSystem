<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>E-Disiplin Login</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <base href="${pageContext.request.contextPath}/">
        <link rel="stylesheet" href="eDisiplinStyle.css">
    </head>
    <body>
        <header class="header">
            <div class="header-container">
                <div class="header-logo">
                    <h1>E-DISIPLIN: Student Conduct & Counseling Log</h1>
                </div>
            </div>
        </header>

        <div class="auth-container">
            <div class="auth-card">
                <h2>Login</h2>
                <p>Select your user type and enter your credentials.</p>

                <% if (request.getAttribute("error") != null) { %>
                    <p style="color:#e74c3c;margin-bottom:16px;"><%= request.getAttribute("error") %></p>
                <% } %>
                <% if (request.getParameter("registered") != null) { %>
                    <p style="color:#2ecc71;margin-bottom:16px;">Registration successful! Please login.</p>
                <% } %>

                <form action="login" method="POST">
                    <div class="form-group">
                        <label>User Type</label>
                        <div class="radio-group">
                            <label><input type="radio" name="userType" value="HEP" required> HEP</label>
                            <label><input type="radio" name="userType" value="Counselor"> Counselor</label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="staffId">Staff ID</label>
                        <input type="text" id="staffId" name="staffId" placeholder="Enter your staff ID" required>
                    </div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" placeholder="Enter your password" required>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Login</button>
                </form>

                <p class="auth-link">
                    Don't have an account? <a href="register" class="link">Register here</a>
                </p>
            </div>
        </div>
    </body>
</html>
