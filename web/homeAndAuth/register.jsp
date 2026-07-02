<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>E-Disiplin Register</title>
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
            <div class="auth-card wide">
                <h2>Register</h2>
                <p>Create a new E-Disiplin account.</p>

                <% if (request.getAttribute("error") != null) { %>
                    <p style="color:#e74c3c;margin-bottom:16px;"><%= request.getAttribute("error") %></p>
                <% } %>

                <form action="register" method="POST">
                    <div class="form-group">
                        <label for="staffId">Staff ID Number</label>
                        <input type="text" id="staffId" name="staffId" placeholder="Enter staff ID" required>
                    </div>
                    <div class="form-group">
                        <label for="fullName">Full Name</label>
                        <input type="text" id="fullName" name="fullName" placeholder="Enter full name" required>
                    </div>
                    <div class="form-group">
                        <label for="email">Staff Email</label>
                        <input type="email" id="email" name="email" placeholder="Enter staff email" required>
                    </div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" placeholder="Create a password" required>
                    </div>
                    <div class="form-group">
                        <label for="age">Age</label>
                        <input type="number" id="age" name="age" min="1" max="120" placeholder="Enter age" required>
                    </div>
                    <div class="form-group">
                        <label for="userType">User Type</label>
                        <select id="userType" name="userType" required>
                            <option value="">Select user type</option>
                            <option value="HEP">HEP</option>
                            <option value="Counselor">Counselor</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="gender">Gender</label>
                        <select id="gender" name="gender" required>
                            <option value="">Select gender</option>
                            <option value="Male">Male</option>
                            <option value="Female">Female</option>
                        </select>
                    </div>
                    <div class="btn-row">
                        <button type="submit" class="btn btn-primary">Register</button>
                        <button type="reset" class="btn btn-secondary">Clear</button>
                    </div>
                </form>

                <p class="auth-link">
                    Already have an account? <a href="login" class="link">Login here</a>
                </p>
            </div>
        </div>
    </body>
</html>
