<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
    index.jsp - Platform Entry Point
    Redirects authenticated users to the dashboard.
    Unauthenticated users are redirected to login by the JwtSessionFilter,
    but we add a client-side JS check as well for faster response.
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>GlobalTrade Logistics - Supply Chain Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet" crossorigin="anonymous"/>
    <script>
        // If auth_token cookie present, go to dashboard; otherwise go to login
        (function() {
            function getCookie(n) {
                const v = `; \${document.cookie}`;
                const p = v.split(`; ${n}=`);
                if (p.length === 2) return p.pop().split(';').shift();
                return null;
            }
            const token = getCookie('auth_token');
            if (token) {
                window.location.href = '${pageContext.request.contextPath}/';
            } else {
                window.location.href = '${pageContext.request.contextPath}/login.jsp';
            }
        })();
    </script>
</head>
<body class="bg-light d-flex justify-content-center align-items-center" style="min-height:100vh;">
    <div class="text-center">
        <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Redirecting-</span>
        </div>
        <p class="mt-3 text-muted">Redirecting-</p>
    </div>
</body>
</html>
