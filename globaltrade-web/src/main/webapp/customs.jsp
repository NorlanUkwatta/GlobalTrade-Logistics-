<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Customs Clearance - GlobalTrade Logistics</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
        <!-- Google Fonts for Premium Typography -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --gt-dark: #0f172a;
            --gt-primary: #d97706;
            --gt-card-bg: rgba(255, 255, 255, 0.98);
        }
        body { background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%); color: #f8fafc; font-family: 'Inter', sans-serif; min-height: 100vh; }
        .sidebar { width: 260px; min-height: 100vh; background: rgba(15, 23, 42, 0.9); backdrop-filter: blur(12px); border-right: 1px solid rgba(255,255,255,0.05); position: fixed; left: 0; top: 0; z-index: 1000; transition: all 0.3s; }
        .sidebar .brand { padding: 1.5rem 1rem; border-bottom: 1px solid rgba(255,255,255,0.05); }
        .sidebar .brand-title { color: #f8fafc; font-size: 1rem; font-weight: 700; letter-spacing: 0.5px; }
        .sidebar .brand-sub { color: #94a3b8; font-size: 0.72rem; }
        .sidebar .nav-link { color: #94a3b8; padding: 0.65rem 1.25rem; border-radius: 8px; margin: 0.15rem 0.5rem; font-size: 0.9rem; transition: all 0.2s; }
        .sidebar .nav-link:hover, .sidebar .nav-link.active { background: rgba(217, 119, 6, 0.15); color: var(--gt-primary); border-right: 3px solid var(--gt-primary); }
        .sidebar .nav-link i { width: 22px; }
        .sidebar .section-label { color: rgba(255,255,255,0.4); font-size: 0.65rem; text-transform: uppercase; letter-spacing: 1px; padding: 0.5rem 1.25rem; margin-top: 0.5rem; }
        .main-content { margin-left: 260px; padding: 1.5rem 2rem; }
        .topbar { background: var(--gt-card-bg); border-radius: 12px; padding: 0.8rem 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 10px 20px rgba(0,0,0,0.2); display: flex; justify-content: space-between; align-items: center; color: #334155; }
        .kpi-card { background: var(--gt-card-bg); border: none; border-radius: 14px; box-shadow: 0 10px 20px rgba(0,0,0,0.2); transition: transform 0.2s; cursor: default; color: #334155; }
        .kpi-card:hover { transform: translateY(-3px); }
        .kpi-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.4rem; }
        .kpi-value { font-size: 2rem; font-weight: 700; line-height: 1; }
        .kpi-label { font-size: 0.82rem; color: #64748b; margin-top: 0.2rem; }
        .kpi-trend { font-size: 0.78rem; margin-top: 0.5rem; }
        .role-pill { font-size: 0.7rem; padding: 0.2em 0.6em; border-radius: 20px; }
        .card { background: var(--gt-card-bg); border-radius: 16px; border: none; box-shadow: 0 20px 40px rgba(0,0,0,0.3); color: #334155; }
        .card-header { background: transparent; border-bottom: 1px solid #e2e8f0; }
        .table-light th { background: #f8fafc; color: #64748b; font-weight: 600; text-transform: uppercase; font-size: 0.75rem; letter-spacing: 0.5px; border-bottom: 2px solid #e2e8f0; }
        .table td { border-bottom: 1px solid #f1f5f9; vertical-align: middle; color: #334155; }
        h4, h5, h6 { color: #334155 !important; }
        .topbar h5 { color: #0f172a !important; }
        .text-dark { color: #0f172a !important; }
        .text-muted { color: #64748b !important; }
        [data-role-required] { display: none; }
    </style>
</head>
<body>

<nav class="sidebar">
    <div class="brand d-flex align-items-center gap-2">
        <i class="bi bi-truck text-success fs-4"></i>
        <div class="brand-title">GlobalTrade Logistics</div>
    </div>
    <ul class="nav flex-column mt-2">
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/"><i class="bi bi-grid-1x2-fill me-2"></i>Dashboard</a>
        </li>
        <li class="section-label" data-role-required="ADMIN,LOGISTICS_COORD">Operations</li>
        <li class="nav-item" data-role-required="ADMIN,LOGISTICS_COORD">
            <a class="nav-link" href="${pageContext.request.contextPath}/shipments.jsp"><i class="bi bi-box-seam me-2"></i>Shipments</a>
        </li>
        <li class="section-label" data-role-required="ADMIN,CUSTOMS_AGENT">Customs</li>
        <li class="nav-item" data-role-required="ADMIN,CUSTOMS_AGENT">
            <a class="nav-link active" href="${pageContext.request.contextPath}/customs.jsp"><i class="bi bi-file-earmark-check me-2"></i>Declarations</a>
        </li>
        <li class="section-label" data-role-required="ADMIN">Administration</li>
        <li class="nav-item" data-role-required="ADMIN">
            <a class="nav-link" href="${pageContext.request.contextPath}/vendors.jsp"><i class="bi bi-shop me-2"></i>Vendors</a>
        </li>
        <li class="nav-item" data-role-required="ADMIN">
            <a class="nav-link" href="${pageContext.request.contextPath}/users/manage.jsp"><i class="bi bi-people-fill me-2"></i>User Management</a>
        </li>
        <li class="section-label">Account</li>
        <li class="nav-item">
            <a class="nav-link text-danger-emphasis" href="#" onclick="doLogout()"><i class="bi bi-box-arrow-left me-2"></i>Sign Out</a>
        </li>
    </ul>
</nav>

<main class="main-content">
    <div class="topbar">
        <div>
            <h5 class="mb-0 fw-bold">Customs Clearance Operations</h5>
            <small class="text-muted">Review and process international border declarations</small>
        </div>
        <div id="userProfileInfo" class="text-end"></div>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">Dec. ID</th>
                        <th>Shipment</th>
                        <th>Status</th>
                        <th>Duties (Rs.)</th>
                        <th>Remarks</th>
                        <th class="text-end pe-4">Actions</th>
                    </tr>
                </thead>
                <tbody id="customsTableBody">
                    <tr><td colspan="6" class="text-center py-4">No declarations found.</td></tr>
                </tbody>
            </table>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
const CTX = '';
let currentUser = null;

async function apiCall(url, method='GET', body=null) {
    const opts = { method, headers: { 'Content-Type': 'application/json' }, credentials: 'same-origin' };
    if(body) opts.body = JSON.stringify(body);
    const res = await fetch(url, opts);
    if(res.status === 401) { window.location.href = CTX + '/login.jsp'; return null; }
    return res;
}

async function doLogout() {
    await apiCall(CTX+'/api/auth/logout', 'POST');
    window.location.href = CTX+'/login.jsp';
}

function applyRoleVisibility(roles) {
    document.querySelectorAll('[data-role-required]').forEach(el => {
        const reqs = el.getAttribute('data-role-required').split(',');
        if (reqs.some(r => roles.includes(r))) el.style.display = '';
        else el.style.display = 'none';
    });
}

async function loadCustoms() {
    const res = await apiCall(CTX + '/api/customs');
    if (!res || !res.ok) return;
    const data = await res.json();
    const tbody = document.getElementById('customsTableBody');
    tbody.innerHTML = '';
    
    if (data.data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No declarations found.</td></tr>';
        return;
    }

    data.data.forEach(c => {
        let badge = c.status === 'APPROVED' ? 'success' : (c.status === 'REJECTED' ? 'danger' : 'warning');
        
        let actions = '<button class="btn btn-sm btn-outline-success me-1" onclick="updateCustoms(' + c.id + ', \'APPROVED\')">Approve</button>' +
                      '<button class="btn btn-sm btn-outline-danger" onclick="updateCustoms(' + c.id + ', \'REJECTED\')">Reject</button>';
                      
        if (c.status !== 'SUBMITTED' && c.status !== 'UNDER_REVIEW') {
            actions = '<span class="text-muted small">Processed</span>';
        }

        tbody.innerHTML += '<tr>' +
            '<td class="ps-4 fw-bold">DEC-' + c.id + '</td>' +
            '<td>' + (c.shipment ? c.shipment.trackingNumber : '-') + '</td>' +
            '<td><span class="badge bg-' + badge + '">' + c.status + '</span></td>' +
            '<td> + (c.dutyAmount || '0.00') + '</td>' +
            '<td>' + (c.remarks || '-') + '</td>' +
            '<td class="text-end pe-4">' + actions + '</td>' +
            '</tr>';
    });
}

async function updateCustoms(id, status) {
    if(!confirm("Mark declaration as " + status + "?")) return;
    const res = await apiCall(CTX + '/api/customs/' + id + '/status?status=' + status + '&remarks=Manual intervention', 'PUT');
    if (res && res.ok) {
        loadCustoms();
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    const res = await apiCall(CTX + '/api/users/me');
    if (res && res.ok) {
        const data = await res.json();
        currentUser = data.data;
        applyRoleVisibility([currentUser.role]);
        loadCustoms();
    }
});
</script>
</body>
</html>

