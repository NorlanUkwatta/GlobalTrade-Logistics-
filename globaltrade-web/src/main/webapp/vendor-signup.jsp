<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Vendor Sign Up - GlobalTrade Logistics</title>
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
<body class="d-flex align-items-center justify-content-center py-5">
<div class="container" style="max-width: 500px;">

    <div class="card login-card">
        <!-- Header -->
        <div class="login-header text-center">
            <i class="bi bi-truck-flatbed" style="font-size: 2.5rem;"></i>
            <div class="brand-title mt-2">GlobalTrade Logistics</div>
            <div class="brand-sub">Vendor Registration Portal</div>
        </div>

        <!-- Body -->
        <div class="card-body p-4">
            <h5 class="card-title text-center mb-4 fw-bold">Register as a Logistics Vendor</h5>

            <!-- Alert Area -->
            <div id="alert-area"></div>

            <form id="signupForm" novalidate>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="username" class="form-label fw-semibold">Username</label>
                        <input type="text" class="form-control" id="username" placeholder="jdoe_vendor" required/>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="fullName" class="form-label fw-semibold">Contact Person</label>
                        <input type="text" class="form-control" id="fullName" placeholder="John Doe" required/>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="email" class="form-label fw-semibold">Business Email</label>
                    <input type="email" class="form-control" id="email" placeholder="vendor@logistics.com" required/>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label fw-semibold">Password</label>
                    <input type="password" class="form-control" id="password" placeholder="Min 8 characters" required/>
                </div>

                <div class="mb-3">
                    <label for="companyName" class="form-label fw-semibold">Company Name</label>
                    <input type="text" class="form-control" id="companyName" placeholder="Maersk Sri Lanka" required/>
                </div>

                <div class="mb-4">
                    <label for="phone" class="form-label fw-semibold">Phone Number</label>
                    <input type="text" class="form-control" id="phone" placeholder="+94 11 2XXX XXX" required/>
                </div>

                <div class="d-grid">
                    <button type="submit" class="btn btn-success btn-login text-white" id="signupBtn">
                        <span class="spinner-border spinner-border-sm me-2" id="spinner" role="status"></span>
                        <i class="bi bi-check-circle me-1" id="signupIcon"></i>
                        Register Company
                    </button>
                </div>
            </form>

            <div class="text-center mt-4">
                <p class="mb-0">Already registered? <a href="login.jsp" class="text-success fw-bold text-decoration-none">Sign In</a></p>
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

document.getElementById('signupForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const request = {
        username: document.getElementById('username').value.trim(),
        fullName: document.getElementById('fullName').value.trim(),
        email: document.getElementById('email').value.trim(),
        password: document.getElementById('password').value,
        companyName: document.getElementById('companyName').value.trim(),
        phone: document.getElementById('phone').value.trim()
    };

    if (!request.username || !request.password || !request.email || !request.companyName) {
        showAlert('danger', '<i class="bi bi-exclamation-circle-fill me-2"></i>Please fill in all required fields.');
        return;
    }

    setLoading(true);
    clearAlert();

    try {
        const resp = await fetch(CTX + '/api/register/vendor', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(request)
        });

        const data = await resp.json();

        if (resp.ok && data.success) {
            showAlert('success', '<i class="bi bi-check-circle-fill me-2"></i>' + (data.message || 'Vendor registration successful! Redirecting...'));
            setTimeout(() => {
                window.location.href = 'login.jsp';
            }, 2000);
        } else {
            showAlert('danger', '<i class="bi bi-x-circle-fill me-2"></i>' + (data.message || 'Registration failed.'));
        }
    } catch (err) {
        showAlert('danger', '<i class="bi bi-exclamation-triangle-fill me-2"></i>Network error. Please try again.');
        console.error('Signup error:', err);
    } finally {
        setLoading(false);
    }
});

function setLoading(loading) {
    const btn    = document.getElementById('signupBtn');
    const spin   = document.getElementById('spinner');
    const icon   = document.getElementById('signupIcon');
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
