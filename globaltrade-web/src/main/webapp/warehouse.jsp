<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Warehouse Management - GlobalTrade Logistics</title>
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
        .form-label { color: #334155 !important; }
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
        <li class="section-label" data-role-required="ADMIN,LOGISTICS_COORD,WAREHOUSE_MGR">Operations</li>
        <li class="nav-item" data-role-required="ADMIN,LOGISTICS_COORD">
            <a class="nav-link" href="${pageContext.request.contextPath}/shipments.jsp"><i class="bi bi-box-seam me-2"></i>Shipments</a>
        </li>
        <li class="nav-item" data-role-required="ADMIN,WAREHOUSE_MGR">
            <a class="nav-link active" href="${pageContext.request.contextPath}/warehouse.jsp"><i class="bi bi-building me-2"></i>Inventory & POs</a>
        </li>
        <li class="section-label" data-role-required="ADMIN,CUSTOMS_AGENT">Customs</li>
        <li class="nav-item" data-role-required="ADMIN,CUSTOMS_AGENT">
            <a class="nav-link" href="${pageContext.request.contextPath}/customs.jsp"><i class="bi bi-file-earmark-check me-2"></i>Declarations</a>
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
            <h5 class="mb-0 fw-bold">Warehouse Operations</h5>
            <small class="text-muted">Manage stock and approve purchase orders</small>
        </div>
        <div id="userProfileInfo" class="text-end"></div>
    </div>
    
    <div class="container-fluid mt-4">
        <!-- Tabs Nav -->
        <ul class="nav nav-tabs" id="warehouseTabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active fw-bold text-dark" id="vendor-deliveries-tab" data-bs-toggle="tab" data-bs-target="#vendor-deliveries" type="button" role="tab" aria-controls="vendor-deliveries" aria-selected="true">Accept Vendor Deliveries</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link fw-bold text-dark" id="inventory-count-tab" data-bs-toggle="tab" data-bs-target="#inventory-count" type="button" role="tab" aria-controls="inventory-count" aria-selected="false">Inventory Count</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link fw-bold text-dark" id="purchase-orders-tab" data-bs-toggle="tab" data-bs-target="#purchase-orders" type="button" role="tab" aria-controls="purchase-orders" aria-selected="false">Purchase Orders</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link fw-bold text-dark" id="shipping-orders-tab" data-bs-toggle="tab" data-bs-target="#shipping-orders" type="button" role="tab" aria-controls="shipping-orders" aria-selected="false">Incoming Shipping Orders (Relay)</button>
            </li>
        </ul>

        <!-- Tabs Content -->
        <div class="tab-content border-start border-end border-bottom bg-white p-4 shadow-sm" id="warehouseTabsContent">
            
            <!-- Vendor Deliveries Tab -->
            <div class="tab-pane fade show active" id="vendor-deliveries" role="tabpanel" aria-labelledby="vendor-deliveries-tab">
                <h6 class="fw-bold mb-3">Accept Vendor Deliveries</h6>
                <p class="text-muted small mb-4">Receive and inspect items from vendors</p>
                <form id="acceptVendorForm" class="row g-3">
                    <div class="col-md-3">
                        <label class="form-label fw-bold small">Vendor</label>
                        <input type="text" id="acceptVendorId" class="form-control" list="vendorList" placeholder="Search vendor by name or ID" autocomplete="off" required>
                        <datalist id="vendorList"></datalist>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label fw-bold small">Order ID</label>
                        <input type="text" id="acceptOrderId" class="form-control" list="orderList" placeholder="Search Order ID" autocomplete="off" required>
                        <datalist id="orderList"></datalist>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label fw-bold small">Weight (kg)</label>
                        <input type="number" id="acceptWeight" class="form-control" step="0.01" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label fw-bold small">Return Items?</label>
                        <select id="acceptReturnType" class="form-select" onchange="toggleReturnFields()">
                            <option value="NONE">No (Full Accept)</option>
                            <option value="PARTIAL">Partial Return</option>
                            <option value="FULL">Full Return</option>
                        </select>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-success w-100 fw-bold">Accept to WH</button>
                    </div>
                    <div class="col-md-9 d-none" id="returnReasonDiv">
                        <label class="form-label fw-bold small">Return Reason</label>
                        <input type="text" id="acceptReturnReason" class="form-control" placeholder="Explain reason for return">
                    </div>
                    <div class="col-md-3 d-none" id="returnQtyDiv">
                        <label class="form-label fw-bold small">Return Quantity</label>
                        <input type="number" id="acceptReturnQty" class="form-control" min="1">
                    </div>
                </form>
            </div>

            <!-- Inventory Tab -->
            <div class="tab-pane fade" id="inventory-count" role="tabpanel" aria-labelledby="inventory-count-tab">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h6 class="fw-bold mb-0">Inventory Count</h6>
                    <button class="btn btn-sm btn-primary" onclick="promptInventory()">Update Stock</button>
                </div>
                <table class="table table-hover mb-0 align-middle">
                    <thead class="table-light"><tr><th>SKU</th><th>Name</th><th>Location</th><th>Qty</th></tr></thead>
                    <tbody id="invTableBody"><tr><td colspan="4" class="text-center py-4">No inventory data.</td></tr></tbody>
                </table>
            </div>

            <!-- PO Tab -->
            <div class="tab-pane fade" id="purchase-orders" role="tabpanel" aria-labelledby="purchase-orders-tab">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h6 class="fw-bold mb-0">Purchase Orders</h6>
                    <button class="btn btn-sm btn-primary" onclick="promptPO()">Create PO</button>
                </div>
                <table class="table table-hover mb-0 align-middle">
                    <thead class="table-light"><tr><th>ID</th><th>Vendor ID</th><th>SKU</th><th>Qty</th><th>Status</th><th>Actions</th></tr></thead>
                    <tbody id="poTableBody"><tr><td colspan="6" class="text-center py-4">No purchase orders found.</td></tr></tbody>
                </table>
            </div>

            <!-- Shipping Orders Tab -->
            <div class="tab-pane fade" id="shipping-orders" role="tabpanel" aria-labelledby="shipping-orders-tab">
                <h6 class="fw-bold mb-3">Incoming Shipping Orders (Relay)<span id="warehouseNameDisplay"></span></h6>
                <table class="table table-hover mb-0 align-middle">
                    <thead class="table-light"><tr><th>Order ID</th><th>Status</th><th>Assigned Warehouse</th><th>Assigned Carrier</th><th>Dimensions</th><th>Shipment Date/Time</th><th>Actions</th></tr></thead>
                    <tbody id="shippingTableBody"></tbody>
                </table>
            </div>

        </div>
    </div>
</main>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
const CTX = '${pageContext.request.contextPath}';
let currentUser = null;
let currentUserWarehouseName = null;
let globalShippingOrders = [];
let allVendorsCache = [];

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

async function initVendorSearch() {
    try {
        const res = await apiCall(CTX + '/api/ops/vendors');
        if (res && res.ok) {
            const d = await res.json();
            allVendorsCache = d.data;
            const dl = document.getElementById('vendorList');
            dl.innerHTML = '';
            d.data.forEach(v => {
                const opt = document.createElement('option');
                opt.value = v.id + ' - ' + v.companyName;
                dl.appendChild(opt);
            });
        }
    } catch(e) { console.error("Error loading vendors:", e); }
}

document.getElementById('acceptVendorId').addEventListener('input', function() {
    const val = this.value.trim();
    let vendorId = parseInt(val.split(' - ')[0]);
    if (isNaN(vendorId)) {
        const found = allVendorsCache.find(v => v.companyName.toLowerCase() === val.toLowerCase() || v.id.toString() === val);
        if (found) vendorId = found.id;
    }
    
    const dl = document.getElementById('orderList');
    dl.innerHTML = '';
    
    if (!isNaN(vendorId)) {
        const vendorOrders = globalShippingOrders.filter(o => o.vendor && o.vendor.id === vendorId);
        vendorOrders.forEach(o => {
            if(o.status !== 'IN_WAREHOUSE' && o.status !== 'SHIPPED' && o.status !== 'DELIVERED') {
                const opt = document.createElement('option');
                opt.value = o.orderId;
                opt.label = 'Status: ' + o.status + ', SKU: ' + (o.productSku || 'N/A');
                dl.appendChild(opt);
            }
        });
    }
});

async function loadData() {
    // Load Inventory
    let res = await apiCall(CTX + '/api/warehouse/inventory');
    if (res && res.ok) {
        let d = await res.json();
        let tbody = document.getElementById('invTableBody');
        tbody.innerHTML = '';
        if (d.data.length === 0) tbody.innerHTML = '<tr><td colspan="4" class="text-center py-4">No inventory.</td></tr>';
        d.data.forEach(i => tbody.innerHTML += "<tr><td class=\"fw-bold\">" + i.sku + "</td><td>" + i.name + "</td><td>" + i.location + "</td><td>" + i.quantity + "</td></tr>");
    }

    // Load POs
    res = await apiCall(CTX + "/api/warehouse/orders");
    if (res && res.ok) {
        let d = await res.json();
        let tbody = document.getElementById("poTableBody");
        tbody.innerHTML = "";
        if (d.data.length === 0) tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4">No purchase orders found.</td></tr>';
        d.data.forEach(po => {
            tbody.innerHTML += `<tr>
                <td>PO-${po.id}</td>
                <td>${po.vendor ? po.vendor.companyName : "-"}</td>
                <td>${po.sku}</td>
                <td>${po.quantity}</td>
                <td><span class="badge bg-secondary">${po.status}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="updatePO(${po.id}, 'IN_PRODUCTION')">In Prod</button>
                    <button class="btn btn-sm btn-outline-success" onclick="updatePO(${po.id}, 'DELIVERED')">Deliver</button>
                </td>
            </tr>`;
        });
    }
    
    // Load Shipping Orders
    res = await apiCall(CTX + "/api/ops/orders");
    if (res && res.ok) {
        let d = await res.json();
        let tbody = document.getElementById("shippingTableBody");
        tbody.innerHTML = "";
        globalShippingOrders = d.data;
        
        // Auto trigger input event to populate orderList if vendor is already typed
        document.getElementById('acceptVendorId').dispatchEvent(new Event('input'));

        const relevantOrders = d.data.filter(o => 
            ["IN_WAREHOUSE", "WAREHOUSE_VERIFIED", "SHIPPED"].includes(o.status) &&
            o.assignedCarrier && 
            (!currentUserWarehouseName || (o.assignedWarehouse && o.assignedWarehouse.name === currentUserWarehouseName))
        );
        if (relevantOrders.length === 0) tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4">No incoming orders with an assigned carrier.</td></tr>';
        
        relevantOrders.forEach(o => {
            let acts = "";
            if (o.status === "SHIPPED") {
                acts = `<span class="badge bg-success">Shipped</span>`;
            } else {
                acts = '<button class="btn btn-sm btn-primary fw-bold" onclick="shipOrder(' + o.id + ')">Mark as Shipped</button>';
            }
            
            tbody.innerHTML += "<tr>" +
                "<td><strong>" + o.orderId + "</strong></td>" +
                "<td><span class=\"badge bg-info\">" + o.status + "</span></td>" +
                "<td>" + (o.assignedWarehouse ? o.assignedWarehouse.name : "-") + "</td>" +
                                  "<td>" + (o.assignedCarrier ? o.assignedCarrier.companyName : "-") + "</td>" +
                  "<td>" + (o.orderDimensions || "-") + "</td>" +
                  "<td>" + (o.shipmentDateTime ? o.shipmentDateTime.replace('T', ' ') : "-") + "</td>" +
                  "<td>" + acts + "</td>" +
            "</tr>";
        });
    }
}

async function promptInventory() {
    let sku = prompt("SKU:"); if(!sku) return;
    let name = prompt("Name:");
    let qty = prompt("Quantity:");
    let loc = prompt("Location:");
    await apiCall(CTX + '/api/warehouse/inventory?sku=' + sku + '&name=' + name + '&qty=' + qty + '&loc=' + loc, 'POST');
    loadData();
}

async function promptPO() {
    let vid = prompt("Vendor ID:"); if(!vid) return;
    let sku = prompt("SKU:");
    let qty = prompt("Quantity:");
    await apiCall(CTX + '/api/warehouse/orders?vendorId=' + vid + '&sku=' + sku + '&qty=' + qty, 'POST');
    loadData();
}

async function verifyWarehouseReceipt(id) {
    if(confirm("Verify receipt of this order at the warehouse?")) {
        await apiCall(CTX + "/api/ops/orders/" + id + "/verify-receipt", "PUT");
        loadData();
    }
}
async function shipOrder(id) {
    if(confirm("Handover to carrier and mark as SHIPPED?")) {
        await apiCall(CTX + "/api/ops/orders/" + id + "/ship", "PUT");
        loadData();
    }
}
async function updatePO(id, status) {
    await apiCall(CTX + '/api/warehouse/orders/' + id + '/status?status=' + status, 'PUT');
    loadData();
}

function toggleReturnFields() {
    const type = document.getElementById('acceptReturnType').value;
    const rDiv = document.getElementById('returnReasonDiv');
    const qDiv = document.getElementById('returnQtyDiv');
    if (type === 'NONE') {
        rDiv.classList.add('d-none');
        qDiv.classList.add('d-none');
        document.getElementById('acceptReturnReason').removeAttribute('required');
        document.getElementById('acceptReturnQty').removeAttribute('required');
    } else {
        rDiv.classList.remove('d-none');
        qDiv.classList.remove('d-none');
        document.getElementById('acceptReturnReason').setAttribute('required', 'required');
        if (type === 'PARTIAL') {
            document.getElementById('acceptReturnQty').setAttribute('required', 'required');
        } else {
            document.getElementById('acceptReturnQty').removeAttribute('required');
        }
    }
}

document.getElementById('acceptVendorForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const vval = document.getElementById('acceptVendorId').value.trim();
    let vid = parseInt(vval.split(' - ')[0]);
    if (isNaN(vid)) {
        const found = allVendorsCache.find(v => v.companyName.toLowerCase() === vval.toLowerCase() || v.id.toString() === vval);
        if (found) vid = found.id;
    }

    const oid = document.getElementById('acceptOrderId').value.trim();
    
    if (isNaN(vid) || !oid) {
        alert("Please select a vendor and an order ID.");
        return;
    }

    const order = globalShippingOrders.find(o => o.orderId.toUpperCase() === oid.toUpperCase() && o.vendor && o.vendor.id === vid);
    if (!order) {
        alert("Order ID not found for the selected vendor. Please check the ID.");
        return;
    }
    
    const payload = {
        weight: parseFloat(document.getElementById('acceptWeight').value),
        returnType: document.getElementById('acceptReturnType').value,
        returnReason: document.getElementById('acceptReturnReason').value,
        returnQuantity: parseInt(document.getElementById('acceptReturnQty').value) || 0
    };
    
    const res = await apiCall(CTX + '/api/ops/orders/' + order.id + '/accept-vendor', 'PUT', payload);
    if (res && res.ok) {
        alert("Order accepted and Ops team notified!");
        document.getElementById('acceptVendorForm').reset();
        toggleReturnFields();
        loadData();
    } else {
        alert("Failed to accept order.");
    }
});

document.addEventListener('DOMContentLoaded', async () => {
    const res = await apiCall(CTX + '/api/users/me');
    if (res && res.ok) {
        const data = await res.json();
        currentUser = data.data;
        applyRoleVisibility([currentUser.role]);

        if (currentUser.role === 'WAREHOUSE_MGR') {
            const resW = await apiCall(CTX + '/api/ops/warehouses');
            if (resW && resW.ok) {
                const wData = await resW.json();
                const myW = wData.data.find(w => w.manager && w.manager.id === currentUser.id);
                if (myW) {
                    currentUserWarehouseName = myW.name;
                    document.getElementById('warehouseNameDisplay').innerHTML = ' &mdash; <span class="badge bg-secondary">' + myW.name + '</span>';
                }
            }
        }
        
        document.getElementById('userProfileInfo').innerHTML = 
            '<strong>' + currentUser.username + '</strong><br><small>' + currentUser.role + '</small>';
    }
    initVendorSearch();
    loadData();
});
</script>
</body>
</html>










