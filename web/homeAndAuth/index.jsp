<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>E-Disiplin - Student Conduct & Counseling Log</title>
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
        <div class="welcome-container">
            <div class="welcome-content">
                <h2>WELCOME TO<br>E-DISIPLIN</h2>
                <h3>Student Conduct & Counseling Log</h3>
                <p>A centralized ledger for tracking disciplinary records and scheduling mandatory counseling sessions.</p>
                <p class="auth-text">Please <a href="login" class="link">login</a> to access, or <a href="register" class="link">register</a> for new user.</p>
                <div class="button-group">
                    <a href="login" class="btn btn-primary">Login</a>
                    <a href="register" class="btn btn-secondary">Register</a>
                </div>
            </div>
            <div class="welcome-illustration">
                <img src="https://cdn-icons-png.flaticon.com/512/17081/17081229.png" alt="E-Disiplin Illustration" class="illustration-image">
            </div>
        </div>
    </body>
</html>
