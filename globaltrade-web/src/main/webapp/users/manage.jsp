<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Management - GlobalTrade</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .sidebar { width: 260px; background: #0d1b2a; min-height: 100vh; position: fixed; top: 0; left: 0; }
        .sidebar .brand { padding: 1.5rem 1rem; border-bottom: 1px solid rgba(255,255,255,0.1); }
        .sidebar .brand-title { color: #fff; font-size: 1rem; font-weight: 700; }
        .sidebar .nav-link { color: rgba(255,255,255,0.75); padding: 0.65rem 1.25rem; border-radius: 8px; margin: 0.15rem 0.5rem; font-size: 0.9rem; }
        .sidebar .nav-link:hover, .sidebar .nav-link.active { background: rgba(255,255,255,0.12); color: #fff; }
        .main-content { margin-left: 260px; padding: 1.5rem 2rem; }
        .topbar { background: #fff; border-radius: 12px; padding: 0.8rem 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,0.06); display: flex; justify-content: space-between; align-items: center; }
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
        <li class="nav-item">
            <a class="nav-link active" href="${pageContext.request.contextPath}/users/manage.jsp"><i class="bi bi-people-fill me-2"></i>User Management</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="#" onclick="doLogout()"><i class="bi bi-box-arrow-left me-2 text-danger"></i>Sign Out</a>
        </li>
    </ul>
</nav>

<main class="main-content">
    <div class="topbar">
        <div>
            <h5 class="mb-0 fw-bold">User Management</h5>
            <small class="text-muted">Control access for all platform roles</small>
        </div>
        <button class="btn btn-primary btn-sm" onclick="promptCreate()">Create User</button>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">Username</th>
                        <th>Name</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th class="text-end pe-4">Actions</th>
                    </tr>
                </thead>
                <tbody id="userTableBody">
                    <tr><td colspan="5" class="text-center py-4 text-muted">Loading users...</td></tr>
                </tbody>
            </table>
        </div>
    </div>
</main>

<script>
const CTX = '';

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

async function loadUsers() {
    const res = await apiCall(CTX + '/api/users');
    if (res && res.ok) {
        const d = await res.json();
        const tbody = document.getElementById('userTableBody');
        tbody.innerHTML = '';
        d.data.forEach(u => {
            let status = u.suspended ? <span class="badge bg-danger">Suspended</span> : <span class="badge bg-success">Active</span>;
            let toggleBtn = u.suspended 
                ? <button class="btn btn-sm btn-outline-success" onclick="toggleStatus(+u.id+, 'activate')">Activate</button>
                : <button class="btn btn-sm btn-outline-warning" onclick="toggleStatus(+u.id+, 'suspend')">Suspend</button>;
            if (u.role === 'ADMIN') toggleBtn = ''; // don't allow suspending admins easily from UI for now

            tbody.innerHTML += 
                <tr>
                    <td class="ps-4 fw-bold"> + u.username + </td>
                    <td> + u.fullName + <br><small class="text-muted">+u.email+</small></td>
                    <td><span class="badge bg-secondary"> + u.role + </span></td>
                    <td> + status + </td>
                    <td class="text-end pe-4"> + toggleBtn + </td>
                </tr>
            ;
        });
    }
}

async function toggleStatus(id, action) {
    if(!confirm("Are you sure you want to " + action + " this user?")) return;
    const body = action === 'suspend' ? { reason: "Admin UI manual suspension" } : null;
    const res = await apiCall(CTX + '/api/users/' + id + '/' + action, 'PUT', body);
    if (res && res.ok) loadUsers();
    else alert("Action failed");
}

async function resetPassword(id) {
    if(!confirm("Are you sure you want to reset this user's password?")) return;
    const res = await apiCall(CTX + '/api/users/' + id + '/reset-password', 'PUT');
    if (res && res.ok) {
        const d = await res.json();
        prompt("Password reset successful. Copy the temporary password below:", d.data);
    } else {
        alert("Failed to reset password. Admins cannot reset other admins.");
    }
}

async function promptCreate() {
    let un = prompt("Username:"); if(!un) return;
    let name = prompt("Full Name:");
    let em = prompt("Email:");
    let pw = prompt("Password:");
    let r = prompt("Role (ADMIN, VENDOR_REP, LOGISTICS_COORD, WAREHOUSE_MGR, CUSTOMS_AGENT, CUSTOMER):");
    
    const body = { username: un, fullName: name, email: em, password: pw, role: r };
    const res = await apiCall(CTX + '/api/users', 'POST', body);
    if (res && res.ok) loadUsers();
    else alert("Creation failed");
}

document.addEventListener('DOMContentLoaded', loadUsers);
</script>
</body>
</html>