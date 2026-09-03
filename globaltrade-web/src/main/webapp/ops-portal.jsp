<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Operations</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --gt-dark: #0f172a;
            --gt-primary: #d97706; /* Elegant amber/gold */
            --gt-card-bg: rgba(255, 255, 255, 0.98);
        }
        body {
            background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
            color: #f8fafc;
            font-family: 'Inter', sans-serif;
            min-height: 100vh;
        }
        .navbar {
            background: rgba(15, 23, 42, 0.9) !important;
            backdrop-filter: blur(12px);
            border-bottom: 1px solid rgba(255,255,255,0.05);
            padding: 1rem 2rem;
        }
        .navbar-brand { color: #f8fafc !important; font-weight: 700; letter-spacing: 0.5px; }
        .navbar-brand i { color: var(--gt-primary) !important; }
        
        .nav-tabs { border-bottom: 1px solid rgba(255,255,255,0.1); margin-bottom: 2rem; }
        .nav-tabs .nav-link { 
            color: #94a3b8; border: none; padding: 1rem 1.5rem; 
            font-weight: 500; transition: all 0.3s ease; 
        }
        .nav-tabs .nav-link:hover { color: #f8fafc; background: rgba(255,255,255,0.02); border-radius: 8px 8px 0 0; }
        .nav-tabs .nav-link.active { 
            background: transparent; color: var(--gt-primary); 
            border-bottom: 3px solid var(--gt-primary);
        }
        
        .table-card {
            background: var(--gt-card-bg);
            border-radius: 16px;
            padding: 2.5rem;
            box-shadow: 0 20px 40px rgba(0,0,0,0.3);
            color: #334155;
        }
        .table { margin-top: 1rem; }
        .table-light th { 
            background: #f8fafc; color: #64748b; font-weight: 600; 
            text-transform: uppercase; font-size: 0.75rem; letter-spacing: 0.5px; 
            border-bottom: 2px solid #e2e8f0; padding: 1rem;
        }
        .table td { border-bottom: 1px solid #f1f5f9; vertical-align: middle; padding: 1rem; }
        .badge { padding: 0.5em 0.8em; border-radius: 6px; font-weight: 500; }
        
        .section-header { font-size: 1.5rem; font-weight: 700; color: #0f172a; margin-bottom: 0.5rem; }
        .section-sub { color: #64748b; font-size: 0.9rem; margin-bottom: 2rem; }
        
        .btn-primary { background: var(--gt-primary); border: none; }
        .btn-primary:hover { background: #059669; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg px-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard.jsp">
            <i class="bi bi-gear-fill me-2"></i>Ops Center
        </a>
        <div class="d-flex align-items-center gap-4">
            <span class="text-light fs-6">Operations Command</span>
            <button class="btn btn-sm btn-outline-light rounded-pill px-3" onclick="doLogout()">Sign Out</button>
        </div>
    </div>
</nav>

<div class="container mt-4">
    <ul class="nav nav-tabs" id="myTab" role="tablist">
        <li class="nav-item">
            <button class="nav-link active" id="orders-tab" data-bs-toggle="tab" data-bs-target="#orders-pane" type="button"><i class="bi bi-inboxes me-2"></i>Requisitions (Orders)</button>
        </li>
        <li class="nav-item">
            <button class="nav-link" id="shipments-tab" data-bs-toggle="tab" data-bs-target="#shipments-pane" type="button"><i class="bi bi-truck me-2"></i>Logistics & Carriers</button>
        </li>
        <li class="nav-item">
            <button class="nav-link" id="categories-tab" data-bs-toggle="tab" data-bs-target="#categories-pane" type="button"><i class="bi bi-tags me-2"></i>Commodity Categories</button>
        </li>
    </ul>

    <div class="tab-content">
        <!-- ORDERS PANE -->
        <div class="tab-pane fade show active" id="orders-pane">
            <div class="table-card">
                <h3 class="section-header">Client Requisitions</h3>
                <p class="section-sub">Assign global orders to the optimal Vendor facility.</p>
                
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Order ID</th>
                            <th>Client / Recipient</th>
                            <th>Items</th>
                            <th>Weight</th>
                            <th>Timeline</th>
                            <th>Vendor & Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="ordersBody"></tbody>
                </table>
            </div>
        </div>

                <!-- CATEGORIES PANE -->
        <div class="tab-pane fade" id="categories-pane" role="tabpanel">
            <div class="table-card bg-white">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h3 class="section-header">Commodity Categories</h3>
                        <p class="section-sub mb-0">Manage product categories for vendor registration.</p>
                    </div>
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createCategoryModal"><i class="bi bi-plus-circle me-1"></i>New Category</button>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Category ID</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody id="categoriesTbody">
                            <tr><td colspan="4" class="text-center text-muted">Loading categories...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- SHIPMENTS PANE -->
        <div class="tab-pane fade" id="shipments-pane">
            <div class="table-card">
                <h3 class="section-header">Active Operations</h3>
                <p class="section-sub">Assign carriers and track global logistics progress.</p>
                
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Tracking #</th>
                            <th>Origin / Dest</th>
                            <th>Status</th>
                            <th>Carrier</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="shipmentsBody"></tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Assign Vendor Modal -->
<div class="modal fade" id="assignVendorModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content border-0 shadow-lg" style="border-radius:16px;">
            <div class="modal-header bg-light border-0 px-4 py-3">
                <h5 class="modal-title fw-bold">Assign Vendor</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body px-4 py-4">
                <input type="hidden" id="assignOrderId">
                <label class="form-label text-muted small fw-bold">SELECT VENDOR</label>
                <select class="form-select" id="vendorSelect"></select>
            </div>
            <div class="modal-footer border-0 px-4 pb-4">
                <button type="button" class="btn btn-primary px-4" onclick="submitVendorAssign()">Assign Order</button>
            </div>
        </div>
    </div>
</div>

<!-- Assign Carrier / Status Modal -->
<div class="modal fade" id="carrierModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content border-0 shadow-lg" style="border-radius:16px;">
            <div class="modal-header bg-light border-0 px-4 py-3">
                <h5 class="modal-title fw-bold">Update Logistics</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body px-4 py-4">
                <input type="hidden" id="manageShipmentId">
                <div class="mb-3">
                    <label class="form-label text-muted small fw-bold">CARRIER / TRANSPORTER</label>
                    <input type="text" class="form-control" id="carrierInput" placeholder="e.g. DHL, FedEx">
                </div>
                <div class="mb-3">
                    <label class="form-label text-muted small fw-bold">SHIPMENT STATUS</label>
                    <select class="form-select" id="statusSelect">
                        <option value="PENDING">Pending</option>
                        <option value="IN_WAREHOUSE">In Warehouse</option>
                        <option value="SHIPPED">Shipped (In Transit)</option>
                        <option value="ON_DELIVERY">On Delivery</option>
                        <option value="DELIVERED">Delivered</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer border-0 px-4 pb-4">
                <button type="button" class="btn btn-primary px-4" onclick="submitLogisticsUpdate()">Save Updates</button>
            </div>
        </div>
    </div>
</div>

<!-- Toast Container for Real Time Notifications -->
<div class="toast-container position-fixed bottom-0 end-0 p-3" style="z-index: 9999;">
  <div id="opsToast" class="toast align-items-center text-white bg-primary border-0" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body" id="opsToastBody">
        Notification
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
const CTX = '${pageContext.request.contextPath}';
let vendorModal, carrierModal;
let knownOrderIds = new Set();
let currentOpsOrders = [];

function viewOrderDetails(idStr) {
    const id = parseInt(idStr);
    const o = currentOpsOrders.find(x => x.id === id);
    if(!o) return;
    document.getElementById('opsOrdId').innerText = '#' + o.orderId;
    document.getElementById('opsOrdCustomer').innerText = o.customerFullName;
    document.getElementById('opsOrdMobile').innerText = o.mobileNumber || '-';
    document.getElementById('opsOrdDesc').innerText = o.description || '-';
    document.getElementById('opsOrdDesign').innerHTML = o.designDocumentUrl ? '<a href="' + o.designDocumentUrl + '" target="_blank">View Design Doc</a>' : 'N/A';
    document.getElementById('opsOrdQuality').innerHTML = o.qualityStandardUrl ? '<a href="' + o.qualityStandardUrl + '" target="_blank">View Quality Standards</a>' : 'N/A';
    
    document.getElementById('opsOrdDecision').innerText = o.vendorDecision || 'PENDING';
    document.getElementById('opsOrdReason').innerText = o.vendorReason || '-';
    document.getElementById('opsOrdDate').innerText = o.vendorProposedDate || '-';
    
    new bootstrap.Modal(document.getElementById('opsOrderModal')).show();
}
let firstLoad = true;
let opsPollingInterval;

function showToast(message) {
    document.getElementById('opsToastBody').innerText = message;
    const toast = new bootstrap.Toast(document.getElementById('opsToast'));
    toast.show();
}

async function apiCall(url, method='GET', body=null) {
    let opts = { method, headers: { 'Content-Type': 'application/json' } };
    if(body) opts.body = JSON.stringify(body);
    const res = await fetch(url, opts);
    if(res.status === 401) { window.location.href = CTX + '/login.jsp'; return null; }
    return res;
}

async function doLogout() {
    await apiCall(CTX+'/api/auth/logout', 'POST');
    window.location.href = CTX+'/login.jsp';
}

async function loadCategories() {
    const res = await apiCall(CTX + '/api/ops/categories');
    if(res && res.ok) {
        const d = await res.json();
        let newHtml = '';
        d.data.forEach(c => {
            newHtml += '<tr>' +
                    '<td class="fw-bold">CAT-' + c.id + '</td>' +
                    '<td>' + c.name + '</td>' +
                    '<td>' + (c.description || '-') + '</td>' +
                    '<td class="text-end text-nowrap">' + '<button class="btn btn-sm btn-outline-danger" onclick="deleteCategory(' + c.id + ')"><i class="bi bi-trash"></i></button>' +
                    '</td>' +
                '</tr>';
        });
        if (d.data.length === 0) newHtml = '<tr><td colspan="4" class="text-center text-muted">No categories found.</td></tr>';
        const tbody = document.getElementById('categoriesTbody');
        if (tbody && tbody.innerHTML !== newHtml) { tbody.innerHTML = newHtml; }
    }
}
async function createCategory() {
    const name = document.getElementById('catName').value;
    const desc = document.getElementById('catDesc').value;
    if(!name) return;
    await apiCall(CTX + '/api/ops/categories', 'POST', { name: name, description: desc });
    bootstrap.Modal.getInstance(document.getElementById('createCategoryModal')).hide();
    document.getElementById('catName').value = '';
    document.getElementById('catDesc').value = '';
    loadCategories();
}
async function deleteCategory(id) {
    if(!confirm('Are you sure you want to delete this category?')) return;
    await apiCall(CTX + '/api/ops/categories/' + id, 'DELETE');
    loadCategories();
}

async function loadOps() {
    await loadOrders();
    await loadShipments();
            loadCategories();
    await loadCategories();
    firstLoad = false;
    
    // Start real-time polling every 3 seconds
    if (!opsPollingInterval) {
        opsPollingInterval = setInterval(() => {
            loadOrders();
            loadShipments();
            loadCategories();
        }, 3000);
    }
}

async function loadOrders() {
    const res = await apiCall(CTX + '/api/ops/orders');
    if(res && res.ok) {
        const d = await res.json();
        let newHtml = '';
        let currentIds = new Set();
        currentOpsOrders = d.data;
        d.data.forEach(o => {
            currentIds.add(o.id);
            if (!firstLoad && !knownOrderIds.has(o.id)) {
                showToast("Real-Time Alert: New Client Requisition Received! (Order #" + o.orderId + ")");
            }
            
            let decisionHtml = '';
            if (o.vendor) {
                const dec = o.vendorDecision || 'PENDING';
                let bg = 'secondary';
                if(dec === 'ACCEPTED') bg = 'success';
                else if(dec === 'REJECTED') bg = 'danger';
                else if(dec === 'PROPOSED_DATE') bg = 'warning';
                
                decisionHtml = '<span class="badge bg-'+bg+'">'+dec+'</span>';
                if(dec === 'REJECTED' || dec === 'PROPOSED_DATE') {
                    if (!firstLoad && (!knownOrderIds.has(o.id + '_' + dec))) {
                        showToast("Real-Time Alert: Vendor responded to Order #" + o.orderId + " with " + dec);
                    }
                    knownOrderIds.add(o.id + '_' + dec);
                }
            }
            
            let acts = '';
            if (o.vendor) {
                acts = '<button class="btn btn-sm btn-outline-info" onclick="viewOrderDetails(\''+o.id+'\')">View</button>' ;
                if (o.vendorDecision === 'REJECTED' || o.vendorDecision === 'PROPOSED_DATE') {
                    acts += '<button class="btn btn-sm btn-outline-primary ms-1" onclick="openVendorModal('+o.id+')">Reassign</button>';
                }
            } else {
                acts = '<button class="btn btn-sm btn-outline-primary" onclick="openVendorModal('+o.id+')">Assign Vendor</button>';
                acts += '<button class="btn btn-sm btn-outline-info ms-1" onclick="viewOrderDetails(\''+o.id+'\')">View</button>';
            }
            
            let statusHtml = "";
            if (o.status && o.status !== "PENDING" && o.status !== "IN_PROGRESS") {
                statusHtml = "<br><span class=\"badge bg-info text-dark\">" + o.status.replace(/_/g, " ") + "</span>";
            }
            let vend = o.vendor ? o.vendor.companyName + "<br>" + decisionHtml + statusHtml : "<span class=\"text-warning\">Unassigned</span>";
            
            if (o.status === "READY_FOR_DELIVERY") {
                acts += "<button class=\"btn btn-sm btn-primary ms-1\" onclick=\"assignCarrierPrompt(" + o.id + ")\">Assign Carrier</button>";
            } else if (o.status === "ORDER_COMPLETED" || o.status === "IN_WAREHOUSE") {
                if (!o.assignedWarehouse) {
                    acts += "<button class=\"btn btn-sm btn-secondary ms-1\" onclick=\"assignWarehousePrompt(" + o.id + ")\">Assign Warehouse</button>";
                }
            }
            
            newHtml += "<tr>" +
                    "<td class=\"fw-bold\">" + o.orderId + "</td>" +
                    "<td>" + o.customerFullName + "</td>" +
                    "<td>" + (o.itemCount || "-") + "</td>" +
                    "<td>" + (o.weight ? o.weight + " kg" : "-") + "</td>" +
                    "<td>" + (o.expectedTimeline || "-") + "</td>" +
                    "<td class=\"fw-bold\">" + vend + "</td>" +
                    "<td class=\"text-end text-nowrap\">" + acts + "</td>" +
                "</tr>";
        });
        
        knownOrderIds = currentIds;
        
        const tbody = document.getElementById('ordersBody');
        if (tbody.innerHTML !== newHtml) {
            tbody.innerHTML = newHtml;
        }
    }
}

async function loadShipments() {
    const res = await apiCall(CTX + '/api/ops/shipments');
    if(res && res.ok) {
        const d = await res.json();
        let newHtml = '';
        d.data.forEach(s => {
            let carr = s.carrierName ? s.carrierName : '<span class="text-danger small fw-bold">NO CARRIER</span>';
            let stat = '<span class="badge bg-secondary">' + s.status.replace(/_/g, ' ') + '</span>';
            if(s.status === 'DELIVERED') stat = '<span class="badge bg-success">DELIVERED</span>';
            if(s.status === 'SHIPPED') stat = '<span class="badge bg-primary">SHIPPED</span>';
            
            newHtml += '<tr>' +
                    '<td class="fw-bold">' + s.trackingNumber + '</td>' +
                    '<td>' + (s.origin || '-') + ' &rarr; ' + (s.destination || '-') + '</td>' +
                    '<td>' + stat + '</td>' +
                    '<td class="fw-bold text-dark">' + carr + '</td>' +
                    '<td class="text-end text-nowrap">' + '<button class="btn btn-sm btn-outline-dark" onclick="openCarrierModal('+s.id+', \''+(s.carrierName||'')+'\', \''+s.status+'\')">Manage</button>' +
                    '</td>' +
                '</tr>';
        });
        
        const tbody = document.getElementById('shipmentsBody');
        if (tbody.innerHTML !== newHtml) {
            tbody.innerHTML = newHtml;
        }
    }
}

async function assignWarehousePrompt(id) {
    let w = prompt("Enter Warehouse Name to assign:");
    if(w) {
        await apiCall(CTX + '/api/ops/orders/' + id + '/assign-warehouse', 'PUT', {warehouseName: w});
        loadOrders();
    }
}
async function assignCarrierPrompt(id) {
    let c = prompt("Enter Carrier Name to assign:");
    if(c) {
        await apiCall(CTX + '/api/ops/orders/' + id + '/assign-carrier', 'PUT', {carrierName: c});
        loadOrders();
    }
}
async function openVendorModal(orderId) {
    document.getElementById('assignOrderId').value = orderId;
    const res = await apiCall(CTX + '/api/ops/vendors');
    if(res && res.ok) {
        const d = await res.json();
        const sel = document.getElementById('vendorSelect');
        sel.innerHTML = '<option value="">Select Vendor...</option>';
        d.data.forEach(v => {
            sel.innerHTML += '<option value="'+v.id+'">'+v.companyName+'</option>';
        });
    }
    if(!vendorModal) vendorModal = new bootstrap.Modal(document.getElementById('assignVendorModal'));
    vendorModal.show();
}

async function submitVendorAssign() {
    const oid = document.getElementById('assignOrderId').value;
    const vid = document.getElementById('vendorSelect').value;
    if(!vid) return;
    
    await apiCall(CTX + '/api/ops/orders/'+oid+'/assign?vendorId='+vid, 'PUT');
    vendorModal.hide();
    loadOrders();
}

function openCarrierModal(shipId, currentCarrier, currentStatus) {
    document.getElementById('manageShipmentId').value = shipId;
    document.getElementById('carrierInput').value = currentCarrier;
    document.getElementById('statusSelect').value = currentStatus;
    if(!carrierModal) carrierModal = new bootstrap.Modal(document.getElementById('carrierModal'));
    carrierModal.show();
}

async function submitLogisticsUpdate() {
    const sid = document.getElementById('manageShipmentId').value;
    const carr = document.getElementById('carrierInput').value;
    const stat = document.getElementById('statusSelect').value;
    
    if(carr) await apiCall(CTX + '/api/ops/shipments/'+sid+'/carrier?carrier='+encodeURIComponent(carr), 'PUT');
    await apiCall(CTX + '/api/ops/shipments/'+sid+'/status?status='+encodeURIComponent(stat), 'PUT');
    
    carrierModal.hide();
    loadShipments();
            loadCategories();
}

document.addEventListener('DOMContentLoaded', loadOps);
</script>
<!-- View Order Details Modal -->
<div class="modal fade" id="opsOrderModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Requisition Details <span id="opsOrdId" class="badge bg-primary ms-2"></span></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p><strong>Customer:</strong> <span id="opsOrdCustomer"></span></p>
                <p><strong>Mobile:</strong> <span id="opsOrdMobile"></span></p>
                <p><strong>Description:</strong> <span id="opsOrdDesc"></span></p>
                
                <h6 class="fw-bold mt-3 border-top pt-3">Client Documents</h6>
                <p><strong>Design Document:</strong> <span id="opsOrdDesign"></span></p>
                <p><strong>Quality Standards:</strong> <span id="opsOrdQuality"></span></p>

                <h6 class="fw-bold mt-3 border-top pt-3">Vendor Response</h6>
                <p><strong>Decision:</strong> <span id="opsOrdDecision"></span></p>
                <p><strong>Reason/Note:</strong> <span id="opsOrdReason"></span></p>
                <p><strong>Proposed Date:</strong> <span id="opsOrdDate"></span></p>
            </div>
        </div>
    </div>
</div>

<!-- Create Category Modal -->
<div class="modal fade" id="createCategoryModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">New Commodity Category</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
            <label class="form-label">Category Name</label>
            <input type="text" id="catName" class="form-control" placeholder="e.g. Electronics"/>
        </div>
        <div class="mb-3">
            <label class="form-label">Description</label>
            <input type="text" id="catDesc" class="form-control" placeholder="Short description"/>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" onclick="createCategory()">Create</button>
      </div>
    </div>
  </div>
</div>
</body>
</html>























