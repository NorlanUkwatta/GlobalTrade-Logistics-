<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Shipments - GlobalTrade Logistics</title>
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
            <a class="nav-link active" href="${pageContext.request.contextPath}/shipments.jsp"><i class="bi bi-box-seam me-2"></i>Shipments</a>
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
            <h5 class="mb-0 fw-bold">Shipment Operations</h5>
            <small class="text-muted">Manage active shipments and statuses</small>
        </div>
        <div class="d-flex align-items-center gap-3">
            <div id="userProfileInfo" class="text-end"></div>
            <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#createShipmentModal" data-role-required="ADMIN,VENDOR_REP">
                <i class="bi bi-plus-lg me-1"></i> New Shipment
            </button>
        </div>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">Tracking #</th>
                        <th>Origin</th>
                        <th>Destination</th>
                        <th>Status</th>
                        <th>Vendor ID</th>
                        <th>Vendor Score</th>
                        <th class="text-end pe-4">Actions</th>
                    </tr>
                </thead>
                <tbody id="shipmentsTableBody">
                    <tr><td colspan="6" class="text-center py-4">Loading shipments...</td></tr>
                </tbody>
            </table>
        </div>
    </div>
</main>

<!-- Create Shipment Modal -->
<div class="modal fade" id="createShipmentModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="createShipmentForm">
                <div class="modal-header border-0 pb-0">
                    <h5 class="modal-title fw-bold">Create Shipment</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label text-muted small fw-bold">ORIGIN</label>
                        <input type="text" class="form-control" id="f_origin" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label text-muted small fw-bold">DESTINATION</label>
                        <input type="text" class="form-control" id="f_dest" required>
                    </div>
                    <div class="mb-3" data-role-required="ADMIN">
                        <label class="form-label text-muted small fw-bold">VENDOR ID</label>
                        <input type="number" class="form-control" id="f_vendor">
                        <small class="text-muted">Admin only: VENDOR_REPs will use their own automatically.</small>
                    </div>
                    <div class="mb-3" data-role-required="ADMIN,VENDOR_REP">
                        <label class="form-label text-muted small fw-bold">CUSTOMER ID (Optional)</label>
                        <input type="number" class="form-control" id="f_customer">
                    </div>
                </div>
                <div class="modal-footer border-0 pt-0">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Update Status Modal -->
<div class="modal fade" id="updateStatusModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="updateStatusForm">
                <input type="hidden" id="u_id">
                <div class="modal-header border-0 pb-0">
                    <h5 class="modal-title fw-bold">Update Status</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label text-muted small fw-bold">NEW STATUS</label>
                        <select class="form-select" id="u_status">
                            <option value="PENDING">PENDING</option>
                            <option value="IN_PROGRESS">IN_PROGRESS</option>
											<option value="IN_WAREHOUSE">IN_WAREHOUSE</option>
											<option value="SHIPPED">SHIPPED</option>
											<option value="RECEIVED_SHIPMENT">RECEIVED_SHIPMENT</option>
											<option value="ON_DELIVERY">ON_DELIVERY</option>
											<option value="DELIVERED">DELIVERED</option>
											<option value="RETURNED">RETURNED</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label text-muted small fw-bold">LOCATION</label>
                        <input type="text" class="form-control" id="u_location" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label text-muted small fw-bold">REMARKS</label>
                        <textarea class="form-control" id="u_remarks"></textarea>
                    </div>
                </div>
                <div class="modal-footer border-0 pt-0">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Status</button>
                </div>
            </form>
        </div>
    </div>
</div>

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

function getStatusBadge(s) {
    let c = 'secondary';
    if (s === 'PENDING') c = 'warning';
    if (s === 'SHIPPED') c = 'primary';
    if (s === 'DELIVERED') c = 'success';
    if (s === 'IN_PROGRESS') c = 'danger';
    return <span class="badge bg- + c + "> + s + </span>;
}

async function loadShipments() {
    const res = await apiCall(CTX + '/api/shipments');
    if (!res || !res.ok) return;
    const data = await res.json();
    const tbody = document.getElementById('shipmentsTableBody');
    tbody.innerHTML = '';
    
    if (data.data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No shipments found.</td></tr>';
        return;
    }

    data.data.forEach(s => {
        let actions = '<button class="btn btn-sm btn-outline-secondary" onclick="openUpdateModal(' + s.id + ', \'' + s.status + '\')">Update</button>';
        if (s.status === 'SHIPPED') {
            actions += ' <button class="btn btn-sm btn-outline-danger" onclick="submitCustoms(' + s.id + ')" data-role-required="ADMIN,VENDOR_REP">Customs</button>';
        }
        
        tbody.innerHTML += '<tr>' +
            '<td class="ps-4 fw-bold">' + s.trackingNumber + '</td>' +
            '<td>' + s.origin + '</td>' +
            '<td>' + s.destination + '</td>' +
            '<td>' + getStatusBadge(s.status) + '</td>' +
            '<td>' + (s.vendor ? s.vendor.id : '-') + '</td>' +
            '<td>' + (s.vendor && s.vendor.performanceScore ? s.vendor.performanceScore.toFixed(1) : '-') + '</td>' +
            '<td class="text-end pe-4">' + actions + '</td>' +
            '</tr>';
    });
    applyRoleVisibility([currentUser.role]);
}

async function submitCustoms(shipmentId) {
    if(!confirm("Submit this shipment to customs for clearance?")) return;
    const res = await apiCall(CTX + '/api/customs/' + shipmentId + '?dutyAmount=50000.00', 'POST');
    if (res && res.ok) {
        alert("Customs Declaration Submitted.");
        loadShipments();
    } else {
        alert("Failed to submit declaration.");
    }
}

function openUpdateModal(id, currentStatus) {
    document.getElementById('u_id').value = id;
    document.getElementById('u_status').value = currentStatus;
    new bootstrap.Modal(document.getElementById('updateStatusModal')).show();
}

document.getElementById('createShipmentForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const body = {
        origin: document.getElementById('f_origin').value,
        destination: document.getElementById('f_dest').value,
        vendorId: document.getElementById('f_vendor').value || null,
        customerId: document.getElementById('f_customer').value || null
    };
    const res = await apiCall(CTX + '/api/shipments', 'POST', body);
    if (res && res.ok) {
        bootstrap.Modal.getInstance(document.getElementById('createShipmentModal')).hide();
        loadShipments();
    } else {
        alert("Failed to create shipment.");
    }
});

document.getElementById('updateStatusForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('u_id').value;
    const st = document.getElementById('u_status').value;
    const loc = document.getElementById('u_location').value;
    const rm = document.getElementById('u_remarks').value;
    
    const res = await apiCall(CTX + '/api/shipments/' + id + '/status?status=' + st + '&location=' + loc + '&remarks=' + rm, 'PUT');
    if (res && res.ok) {
        bootstrap.Modal.getInstance(document.getElementById('updateStatusModal')).hide();
        loadShipments();
    } else {
        alert("Failed to update status. Are you authorized for this vendor?");
    }
});

document.addEventListener('DOMContentLoaded', async () => {
    const res = await apiCall(CTX + '/api/users/me');
    if (res && res.ok) {
        const data = await res.json();
        currentUser = data.data;
        applyRoleVisibility([currentUser.role]);
        loadShipments();
    }
});
</script>
</body>
</html>

