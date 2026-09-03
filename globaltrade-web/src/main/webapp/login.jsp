<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Login - GlobalTrade Logistics</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet" crossorigin="anonymous"/>
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <!-- Google Fonts for Premium Typography -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --gt-dark: #0f172a;
            --gt-primary: #d97706; /* Elegant amber/gold */
            --gt-card-bg: rgba(255, 255, 255, 0.98);
        }
        body { background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%); color: #f8fafc; font-family: 'Inter', sans-serif; min-height: 100vh; }
        .login-card { background: var(--gt-card-bg); border: none; border-radius: 16px; box-shadow: 0 20px 40px rgba(0,0,0,0.3); color: #334155; }
        .login-header { background: transparent; border-radius: 16px 16px 0 0; padding: 2rem; border-bottom: 1px solid #e2e8f0; }
        .login-header i { color: var(--gt-primary) !important; }
        .brand-title { font-size: 1.3rem; font-weight: 700; letter-spacing: 0.5px; color: #0f172a; }
        .brand-sub { font-size: 0.8rem; color: #64748b; }
        .btn-login { background: var(--gt-primary); border: none; padding: 0.75rem; font-weight: 600; font-size: 1rem; border-radius: 8px; color: #fff; }
        .btn-login:hover { background: #b45309; color: #fff; }
        .form-control, .form-select { border-radius: 8px; padding: 0.75rem 1rem; border-color: #e2e8f0; font-size: 0.95rem; }
        .form-control:focus, .form-select:focus { border-color: var(--gt-primary); box-shadow: 0 0 0 4px rgba(217, 119, 6, 0.1); }
        .spinner-border-sm { display: none; }
        .role-badge { font-size: 0.75rem; padding: 0.4em 0.8em; border-radius: 6px; font-weight: 600; background: #f8fafc; color: #64748b; border: 1px solid #e2e8f0; }
        .text-success { color: var(--gt-primary) !important; }
        a.text-success:hover { color: #b45309 !important; }
        .card-footer { border-top: 1px solid #e2e8f0; background: transparent; color: #94a3b8 !important; }
        .form-label { font-size: 0.85rem; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; }
        .btn-outline-secondary { border-color: #e2e8f0; color: #64748b; }
        .btn-outline-secondary:hover { background: #f8fafc; color: #334155; }
    </style>
</head>
<body class="d-flex align-items-center justify-content-center">
<div class="container" style="max-width: 420px;">

    <div class="card login-card">
        <!-- Header -->
        <div class="login-header text-center">
            <i class="bi bi-truck" style="font-size: 2.5rem;"></i>
            <div class="brand-title mt-2">GlobalTrade Logistics</div>
            <div class="brand-sub">Supply Chain Management Platform</div>
            <div class="mt-2">
                <span class="badge role-badge">50+ Countries</span>
                <span class="badge role-badge ms-1">99.9% Uptime</span>
            </div>
        </div>

        <!-- Body -->
        <div class="card-body p-4">
            <h5 class="card-title text-center mb-4 fw-bold">Sign In to Your Account</h5>

            <!-- Alert Area -->
            <div id="alert-area"></div>

            <form id="loginForm" novalidate>
                <div class="mb-3">
                    <label for="username" class="form-label fw-semibold">
                        <i class="bi bi-person-fill me-1"></i>Username
                    </label>
                    <input type="text" class="form-control form-control-lg" id="username"
                           placeholder="Enter your username" autocomplete="username" required/>
                </div>

                <div class="mb-4">
                    <label for="password" class="form-label fw-semibold">
                        <i class="bi bi-lock-fill me-1"></i>Password
                    </label>
                    <div class="input-group">
                        <input type="password" class="form-control form-control-lg" id="password"
                               placeholder="Enter your password" autocomplete="current-password" required/>
                        <button class="btn btn-outline-secondary" type="button" id="togglePwd"
                                title="Show/hide password">
                            <i class="bi bi-eye" id="eyeIcon"></i>
                        </button>
                    </div>
                </div>

                <div class="d-grid">
                    <button type="submit" class="btn btn-success btn-login text-white" id="loginBtn">
                        <span class="spinner-border spinner-border-sm me-2" id="spinner" role="status"></span>
                        <i class="bi bi-box-arrow-in-right me-1" id="loginIcon"></i>
                        Sign In
                    </button>
                </div>
            </form>

            <div class="text-center mt-4">
                <p class="mb-2">Don't have an account? 
                    <a href="vendor-signup.jsp" class="text-success fw-bold text-decoration-none">Vendor Signup</a>
                </p>
                <small class="text-muted">
                    <i class="bi bi-shield-lock-fill me-1"></i>
                    Secured with JWT + BCrypt. All activity is audited.
                </small>
            </div>
        </div>

        <!-- Footer -->
        <div class="card-footer text-center text-muted py-2" style="border-radius: 0 0 16px 16px;">
            <small>&copy; 2024 GlobalTrade Logistics Corporation</small>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        crossorigin="anonymous"></script>
<script>
const CTX = '${pageContext.request.contextPath}';

// - Password visibility toggle -
document.getElementById('togglePwd').addEventListener('click', () => {
    const pwd  = document.getElementById('password');
    const icon = document.getElementById('eyeIcon');
    if (pwd.type === 'password') {
        pwd.type = 'text';
        icon.classList.replace('bi-eye', 'bi-eye-slash');
    } else {
        pwd.type = 'password';
        icon.classList.replace('bi-eye-slash', 'bi-eye');
    }
});

// - Login Form Submission -
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    if (!username || !password) {
        showAlert('danger', '<i class="bi bi-exclamation-circle-fill me-2"></i>Please enter your username and password.');
        return;
    }

    setLoading(true);
    clearAlert();

    try {
        const resp = await fetch(CTX + '/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
            credentials: 'include'  // Include cookies in the request
        });

        const data = await resp.json();

        if (resp.ok && data.success) {
            showAlert('success', '<i class="bi bi-check-circle-fill me-2"></i>' + (data.message || 'Login successful. Redirecting...'));
            
            const role = data.data ? data.data.role : 'ADMIN';
            let target = '/dashboard.jsp';
            
            if (role === 'VENDOR_REP') { target = '/vendor-portal.jsp'; } else if (role === 'LOGISTICS_COORD') { target = '/shipments.jsp'; } else if (role === 'CUSTOMS_AGENT') {
                target = '/customs.jsp';
            } else if (role === 'WAREHOUSE_MGR') {
                target = '/warehouse.jsp';
            } else if (role === 'CUSTOMER') {
                target = '/customer-portal.jsp';
            } else if (role === 'OPS') {
                target = '/ops-portal.jsp';
            } else if (role === 'ITOPS') {
                target = '/itops-portal.jsp';
            }
            
            setTimeout(() => {
                window.location.href = CTX + target;
            }, 800);
        } else if (resp.status === 403) {
            showAlert('warning',
                '<i class="bi bi-exclamation-triangle-fill me-2"></i>' + data.message);
        } else {
            showAlert('danger',
                '<i class="bi bi-x-circle-fill me-2"></i>' + (data.message || 'Login failed. Please check your credentials.'));
        }
    } catch (err) {
        showAlert('danger', '<i class="bi bi-exclamation-triangle-fill me-2"></i>Network error. Please check your connection and try again.');
        console.error('Login error:', err);
    } finally {
        setLoading(false);
    }
});

function setLoading(loading) {
    const btn    = document.getElementById('loginBtn');
    const spin   = document.getElementById('spinner');
    const icon   = document.getElementById('loginIcon');
    btn.disabled = loading;
    spin.style.display = loading ? 'inline-block' : 'none';
    icon.style.display = loading ? 'none' : 'inline-block';
}

function showAlert(type, message) {
    const area = document.getElementById('alert-area');
    area.innerHTML = `<div class="alert alert-\${type} alert-dismissible fade show py-2 shadow-sm" role="alert">
        \${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>`;
    area.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function clearAlert() {
    document.getElementById('alert-area').innerHTML = '';
}
</script>
</body>
</html>

