<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enterprise Portal - GlobalTrade Logistics</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Google Fonts for Premium Typography -->
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
            animation: fadeIn 0.6s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .table { color: #334155; margin-top: 1rem; }
        .table-light th { 
            background: #f8fafc; color: #64748b; font-weight: 600; 
            text-transform: uppercase; font-size: 0.75rem; letter-spacing: 0.5px; 
            border-bottom: 2px solid #e2e8f0; padding: 1rem;
        }
        .table td { border-bottom: 1px solid #f1f5f9; vertical-align: middle; padding: 1rem; }
        .badge { padding: 0.5em 0.8em; border-radius: 6px; font-weight: 500; }
        
        /* Animated Progress Stepper */
        .shipment-anim-card {
            border: 1px solid #e2e8f0; border-radius: 16px; padding: 2rem; 
            margin-bottom: 1.5rem; transition: transform 0.3s, box-shadow 0.3s;
            background: #fff;
        }
        .shipment-anim-card:hover { 
            transform: translateY(-3px); box-shadow: 0 12px 24px rgba(0,0,0,0.06); 
        }
        .track-stepper {
            display: flex; justify-content: space-between; position: relative; margin: 40px 0 20px 0;
        }
        .track-stepper::before {
            content: ''; position: absolute; top: 18px; left: 10%; width: 80%; height: 4px;
            background: #f1f5f9; z-index: 1; border-radius: 2px;
        }
        .track-progress-bar {
            position: absolute; top: 18px; left: 10%; height: 4px; background: var(--gt-primary); z-index: 2;
            border-radius: 2px; transition: width 1.5s cubic-bezier(0.4, 0, 0.2, 1); width: 0%;
        }
        .track-step {
            position: relative; z-index: 3; text-align: center; width: 25%;
        }
        .track-icon {
            width: 40px; height: 40px; border-radius: 50%; background: #f8fafc; color: #cbd5e1;
            display: inline-flex; align-items: center; justify-content: center; font-size: 16px;
            transition: all 0.5s ease; border: 4px solid #fff; margin: 0 auto;
        }
        .track-step.active .track-icon {
            background: var(--gt-primary); color: #fff;
            box-shadow: 0 0 0 6px rgba(217, 119, 6, 0.15);
            animation: pulse 2s infinite;
        }
        .track-step.completed .track-icon {
            background: var(--gt-primary); color: #fff; border-color: var(--gt-primary);
        }
        .track-label {
            margin-top: 12px; font-size: 0.75rem; font-weight: 600; color: #94a3b8; 
            text-transform: uppercase; letter-spacing: 0.5px;
        }
        .track-step.active .track-label { color: var(--gt-primary); }
        .track-step.completed .track-label { color: #475569; }

        @keyframes pulse {
            0% { box-shadow: 0 0 0 0 rgba(217, 119, 6, 0.4); }
            70% { box-shadow: 0 0 0 12px rgba(217, 119, 6, 0); }
            100% { box-shadow: 0 0 0 0 rgba(217, 119, 6, 0); }
        }
        @keyframes fadeIn { 
            from { opacity: 0; transform: translateY(15px); } 
            to { opacity: 1; transform: translateY(0); } 
        }

        /* Forms & Buttons */
        .form-control, .form-select { 
            border-radius: 8px; padding: 0.75rem 1rem; border-color: #e2e8f0; font-size: 0.95rem;
        }
        .form-control:focus, .form-select:focus { 
            border-color: var(--gt-primary); box-shadow: 0 0 0 4px rgba(217, 119, 6, 0.1); 
        }
        .btn-primary { 
            background: var(--gt-primary); border: none; border-radius: 8px; 
            padding: 0.6rem 1.5rem; font-weight: 600; letter-spacing: 0.3px;
        }
        .btn-primary:hover { background: #b45309; }
        
        .section-header { font-size: 1.5rem; font-weight: 700; color: #0f172a; margin-bottom: 0.5rem; }
        .section-sub { color: #64748b; font-size: 0.9rem; margin-bottom: 2rem; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg px-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard.jsp">
            <i class="bi bi-globe me-2"></i>GlobalTrade Enterprise
        </a>
        <div class="d-flex align-items-center gap-4">
            <span class="text-light fs-6" id="userNameDisplay">Welcome, Client</span>
            <button class="btn btn-sm btn-outline-light rounded-pill px-3" onclick="doLogout()">Sign Out</button>
        </div>
    </div>
</nav>

<div class="container mt-4">
    <ul class="nav nav-tabs" id="myTab" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="orders-tab" data-bs-toggle="tab" data-bs-target="#orders-pane" type="button"><i class="bi bi-briefcase me-2"></i>Purchase Orders</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="shipments-tab" data-bs-toggle="tab" data-bs-target="#shipments-pane" type="button"><i class="bi bi-activity me-2"></i>Live Tracking</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="payments-tab" data-bs-toggle="tab" data-bs-target="#payments-pane" type="button"><i class="bi bi-receipt me-2"></i>Billing & Payments</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="returns-tab" data-bs-toggle="tab" data-bs-target="#returns-pane" type="button"><i class="bi bi-arrow-return-left me-2"></i>RMA / Returns</button>
        </li>
        <li class="nav-item ms-auto" role="presentation">
            <button class="nav-link" id="settings-tab" data-bs-toggle="tab" data-bs-target="#settings-pane" type="button"><i class="bi bi-gear-fill me-2"></i>Account Settings</button>
        </li>
    </ul>

    <div class="tab-content" id="myTabContent">
        <!-- ORDERS -->
        <div class="tab-pane fade show active" id="orders-pane" role="tabpanel">
            <div class="table-card">
                <div class="d-flex justify-content-between align-items-end mb-4">
                    <div>
                        <h3 class="section-header">Order Management</h3>
                        <p class="section-sub mb-0">View and manage your global supply chain orders.</p>
                    </div>
                    <button class="btn btn-primary" onclick="openOrderModal()"><i class="bi bi-plus-lg me-2"></i>New Requisition</button>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Order ID</th>
                                <th>Supplier</th>
                                <th>Description</th>
                                <th>Status</th>
                                <th>Date</th>
                                <th class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody id="ordersTableBody"></tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- TRACKING -->
        <div class="tab-pane fade" id="shipments-pane" role="tabpanel">
            <div class="table-card">
                <h3 class="section-header">Live Shipment Tracking</h3>
                <p class="section-sub">Dynamic status and location tracking of your active logistics routes.</p>
                <div id="shipmentList"></div>
            </div>
        </div>

        <!-- PAYMENTS -->
        <div class="tab-pane fade" id="payments-pane" role="tabpanel">
            <div class="table-card">
                <div class="d-flex justify-content-between align-items-end mb-4">
                    <div>
                        <h3 class="section-header">Billing & Invoices</h3>
                        <p class="section-sub mb-0">Financial settlements for your global orders.</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/api/customers/reports/history" class="btn btn-outline-secondary"><i class="bi bi-download me-2"></i>Export Ledger</a>
                </div>
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Invoice ID</th>
                            <th>Order Ref</th>
                            <th>Payee (Vendor)</th>
                            <th>Amount</th>
                            <th>Status</th>
                            <th>Date Issued</th>
                        </tr>
                    </thead>
                    <tbody id="paymentsTableBody"></tbody>
                </table>
            </div>
        </div>

        <!-- RETURNS -->
        <div class="tab-pane fade" id="returns-pane" role="tabpanel">
            <div class="table-card">
                <h3 class="section-header">Return Merchandise Authorization</h3>
                <p class="section-sub">History of returned items and defect reporting.</p>
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>RMA ID</th>
                            <th>Order Ref</th>
                            <th>Item Details</th>
                            <th>Reason Code</th>
                            <th>Date Logged</th>
                        </tr>
                    </thead>
                    <tbody id="returnsTableBody"></tbody>
                </table>
            </div>
        </div>

        <!-- SETTINGS -->
        <div class="tab-pane fade" id="settings-pane" role="tabpanel">
            <div class="table-card mx-auto" style="max-width: 700px;">
                <h3 class="section-header text-center mb-1">Corporate Profile</h3>
                <p class="section-sub text-center mb-4">Amend your contact and company details.</p>
                
                <form id="profileForm">
                    <div class="mb-4">
                        <label class="form-label fw-bold text-muted small">CORPORATE ENTITY NAME</label>
                        <input type="text" class="form-control form-control-lg" id="profCompany" required>
                    </div>
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <label class="form-label fw-bold text-muted small">PRIMARY CONTACT NAME</label>
                            <input type="text" class="form-control" id="profName" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold text-muted small">AUTHORISED EMAIL</label>
                            <input type="email" class="form-control" id="profEmail" required>
                        </div>
                    </div>
                    <div class="text-center mt-5">
                        <button type="button" class="btn btn-primary px-5 py-2" onclick="saveProfile()"><i class="bi bi-check2-circle me-2"></i>Save Amendments</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Order Modal -->
<div class="modal fade" id="orderModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 shadow-lg" style="border-radius:16px;">
            <div class="modal-header bg-light border-0 px-4 py-3">
                <h5 class="modal-title fw-bold">New Supply Requisition</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body px-4 py-4">
                <form id="orderForm">
                    <div class="row mb-3">
                        <div class="col-md-12">
                            <label class="form-label text-muted small fw-bold">RECIPIENT NAME</label>
                            <input type="text" class="form-control" id="ordName" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label text-muted small fw-bold">COMMERCIAL GOODS DESCRIPTION</label>
                        <input type="text" class="form-control" id="ordDesc" required>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label text-muted small fw-bold">ITEM COUNT</label>
                            <input type="number" step="1" class="form-control" id="ordCount" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted small fw-bold">CONTACT NUMBER</label>
                            <input type="text" class="form-control" id="ordMobile" required>
                        </div>
                    </div>
                    
                    <h6 class="fw-bold mt-4 mb-3 border-bottom pb-2">Reference Documents</h6>
                    <div class="mb-3">
                        <label class="form-label text-muted small fw-bold">PRODUCT DESIGN DOCUMENT (URL)</label>
                        <input type="url" class="form-control" id="ordDesignDoc" placeholder="e.g. Google Drive Link">
                    </div>
                    <div class="mb-3">
                        <label class="form-label text-muted small fw-bold">EXPECTED QUALITY STANDARDS (URL)</label>
                        <input type="url" class="form-control" id="ordQualityDoc" placeholder="e.g. Dropbox Link">
                    </div>

                    <h6 class="fw-bold mt-4 mb-3 border-bottom pb-2">Delivery Details</h6>
                                        <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label text-muted small fw-bold">EXPECT TO START ORDER</label>
                            <input type="date" class="form-control" id="ordTimelineStart" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted small fw-bold">EXPECT TO RECEIVE ORDER</label>
                            <input type="date" class="form-control" id="ordTimelineEnd" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label text-muted small fw-bold">DESTINATION FACILITY / ADDRESS</label>
                        <input type="text" class="form-control" id="ordLine1" required>
                    </div>
                    <div class="row mb-2">
                        <div class="col-md-4">
                            <label class="form-label text-muted small fw-bold">CITY</label>
                            <input type="text" class="form-control" id="ordCity" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label text-muted small fw-bold">POSTAL CODE</label>
                            <input type="text" class="form-control" id="ordPostal" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label text-muted small fw-bold">COUNTRY</label>
                            <input type="text" class="form-control" id="ordCountry" required>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer border-0 px-4 pb-4">
                <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary px-4" onclick="submitOrder()">Submit Requisition</button>
            </div>
        </div>
    </div>
</div>

<!-- Return Modal -->
<div class="modal fade" id="returnModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content border-0 shadow-lg" style="border-radius:16px;">
            <div class="modal-header bg-light border-0 px-4 py-3">
                <h5 class="modal-title fw-bold">Initiate RMA</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body px-4 py-4">
                <input type="hidden" id="retOrderId">
                <div class="mb-3">
                    <label class="form-label text-muted small fw-bold">DEFECTIVE ITEM NAME</label>
                    <input type="text" class="form-control" id="retItem" required>
                </div>
                <div class="mb-3">
                    <label class="form-label text-muted small fw-bold">REASON FOR RETURN</label>
                    <textarea class="form-control" id="retReason" rows="3" required></textarea>
                </div>
            </div>
            <div class="modal-footer border-0 px-4 pb-4">
                <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger px-4" onclick="submitReturn()">Submit RMA</button>
            </div>
        </div>
    </div>
</div>

<!-- Toast Container for Real Time Notifications -->
<div class="toast-container position-fixed bottom-0 end-0 p-3" style="z-index: 9999;">
  <div id="custToast" class="toast align-items-center text-white bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body" id="custToastBody">
        Notification
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
const CTX = '${pageContext.request.contextPath}';
let orderModal = null;
let returnModal = null;
let knownShipmentStatuses = {};
let firstCustLoad = true;
let custPollingInterval;
let knownCustDecisions = new Set();


function showCustToast(message) {
    document.getElementById('custToastBody').innerText = message;
    const toast = new bootstrap.Toast(document.getElementById('custToast'));
    toast.show();
}

function showCustToast(message) {
    document.getElementById('custToastBody').innerText = message;
    const toast = new bootstrap.Toast(document.getElementById('custToast'));
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

async function loadPortal() {
    await loadProfile();
    await loadOrders();
    await loadShipments();
    await loadPayments();
    await loadReturns();
    firstCustLoad = false;
    
    if (!custPollingInterval) {
        custPollingInterval = setInterval(() => {
            loadOrders();
            loadShipments();
            loadPayments();
        }, 3000);
    }
}

async function loadProfile() {
    const res = await apiCall(CTX + '/api/customers/profile');
    if (res && res.ok) {
        const d = await res.json();
        document.getElementById('userNameDisplay').innerText = 'Welcome, ' + (d.data.companyName || d.data.fullName);
        document.getElementById('profName').value = d.data.fullName || '';
        document.getElementById('profEmail').value = d.data.email || '';
        document.getElementById('profCompany').value = d.data.companyName || '';
        document.getElementById('ordName').value = d.data.fullName || ''; 
    }
}

async function saveProfile() {
    const payload = {
        fullName: document.getElementById('profName').value,
        email: document.getElementById('profEmail').value,
        companyName: document.getElementById('profCompany').value
    };
    const res = await apiCall(CTX + '/api/customers/profile', 'PUT', payload);
    if(res && res.ok) {
        alert("Corporate profile amended successfully.");
        loadProfile();
    }
}

async function loadOrders() {
    const res = await apiCall(CTX + '/api/customers/orders');
    if(res && res.ok) {
        const d = await res.json();
        let newHtml = '';
        d.data.forEach(o => {
            if (o.vendorDecision) {
                const decKey = o.id + '_' + o.vendorDecision;
                if (!firstCustLoad && !knownCustDecisions.has(decKey)) {
                    showCustToast("Vendor for Order #" + o.orderId + " has responded: " + o.vendorDecision);
                }
                knownCustDecisions.add(decKey);
            }
            let actions = '';
            if (o.status === 'PENDING') {
                actions += `<button class="btn btn-sm btn-outline-warning me-2" onclick="cancelOrder(\${o.id})">Cancel</button>`;
            }
            if (o.status === 'CANCELLED') {
                actions += `<button class="btn btn-sm btn-outline-danger me-2" onclick="deleteOrder(\${o.id})">Delete</button>`;
            }
            if (o.status === 'DELIVERED') {
                actions += `<button class="btn btn-sm btn-outline-danger" onclick="openReturnModal(\${o.id})">RMA</button>`;
            }
            
            let statusBadge = `<span class="badge bg-secondary">\${o.status.replace(/_/g, ' ')}</span>`;
            if(o.status === 'DELIVERED') statusBadge = `<span class="badge bg-success">DELIVERED</span>`;
            if(o.status === 'SHIPPED') statusBadge = `<span class="badge bg-primary">SHIPPED</span>`;
            
            newHtml += `
                <tr>
                    <td class="fw-bold">\${o.orderId}</td>
                    <td>\${o.vendor ? o.vendor.companyName : '-'}</td>
                    <td>\${o.orderDescription || '-'}</td>
                    <td>\${statusBadge}</td>
                    <td>\${o.createdAt ? new Date(o.createdAt.replace('[UTC]', '')).toLocaleDateString() : '-'}</td>
                    <td class="text-end">\${actions}</td>
                </tr>
            `;
        });
        const tbody = document.getElementById('ordersTableBody');
        if (tbody.innerHTML !== newHtml) tbody.innerHTML = newHtml;
    }
}

function getStepperHtml(status) {
    // 0=Processing, 1=InTransit, 2=OutForDelivery, 3=Delivered
    let step = 0;
    if (['warehouse', 'IN_WAREHOUSE', 'SHIPPED', 'RECEIVED_SHIPMENT', 'IN_TRANSIT'].includes(status)) step = 1;
    if (['ON_DELIVERY'].includes(status)) step = 2;
    if (['DELIVERED'].includes(status)) step = 3;
    
    let w = (step / 3) * 100;
    
    let html = `
        <div class="track-stepper">
            <div class="track-progress-bar" style="width: 0%;" onload="this.style.width='\${w}%';"></div>
    `;
    // We'll set the width animation using a small timeout below
    
    const steps = [
        { label: "Processing", icon: "bi-box-seam" },
        { label: "In Transit", icon: "bi-airplane" },
        { label: "On Delivery", icon: "bi-truck" },
        { label: "Delivered", icon: "bi-check2-circle" }
    ];
    
    steps.forEach((s, idx) => {
        let classes = '';
        if (idx < step) classes = 'completed';
        else if (idx === step) classes = 'active';
        
        html += `
            <div class="track-step \${classes}">
                <div class="track-icon"><i class="bi \${s.icon}"></i></div>
                <div class="track-label">\${s.label}</div>
            </div>
        `;
    });
    html += '</div>';
    // Add inline script to trigger width animation after render
    html += `<script>setTimeout(()=>{document.querySelectorAll('.track-progress-bar')[document.querySelectorAll('.track-progress-bar').length-1].style.width='\${w}%';},50)<\/script>`;
    return html;
}

async function loadShipments() {
    const res = await apiCall(CTX + '/api/customers/shipments');
    if (res && res.ok) {
        const d = await res.json();
        const list = document.getElementById('shipmentList');
        list.innerHTML = '';
                let newHtml = '';
        d.data.forEach(s => {
            const currentStatus = s.status;
            if (!firstCustLoad && knownShipmentStatuses[s.id] && knownShipmentStatuses[s.id] !== currentStatus) {
                showCustToast("Real-Time Alert: Shipment " + s.trackingNumber + " status updated to " + currentStatus.replace(/_/g, ' ') + "!");
            }
            knownShipmentStatuses[s.id] = currentStatus;
            const origin = s.origin || 'Pending Dispatch';
            const dest = s.destination || 'Awaiting Routing';
            
            newHtml += `
                <div class="shipment-anim-card">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <span class="text-muted small fw-bold d-block mb-1">CONSIGNMENT REF</span>
                            <h5 class="mb-0 fw-bold text-dark">\${s.trackingNumber}</h5>
                        </div>
                        <div class="text-end">
                            <span class="text-muted small fw-bold d-block mb-1">CURRENT STATUS</span>
                            <span class="badge bg-dark px-3 py-2">\${s.status.replace(/_/g, ' ')}</span>
                        </div>
                    </div>
                    
                    <div class="row text-center mb-3">
                        <div class="col-5">
                            <p class="mb-0 text-muted small">Origin</p>
                            <p class="fw-bold mb-0">\${origin}</p>
                        </div>
                        <div class="col-2 d-flex align-items-center justify-content-center">
                            <i class="bi bi-arrow-right text-muted fs-4"></i>
                        </div>
                        <div class="col-5">
                            <p class="mb-0 text-muted small">Destination</p>
                            <p class="fw-bold mb-0">\${dest}</p>
                        </div>
                    </div>
                    \${getStepperHtml(s.status)}
                </div>
            `;
        });
    }
}

async function loadPayments() {
    const res = await apiCall(CTX + '/api/customers/payments');
    if(res && res.ok) {
        const d = await res.json();
        const tbody = document.getElementById('paymentsTableBody');
        tbody.innerHTML = '';
        d.data.forEach(p => {
            let stat = p.isPaid ? '<span class="badge bg-success">Settled</span>' : '<span class="badge bg-warning text-dark">Outstanding</span>';
            tbody.innerHTML += `
                <tr>
                    <td class="fw-bold">INV-\${p.id}</td>
                    <td>\${p.shippingOrder ? p.shippingOrder.orderId : '-'}</td>
                    <td>\${p.vendor ? p.vendor.companyName : '-'}</td>
                    <td class="fw-bold">Rs. \${p.amount ? p.amount.toFixed(2) : '0.00'}</td>
                    <td>\${stat}</td>
                    <td>\${p.createdAt ? new Date(p.createdAt.replace('[UTC]', '')).toLocaleDateString() : '-'}</td>
                </tr>
            `;
        });
    }
}

async function loadReturns() {
    const res = await apiCall(CTX + '/api/customers/returns');
    if(res && res.ok) {
        const d = await res.json();
        const tbody = document.getElementById('returnsTableBody');
        tbody.innerHTML = '';
        d.data.forEach(r => {
            tbody.innerHTML += `
                <tr>
                    <td class="fw-bold">RMA-\${r.id}</td>
                    <td>\${r.shippingOrder ? r.shippingOrder.orderId : '-'}</td>
                    <td>\${r.itemName}</td>
                    <td class="text-muted">\${r.reason || '-'}</td>
                    <td>\${r.returnedAt ? new Date(r.returnedAt.replace('[UTC]', '')).toLocaleDateString() : '-'}</td>
                </tr>
            `;
        });
    }
}

async function openOrderModal() {
    if(!orderModal) orderModal = new bootstrap.Modal(document.getElementById('orderModal'));
    orderModal.show();
}

async function submitOrder() {
    const payload = {
        customerFullName: document.getElementById('ordName').value,
        orderDescription: document.getElementById('ordDesc').value,
        itemCount: parseInt(document.getElementById('ordCount').value),
        productDesignDocUrl: document.getElementById('ordDesignDoc').value || null,
        qualityStandardsDocUrl: document.getElementById('ordQualityDoc').value || null,
        expectedTimeline: (document.getElementById('ordTimelineStart').value + ' to ' + document.getElementById('ordTimelineEnd').value),
        mobile: document.getElementById('ordMobile').value,
        addressLine1: document.getElementById('ordLine1').value,
        city: document.getElementById('ordCity').value,
        postalCode: document.getElementById('ordPostal').value,
        country: document.getElementById('ordCountry').value
    };
    
    const res = await apiCall(CTX + '/api/customers/orders', 'POST', payload);
    if(res && res.ok) {
        orderModal.hide();
        loadOrders();
        loadPayments();
    }
}

async function cancelOrder(id) {
    if(confirm("Cancel this requisition?")) {
        await apiCall(CTX + '/api/customers/orders/' + id + '/cancel', 'PUT');
        loadOrders();
    }
}

async function deleteOrder(id) {
    if(confirm("Delete this cancelled requisition?")) {
        await apiCall(CTX + '/api/customers/orders/' + id, 'DELETE');
        loadOrders();
    }
}

function openReturnModal(orderId) {
    document.getElementById('retOrderId').value = orderId;
    if(!returnModal) returnModal = new bootstrap.Modal(document.getElementById('returnModal'));
    returnModal.show();
}

async function submitReturn() {
    const oid = document.getElementById('retOrderId').value;
    const payload = {
        itemName: document.getElementById('retItem').value,
        reason: document.getElementById('retReason').value
    };
    const res = await apiCall(CTX + '/api/customers/returns?orderId=' + oid, 'POST', payload);
    if(res && res.ok) {
        returnModal.hide();
        loadReturns();
        loadOrders(); 
    }
}

document.addEventListener('DOMContentLoaded', loadPortal);
</script>
<!-- Toast Container -->
<div class="toast-container position-fixed bottom-0 end-0 p-3" style="z-index: 9999;">
  <div id="custToast" class="toast align-items-center text-white bg-primary border-0" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body" id="custToastBody">
        Notification
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  </div>
</div>
</body>
</html>








