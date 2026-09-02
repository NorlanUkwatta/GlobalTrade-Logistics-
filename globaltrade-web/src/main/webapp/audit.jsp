<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Audit Logs - GlobalTrade Logistics</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .sidebar { width: 260px; background: #0d1b2a; min-height: 100vh; position: fixed; top: 0; left: 0; z-index: 1000; }
        .sidebar .brand { padding: 1.5rem 1rem; border-bottom: 1px solid rgba(255,255,255,0.1); }
        .sidebar .brand-title { color: #fff; font-size: 1rem; font-weight: 700; }
        .sidebar .nav-link { color: rgba(255,255,255,0.75); padding: 0.65rem 1.25rem; border-radius: 8px; margin: 0.15rem 0.5rem; font-size: 0.9rem; }
        .sidebar .nav-link:hover, .sidebar .nav-link.active { background: rgba(255,255,255,0.12); color: #fff; }
        .sidebar .section-label { color: rgba(255,255,255,0.4); font-size: 0.65rem; text-transform: uppercase; padding: 0.5rem 1.25rem; }
        .main-content { margin-left: 260px; padding: 1.5rem 2rem; }
        .topbar { background: #fff; border-radius: 12px; padding: 0.8rem 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,0.06); display: flex; justify-content: space-between; align-items: center; }
        .code-pre { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 6px; padding: 8px; font-size: 0.8rem; overflow-x: auto; max-height: 150px; }
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
        <li class="section-label">Administration</li>
        <li class="nav-item">
            <a class="nav-link active" href="${pageContext.request.contextPath}/audit.jsp"><i class="bi bi-journal-text me-2"></i>Audit Logs</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="#" onclick="doLogout()"><i class="bi bi-box-arrow-left me-2 text-danger"></i>Sign Out</a>
        </li>
    </ul>
</nav>

<main class="main-content">
    <div class="topbar">
        <div>
            <h5 class="mb-0 fw-bold">System Audit Logs</h5>
            <small class="text-muted">Immutable tracking of critical system actions</small>
        </div>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">Timestamp</th>
                        <th>Action</th>
                        <th>User</th>
                        <th>Entity</th>
                        <th class="pe-4">Details</th>
                    </tr>
                </thead>
                <tbody id="auditTableBody">
                    <tr><td colspan="5" class="text-center py-4 text-muted">Loading audit logs...</td></tr>
                </tbody>
            </table>
        </div>
    </div>
</main>

<script>
const CTX = '';

async function apiCall(url, method='GET') {
    const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json' } });
    if(res.status === 401) { window.location.href = CTX + '/login.jsp'; return null; }
    return res;
}
async function doLogout() {
    await apiCall(CTX+'/api/auth/logout', 'POST');
    window.location.href = CTX+'/login.jsp';
}

document.addEventListener('DOMContentLoaded', async () => {
    const res = await apiCall(CTX + '/api/audit?limit=200');
    if (res && res.ok) {
        const d = await res.json();
        const tbody = document.getElementById('auditTableBody');
        tbody.innerHTML = '';
        if (d.data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4">No logs found.</td></tr>';
            return;
        }
        
        d.data.forEach(log => {
            let detailsHtml = log.details ? <pre class="code-pre m-0"> + log.details + </pre> : <span class="text-muted small">None</span>;
            let successBadge = log.success ? <span class="badge bg-success">Success</span> : <span class="badge bg-danger">Failure</span>;
            
            tbody.innerHTML += 
                <tr>
                    <td class="ps-4 small text-muted"> + new Date(log.timestamp).toLocaleString() + </td>
                    <td><div class="fw-bold"> + log.action + </div> + successBadge + </td>
                    <td> + (log.callerUsername || 'System') +  <span class="badge bg-secondary ms-1"> + (log.callerRole || '') + </span></td>
                    <td> + (log.entityType || '-') +   + (log.entityId || '') + </td>
                    <td class="pe-4" style="max-width: 400px;"> + detailsHtml + </td>
                </tr>
            ;
        });
    }
});
</script>
</body>
</html>