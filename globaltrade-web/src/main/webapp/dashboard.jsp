<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Dashboard Ã¢â‚¬â€ GlobalTrade Logistics</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet" crossorigin="anonymous"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <style>
        body { background-color: #f0f4f8; font-family: 'Segoe UI', sans-serif; }
        .sidebar { width: 260px; min-height: 100vh; background: linear-gradient(180deg,#0d1b2a,#1b4332); position: fixed; left: 0; top: 0; z-index: 1000; transition: all 0.3s; }
        .sidebar .brand { padding: 1.5rem 1rem; border-bottom: 1px solid rgba(255,255,255,0.1); }
        .sidebar .brand-title { color: #fff; font-size: 1rem; font-weight: 700; }
        .sidebar .brand-sub { color: rgba(255,255,255,0.6); font-size: 0.72rem; }
        .sidebar .nav-link { color: rgba(255,255,255,0.75); padding: 0.65rem 1.25rem; border-radius: 8px; margin: 0.15rem 0.5rem; font-size: 0.9rem; transition: all 0.2s; }
        .sidebar .nav-link:hover, .sidebar .nav-link.active { background: rgba(255,255,255,0.12); color: #fff; }
        .sidebar .nav-link i { width: 22px; }
        .sidebar .section-label { color: rgba(255,255,255,0.4); font-size: 0.65rem; text-transform: uppercase; letter-spacing: 1px; padding: 0.5rem 1.25rem; margin-top: 0.5rem; }
        .main-content { margin-left: 260px; padding: 1.5rem 2rem; }
        .topbar { background: #fff; border-radius: 12px; padding: 0.8rem 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,0.06); display: flex; justify-content: space-between; align-items: center; }
        .kpi-card { border: none; border-radius: 14px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); transition: transform 0.2s; cursor: default; }
        .kpi-card:hover { transform: translateY(-3px); }
        .kpi-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.4rem; }
        .kpi-value { font-size: 2rem; font-weight: 700; line-height: 1; }
        .kpi-label { font-size: 0.82rem; color: #6c757d; margin-top: 0.2rem; }
        .kpi-trend { font-size: 0.78rem; margin-top: 0.5rem; }
        .role-pill { font-size: 0.7rem; padding: 0.2em 0.6em; border-radius: 20px; }
        /* Role-specific nav visibility controlled by JS */
        [data-role-required] { display: none; }
    </style>
</head>
<body>

<nav class="sidebar">
    <div class="brand d-flex align-items-center gap-2">
        <i class="bi bi-truck text-success fs-4"></i>
        <div>
            <div class="brand-title">GlobalTrade</div>
            <div class="brand-sub">Supply Chain Portal</div>
        </div>
    </div>

    <ul class="nav flex-column mt-2">
        <li class="nav-item">
            <a class="nav-link active" href="${pageContext.request.contextPath}/">
                <i class="bi bi-grid-1x2-fill me-2"></i>Dashboard
            </a>
        </li>

        <!-- Logistics Coordinator + Admin -->
        <li class="section-label" data-role-required="ADMIN,LOGISTICS_COORD">Operations</li>
        <li class="nav-item" data-role-required="ADMIN,LOGISTICS_COORD">
            <a class="nav-link" href="${pageContext.request.contextPath}/shipments.jsp"><i class="bi bi-box-seam me-2"></i>Shipments</a>
        </li>

        <!-- Warehouse Manager -->
        <li class="section-label" data-role-required="ADMIN,WAREHOUSE_MGR,LOGISTICS_COORD">Warehouse</li>
        <li class="nav-item" data-role-required="ADMIN,WAREHOUSE_MGR,LOGISTICS_COORD">
            <a class="nav-link" href="${pageContext.request.contextPath}/warehouse.jsp"><i class="bi bi-building me-2"></i>Inventory</a>
        </li>

        <!-- Vendor Rep -->
        <li class="section-label" data-role-required="ADMIN">Vendors</li>
        <li class="nav-item" data-role-required="ADMIN">
            <a class="nav-link" href="${pageContext.request.contextPath}/vendors.jsp"><i class="bi bi-shop me-2"></i>Vendor Portal</a>
        </li>

        <!-- Customs Agent -->
        <li class="section-label" data-role-required="ADMIN,CUSTOMS_AGENT">Customs</li>
        <li class="nav-item" data-role-required="ADMIN,CUSTOMS_AGENT">
            <a class="nav-link" href="${pageContext.request.contextPath}/customs.jsp"><i class="bi bi-file-earmark-check me-2"></i>Declarations</a>
        </li>

        <!-- Customer -->
        <li class="section-label" data-role-required="CUSTOMER">My Shipments</li>
        <li class="nav-item" data-role-required="CUSTOMER">
            <a class="nav-link" href="${pageContext.request.contextPath}/customer-portal.jsp"><i class="bi bi-search me-2"></i>Track Order</a>
        </li>

        <!-- Admin only -->
        <li class="section-label" data-role-required="ADMIN">Administration</li>
        <li class="nav-item" data-role-required="ADMIN">
            <a class="nav-link" href="${pageContext.request.contextPath}/users/manage.jsp">
                <i class="bi bi-people-fill me-2"></i>User Management
            </a>
        </li>
        <li class="nav-item" data-role-required="ADMIN">
            <a class="nav-link" href="${pageContext.request.contextPath}/audit.jsp"><i class="bi bi-journal-text me-2"></i>Audit Logs</a>
        </li>

        <li class="section-label">Account</li>
        <li class="nav-item">
            <a class="nav-link" href="#"><i class="bi bi-person-circle me-2"></i>My Profile</a>
        </li>
        <li class="nav-item">
            <a class="nav-link text-danger-emphasis" href="#" onclick="doLogout()">
                <i class="bi bi-box-arrow-left me-2"></i>Sign Out
            </a>
        </li>
    </ul>
</nav>

<!-- Ã¢â€â‚¬Ã¢â€â‚¬ MAIN CONTENT Ã¢â€â‚¬Ã¢â€â‚¬ -->
<main class="main-content">

    <!-- Top Bar -->
    <div class="topbar">
        <div>
            <h5 class="mb-0 fw-bold" id="page-title">Operations Dashboard</h5>
            <small class="text-muted" id="page-sub">Welcome back, <span id="user-fullname">...</span></small>
        </div>
        <div class="d-flex align-items-center gap-3">
            <span class="badge bg-success role-pill" id="user-role-badge">...</span>
            <div class="dropdown">
                <button class="btn btn-light btn-sm dropdown-toggle" data-bs-toggle="dropdown">
                    <i class="bi bi-person-circle me-1"></i><span id="user-display">...</span>
                </button>
                <ul class="dropdown-menu dropdown-menu-end">
                    <li><a class="dropdown-item" href="#"><i class="bi bi-person me-2"></i>Profile</a></li>
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item text-danger" href="#" onclick="doLogout()">
                        <i class="bi bi-box-arrow-left me-2"></i>Sign Out</a></li>
                </ul>
            </div>
        </div>
    </div>

    <!-- KPI Cards -->
    <div class="row g-4 mb-4" id="kpi-row">
        <div class="col-sm-6 col-xl-3">
            <div class="card kpi-card p-3">
                <div class="d-flex align-items-start gap-3">
                    <div class="kpi-icon bg-primary bg-opacity-10 text-primary"><i class="bi bi-truck"></i></div>
                    <div>
                        <div class="kpi-value text-primary" id="kpi-shipments">0</div>
                        <div class="kpi-label">Active Shipments</div>
                        <div class="kpi-trend text-success"><i class="bi bi-arrow-up-short"></i> Loading...</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-xl-3">
            <div class="card kpi-card p-3">
                <div class="d-flex align-items-start gap-3">
                    <div class="kpi-icon bg-warning bg-opacity-10 text-warning"><i class="bi bi-exclamation-triangle"></i></div>
                    <div>
                        <div class="kpi-value text-warning" id="kpi-alerts">0</div>
                        <div class="kpi-label">Open Alerts</div>
                        <div class="kpi-trend text-muted">Shipment delays + shortages</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-xl-3">
            <div class="card kpi-card p-3">
                <div class="d-flex align-items-start gap-3">
                    <div class="kpi-icon bg-success bg-opacity-10 text-success"><i class="bi bi-building"></i></div>
                    <div>
                        <div class="kpi-value text-success" id="kpi-inventory">0</div>
                        <div class="kpi-label">Low Stock SKUs</div>
                        <div class="kpi-trend text-muted">Below reorder threshold</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-xl-3">
            <div class="card kpi-card p-3">
                <div class="d-flex align-items-start gap-3">
                    <div class="kpi-icon bg-danger bg-opacity-10 text-danger"><i class="bi bi-file-earmark-text"></i></div>
                    <div>
                        <div class="kpi-value text-danger" id="kpi-customs">0</div>
                        <div class="kpi-label">Customs Pending</div>
                        <div class="kpi-trend text-muted">Filing deadlines today</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- System Status -->
    <div class="card border-0 shadow-sm rounded-3 mb-4">
        <div class="card-header bg-white border-0 pt-3">
            <h6 class="fw-bold mb-0"><i class="bi bi-activity me-2 text-success"></i>System Status</h6>
        </div>
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <div class="d-flex align-items-center gap-2">
                        <span class="badge bg-success rounded-pill" style="width:10px;height:10px;padding:0;">&nbsp;</span>
                        <span class="small">EJB Timer Services</span>
                        <span class="badge bg-success ms-auto">Operational</span>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="d-flex align-items-center gap-2">
                        <span class="badge bg-success rounded-pill" style="width:10px;height:10px;padding:0;">&nbsp;</span>
                        <span class="small">Database (MySQL)</span>
                        <span class="badge bg-success ms-auto">Connected</span>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="d-flex align-items-center gap-2">
                        <span class="badge bg-success rounded-pill" style="width:10px;height:10px;padding:0;">&nbsp;</span>
                        <span class="small">Audit Service</span>
                        <span class="badge bg-success ms-auto">Recording</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Audit Activity (Admin only) -->
    <div class="card border-0 shadow-sm rounded-3" id="audit-section" style="display:none;">
        <div class="card-header bg-white border-0 pt-3">
            <h6 class="fw-bold mb-0"><i class="bi bi-journal-text me-2 text-primary"></i>Recent Audit Activity</h6>
        </div>
        <div class="card-body">
            <div id="audit-table-container">
                <p class="text-muted small">Loading audit trail...</p>
            </div>
        </div>
    </div>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
const CTX = '${pageContext.request.contextPath}';
let currentUser = null;

// Ã¢â€â‚¬Ã¢â€â‚¬ Utility Ã¢â€â‚¬Ã¢â€â‚¬

async function apiCall(url, method='GET', body=null) {
    const opts = { method, headers: { 'Content-Type': 'application/json' } };
    if(body) opts.body=JSON.stringify(body);
    const res = await fetch(url, opts);
    if(res.status===401) { window.location.href=CTX+'/login.jsp'; return null; }
    return res;
}

async function doLogout() {
    await apiCall(CTX+'/api/auth/logout','POST');
    window.location.href=CTX+'/login.jsp';
}

// Ã¢â€â‚¬Ã¢â€â‚¬ Load User Profile Ã¢â€â‚¬Ã¢â€â‚¬
async function loadUserProfile() {
    const res = await apiCall(CTX + '/api/auth/me');
    if (!res) return;
    const data = await res.json();
    if (data.success && data.data) {
        currentUser = data.data;
        document.getElementById('user-fullname').textContent = currentUser.fullName;
        document.getElementById('user-display').textContent = currentUser.username;
        document.getElementById('user-role-badge').textContent = formatRole(currentUser.role);
        applyRoleVisibility(currentUser.role);
        if (currentUser.role === 'ADMIN') {
            document.getElementById('audit-section').style.display = 'block';
        }
    }
}

function formatRole(role) {
    const map = { ADMIN:'Administrator', LOGISTICS_COORD:'Logistics Coordinator',
        WAREHOUSE_MGR:'Warehouse Manager', VENDOR_REP:'Vendor Representative',
        CUSTOMS_AGENT:'Customs Agent', CUSTOMER:'Customer' };
    return map[role] || role;
}

function applyRoleVisibility(role) {
    if (role === 'VENDOR_REP') { window.location.href = CTX + '/vendor-portal.jsp'; return; }
    if (role === 'LOGISTICS_COORD') { window.location.href = CTX + '/shipments.jsp'; return; }
    if (role === 'CUSTOMS_AGENT') { window.location.href = CTX + '/customs.jsp'; return; }
    if (role === 'WAREHOUSE_MGR') { window.location.href = CTX + '/warehouse.jsp'; return; }
    if (role === 'CUSTOMER') { window.location.href = CTX + '/customer-portal.jsp'; return; }
    if (role === 'OPS') { window.location.href = CTX + '/ops-portal.jsp'; return; }
    document.querySelectorAll('[data-role-required]').forEach(el => {
        const required = el.getAttribute('data-role-required').split(',');
        if (required.includes(role)) {
            el.style.display = '';
        }
    });
}

// Ã¢â€â‚¬Ã¢â€â‚¬ Simulate KPI Loading (Phase 1 placeholder Ã¢â‚¬â€ real data in Phase 2) Ã¢â€â‚¬Ã¢â€â‚¬
function loadKpiPlaceholders() {
    setTimeout(() => {
        document.getElementById('kpi-shipments').textContent = '--';
        document.getElementById('kpi-alerts').textContent = '--';
        document.getElementById('kpi-inventory').textContent = '--';
        document.getElementById('kpi-customs').textContent = '--';
    }, 300);
}

// Ã¢â€â‚¬Ã¢â€â‚¬ Init Ã¢â€â‚¬Ã¢â€â‚¬
document.addEventListener('DOMContentLoaded', () => {
    loadUserProfile();
    loadKpiPlaceholders();
});
</script>
</body>
</html>
