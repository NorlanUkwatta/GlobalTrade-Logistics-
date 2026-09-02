<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Customs Clearance - GlobalTrade Logistics</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .sidebar { width: 260px; background: #0d1b2a; min-height: 100vh; position: fixed; top: 0; left: 0; }
        .sidebar .brand { padding: 1.5rem 1rem; border-bottom: 1px solid rgba(255,255,255,0.1); }
        .sidebar .brand-title { color: #fff; font-size: 1rem; font-weight: 700; }
        .sidebar .nav-link { color: rgba(255,255,255,0.75); padding: 0.65rem 1.25rem; border-radius: 8px; margin: 0.15rem 0.5rem; font-size: 0.9rem; }
        .sidebar .nav-link:hover, .sidebar .nav-link.active { background: rgba(255,255,255,0.12); color: #fff; }
        .sidebar .nav-link i { width: 22px; }
        .sidebar .section-label { color: rgba(255,255,255,0.4); font-size: 0.65rem; text-transform: uppercase; padding: 0.5rem 1.25rem; }
        .main-content { margin-left: 260px; padding: 1.5rem 2rem; }
        .topbar { background: #fff; border-radius: 12px; padding: 0.8rem 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,0.06); display: flex; justify-content: space-between; align-items: center; }
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
    const opts = { method, headers: { 'Content-Type': 'application/json' } };
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
