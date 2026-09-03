<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Supplier Portal - GlobalTrade Logistics</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <!-- Google Fonts for Premium Typography -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --gt-dark: #0f172a;
            --gt-primary: #d97706; /* Elegant amber/gold */
            --gt-card-bg: rgba(255, 255, 255, 0.98);
        }
        body { background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%); color: #f8fafc; font-family: 'Inter', sans-serif; min-height: 100vh; }
        .sidebar { width: 260px; background: rgba(15, 23, 42, 0.9); backdrop-filter: blur(12px); min-height: 100vh; position: fixed; top: 0; left: 0; border-right: 1px solid rgba(255,255,255,0.05); }
        .sidebar .brand { padding: 1.5rem 1rem; border-bottom: 1px solid rgba(255,255,255,0.05); }
        .sidebar .brand-title { color: #f8fafc; font-size: 1rem; font-weight: 700; letter-spacing: 0.5px; }
        .sidebar .nav-link { color: #94a3b8; padding: 0.65rem 1.25rem; border-radius: 8px; margin: 0.15rem 0.5rem; cursor: pointer; transition: all 0.3s ease; }
        .sidebar .nav-link:hover { color: #f8fafc; background: rgba(255,255,255,0.02); }
        .sidebar .nav-link.active { background: rgba(217, 119, 6, 0.15); color: var(--gt-primary); border-right: 3px solid var(--gt-primary); }
        .main-content { margin-left: 260px; padding: 1.5rem 2rem; }
        .kpi-card { background: var(--gt-card-bg); border-radius: 16px; padding: 1.5rem; border: none; box-shadow: 0 10px 20px rgba(0,0,0,0.2); margin-bottom: 1rem; color: #334155; }
        .kanban-board { display: flex; gap: 1rem; overflow-x: auto; padding-bottom: 1rem; }
        .kanban-col { background: rgba(255,255,255,0.05); border-radius: 12px; min-width: 320px; padding: 1rem; display: flex; flex-direction: column; gap: 1rem; }
        .po-card { background: var(--gt-card-bg); border-radius: 12px; padding: 1.2rem; box-shadow: 0 4px 12px rgba(0,0,0,0.1); border-left: 4px solid var(--gt-primary); color: #334155; }
        .po-card.delay { border-left-color: #dc3545; }
        .po-card.ready { border-left-color: #10b981; }
        .content-section { display: none; }
        .content-section.active { display: block; }
        .table-card { background: var(--gt-card-bg); border-radius: 16px; padding: 2.5rem; box-shadow: 0 20px 40px rgba(0,0,0,0.3); color: #334155; }
        .table-light th { background: #f8fafc; color: #64748b; font-weight: 600; text-transform: uppercase; font-size: 0.75rem; letter-spacing: 0.5px; border-bottom: 2px solid #e2e8f0; padding: 1rem; }
        .table td { border-bottom: 1px solid #f1f5f9; vertical-align: middle; padding: 1rem; color: #334155; }
        h4, h6 { color: #f8fafc; }
        .po-card h6, .po-card p, .kpi-card h6, .kpi-card h3, .table-card h4, .table-card h6, .bg-light h6 { color: #334155 !important; }
        .text-dark { color: #0f172a !important; }
        .topbar { background: var(--gt-card-bg); border-radius: 12px; padding: 0.8rem 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 10px 20px rgba(0,0,0,0.2); display: flex; justify-content: space-between; align-items: center; color: #334155; }
    </style>
</head>
<body>

<nav class="sidebar">
    <div class="brand d-flex align-items-center gap-2">
        <i class="bi bi-shop text-warning fs-4"></i>
        <div class="brand-title">Supplier Portal</div>
    </div>
    <ul class="nav flex-column mt-2" id="sidebarNav">
        <li class="nav-item">
            <a class="nav-link active" data-target="section-po"><i class="bi bi-kanban me-2"></i>Purchase Orders</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-target="section-profile"><i class="bi bi-person-badge me-2"></i>My Profile</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-target="section-shipping"><i class="bi bi-box-seam me-2"></i>Shipping Orders</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-target="section-returns"><i class="bi bi-arrow-return-left me-2"></i>Returned Items</a>
        </li>
        <li class="nav-item mt-4">
            <a class="nav-link text-warning" href="${pageContext.request.contextPath}/api/vendor-portal/reports/scorecard" target="_blank"><i class="bi bi-file-earmark-pdf me-2"></i>Download Scorecard</a>
        </li>
        <li class="nav-item">
            <a class="nav-link text-success" href="${pageContext.request.contextPath}/api/vendor-portal/reports/settlements/pdf" target="_blank"><i class="bi bi-receipt me-2"></i>Payment Statements</a>
        </li>
        <li class="nav-item">
            <a class="nav-link text-info" href="#" data-bs-toggle="modal" data-bs-target="#complianceModal"><i class="bi bi-shield-check me-2"></i>Compliance Upload</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="#" onclick="doLogout()"><i class="bi bi-box-arrow-left me-2 text-danger"></i>Sign Out</a>
        </li>
    </ul>
</nav>

<main class="main-content">
    <div class="topbar">
        <h4 class="mb-0 fw-bold" id="pageTitle" style="color: #0f172a !important;">Purchase Orders</h4>
        <span class="badge bg-secondary fs-6" id="userNameDisplay">Vendor Rep</span>
    </div>

    <!-- Purchase Orders Section -->
    <div id="section-po" class="content-section active">
        <div class="row mb-4">
            <div class="col-md-6">
                <div class="kpi-card d-flex align-items-center">
                    <div class="bg-primary bg-opacity-10 text-primary rounded-circle p-3 me-3">
                        <i class="bi bi-star-fill fs-3"></i>
                    </div>
                    <div>
                        <h6 class="text-muted mb-1">Performance Score (Automated)</h6>
                        <h3 class="mb-0 fw-bold" id="kpiScore">--/100</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="kpi-card d-flex align-items-center">
                    <div class="bg-danger bg-opacity-10 text-danger rounded-circle p-3 me-3">
                        <i class="bi bi-exclamation-triangle-fill fs-3"></i>
                    </div>
                    <div>
                        <h6 class="text-muted mb-1">Defect Rate</h6>
                        <h3 class="mb-0 fw-bold" id="defectRate">--%</h3>
                    </div>
                </div>
            </div>
        </div>

                <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-0 py-3">
                <h5 class="mb-0 fw-bold">Assigned Orders</h5>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Order ID</th>
                                <th>Customer Name</th>
                                <th>Item Count</th>
                                <th>Assigned By (Ops)</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="assignedOrdersTbody">
                            <!-- Populated by JS -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="kanban-board">
            
            <div class="kanban-col">
                <h6 class="fw-bold mb-3"><i class="bi bi-gear me-2"></i>In Production</h6>
                <div id="col-production"></div>
            </div>
            <div class="kanban-col">
                <h6 class="fw-bold mb-3"><i class="bi bi-truck me-2"></i>Ready / Shipped</h6>
                <div id="col-ready"></div>
            </div>
        </div>
    </div>

    <!-- Profile Section -->
    <div id="section-profile" class="content-section">
        <div class="card shadow-sm border-0">
            <div class="card-body">
                <h5 class="card-title mb-4">Company Profile & Pickup Details</h5>
                <form id="profileForm" onsubmit="saveProfile(event)">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label>Company Name</label>
                            <input type="text" id="profCompany" class="form-control" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label>Contact Name</label>
                            <input type="text" id="profContact" class="form-control" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label>Email</label>
                            <input type="email" id="profEmail" class="form-control" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label>Phone</label>
                            <input type="text" id="profPhone" class="form-control" required>
                        </div>
                        
                        <div class="col-12 mt-3 mb-3"><hr><h6>Pickup Address</h6></div>
                        
                        <div class="col-md-6 mb-3">
                            <label>Address Line 1</label>
                            <input type="text" id="profAddr1" class="form-control">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label>Address Line 2</label>
                            <input type="text" id="profAddr2" class="form-control">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label>City</label>
                            <input type="text" id="profCity" class="form-control">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label>State / Province</label>
                            <input type="text" id="profState" class="form-control">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label>Postal Code</label>
                            <input type="text" id="profZip" class="form-control">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label>Country</label>
                            <input type="text" id="profCountry" class="form-control">
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary mt-3">Save Profile</button>
                </form>
            </div>
        </div>
    </div>

    <!-- Shipping Orders Section -->
    <div id="section-shipping" class="content-section">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
                <h5 class="mb-0">My Shipping Orders</h5>
                <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#createShippingOrderModal">Create Order</button>
            </div>
            <div class="card-body">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Order ID</th>
                            <th>Date</th>
                            <th>Customer</th>
                            <th>Route</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="shippingOrdersTableBody"></tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Returns Section -->
    <div id="section-returns" class="content-section">
        <div class="card shadow-sm border-0">
            <div class="card-body">
                <h5 class="mb-4">Returned Items</h5>
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Order ID</th>
                            <th>Item Name</th>
                            <th>Reason</th>
                            <th>Returned Date</th>
                        </tr>
                    </thead>
                    <tbody id="returnsTableBody"></tbody>
                </table>
            </div>
        </div>
    </div>

</main>

<!-- Create Shipping Order Modal -->
<div class="modal fade" id="createShippingOrderModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Create Shipping Order</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="shippingOrderForm" onsubmit="createShippingOrder(event)">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Customer Full Name *</label>
                            <input type="text" class="form-control" id="soCustomerName" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Mobile *</label>
                            <input type="text" class="form-control" id="soMobile" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Delivery Address Line 1 *</label>
                            <input type="text" class="form-control" id="soAddr1" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Delivery Address Line 2</label>
                            <input type="text" class="form-control" id="soAddr2">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label">City *</label>
                            <input type="text" class="form-control" id="soCity" required>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label">State *</label>
                            <input type="text" class="form-control" id="soState" required>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label">Postal Code *</label>
                            <input type="text" class="form-control" id="soZip" required>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label">Country *</label>
                            <input type="text" class="form-control" id="soCountry" required>
                        </div>
                        <div class="col-md-12 mb-3">
                            <label class="form-label">Order Description</label>
                            <textarea class="form-control" id="soDesc" rows="2"></textarea>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label">Weight (kg)</label>
                            <input type="number" step="0.1" class="form-control" id="soWeight">
                        </div>

                    </div>
                    <div class="modal-footer px-0 pb-0">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Submit Order</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- View Shipping Order Details Modal -->
<div class="modal fade" id="viewShippingOrderModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Order Details <span id="voId" class="badge bg-primary ms-2"></span></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p><strong>Customer:</strong> <span id="voCustomer"></span></p>
                <p><strong>Mobile:</strong> <span id="voMobile"></span></p>
                <p><strong>Delivery Address:</strong><br><span id="voAddress"></span></p>
                <p><strong>Description:</strong> <span id="voDesc"></span></p>
                <p><strong>Weight:</strong> <span id="voWeight"></span> kg</p>
                <p><strong>Route:</strong> <span id="voRoute"></span></p>
                <p><strong>Status:</strong> <span id="voStatus"></span></p>
            </div>
        </div>
    </div>
</div>

<!-- (Keeping Existing Modals for ASN, Propose Date, Compliance) -->
<!-- ASN Modal -->
<div class="modal fade" id="asnModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Generate Advance Shipping Notice</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="asnPoId">
                <div class="row">
                    <div class="col-md-4 mb-3"><label>Dimensions</label><input type="text" id="asnDims" class="form-control" value="120x100x150cm"></div>
                    <div class="col-md-4 mb-3"><label>Weight (kg)</label><input type="number" id="asnWeight" class="form-control" value="500"></div>
                    <div class="col-md-4 mb-3"><label>Pallet Count</label><input type="number" id="asnPallets" class="form-control" value="2"></div>
                </div>
                <hr>
                <h6>Consignee / Receiver Info</h6>
                <div class="mb-2"><input type="text" id="asnReceiverName" class="form-control" placeholder="Receiver Name"></div>
                <div class="mb-2"><input type="text" id="asnReceiverEmail" class="form-control" placeholder="Email"></div>
                <div class="mb-2"><input type="text" id="asnReceiverMobile" class="form-control" placeholder="Mobile"></div>
                <div class="mb-2"><textarea id="asnReceiverAddress" class="form-control" placeholder="Full Delivery Address"></textarea></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-success" onclick="submitASN()">Submit & Transmit</button>
            </div>
        </div>
    </div>
</div>

<!-- Propose Date Modal -->
<div class="modal fade" id="proposeDateModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Propose New Delivery Date</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="proposePoId">
                <label>New Proposed Date</label>
                <input type="date" id="newPoDate" class="form-control">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="submitProposedDate()">Propose Date</button>
            </div>
        </div>
    </div>
</div>

<!-- Compliance Modal -->
<div class="modal fade" id="complianceModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Upload Compliance Document</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="uploadPoId">
                <div class="mb-3">
                    <label class="form-label">Document Type</label>
                    <select class="form-select" id="compType">
                        <option>Certificate of Origin</option>
                        <option>Commercial Invoice</option>
                        <option>Packing List</option>
                    </select>
                </div>
                <div class="mb-3">
                    <label class="form-label">File</label>
                    <input type="file" class="form-control" id="compFile">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="uploadCompliance()">Upload</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
const CTX = '${pageContext.request.contextPath}';
let asnModalInstance, proposeDateModalInstance, complianceModalInstance;
let createShippingOrderModalInstance, viewShippingOrderModalInstance;
let firstVendorLoad = true;
let knownVendorOrderIds = new Set();
let vendorPollingInterval = null;

async function apiCall(url, method='GET', bodyData=null) {
    let options = { method, headers: { 'Content-Type': 'application/json' } };
    if(bodyData) options.body = JSON.stringify(bodyData);
    const res = await fetch(url, options);
    if(res.status === 401) { window.location.href = CTX + '/login.jsp'; return null; }
    return res;
}

async function doLogout() {
    await apiCall(CTX+'/api/auth/logout', 'POST');
    window.location.href = CTX+'/login.jsp';
}

// --- Navigation Logic ---
document.querySelectorAll('#sidebarNav .nav-link').forEach(link => {
    link.addEventListener('click', function(e) {
        if(this.getAttribute('data-target')) {
            e.preventDefault();
            document.querySelectorAll('#sidebarNav .nav-link').forEach(l => l.classList.remove('active'));
            this.classList.add('active');
            
            document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
            document.getElementById(this.getAttribute('data-target')).classList.add('active');
            
            document.getElementById('pageTitle').innerText = this.innerText;
        }
    });
});

// --- API Logic ---
async function loadPortal() {
    // 1. User & KPI Info
    const meRes = await apiCall(CTX + '/api/users/me');
    if (meRes && meRes.ok) {
        const d = await meRes.json();
        document.getElementById('userNameDisplay').innerText = d.data.fullName;
    }

    // 2. Load Profile
    const profRes = await apiCall(CTX + '/api/vendor-portal/profile');
    if (profRes && profRes.ok) {
        const p = (await profRes.json()).data;
        document.getElementById('kpiScore').innerText = (p.performanceScore||0) + "/100";
        document.getElementById('defectRate').innerText = (p.defectRate||0) + "%";
        
        document.getElementById('profCompany').value = p.companyName || '';
        document.getElementById('profContact').value = p.contactName || '';
        document.getElementById('profEmail').value = p.email || '';
        document.getElementById('profPhone').value = p.phone || '';
        document.getElementById('profAddr1').value = p.pickupAddressLine1 || '';
        document.getElementById('profAddr2').value = p.pickupAddressLine2 || '';
        document.getElementById('profCity').value = p.pickupCity || '';
        document.getElementById('profState').value = p.pickupState || '';
        document.getElementById('profZip').value = p.pickupPostalCode || '';
        document.getElementById('profCountry').value = p.pickupCountry || '';
    }

    const soRes = await apiCall(CTX + '/api/vendor-portal/shipping-orders');
    if (soRes && soRes.ok) {
        const d = await soRes.json();
        currentShippingOrders = d.data;
        
        let currentIds = new Set();
        d.data.forEach(o => {
            currentIds.add(o.id);
            if (!firstVendorLoad && !knownVendorOrderIds.has(o.id)) {
                showVendorToast("New Requisition Assigned: " + o.orderId);
            }
        });
        knownVendorOrderIds = currentIds;
        firstVendorLoad = false;
        
        renderKanban(d.data);
        renderAssignedOrdersTable();
        renderShippingOrders(d.data);
    }

    // 5. Load Returns
    const retRes = await apiCall(CTX + '/api/vendor-portal/returns');
    if (retRes && retRes.ok) {
        const d = await retRes.json();
        renderReturns(d.data);
    }
    if (!vendorPollingInterval) {
        vendorPollingInterval = setInterval(() => {
            loadPortal();
        }, 3000);
    }
}

async function saveProfile(e) {
    e.preventDefault();
    const data = {
        companyName: document.getElementById('profCompany').value,
        contactName: document.getElementById('profContact').value,
        email: document.getElementById('profEmail').value,
        phone: document.getElementById('profPhone').value,
        pickupAddressLine1: document.getElementById('profAddr1').value,
        pickupAddressLine2: document.getElementById('profAddr2').value,
        pickupCity: document.getElementById('profCity').value,
        pickupState: document.getElementById('profState').value,
        pickupPostalCode: document.getElementById('profZip').value,
        pickupCountry: document.getElementById('profCountry').value
    };
    const res = await apiCall(CTX + '/api/vendor-portal/profile', 'PUT', data);
    if(res && res.ok) alert("Profile updated successfully!");
}

async function createShippingOrder(e) {
    e.preventDefault();
    const data = {
        customerFullName: document.getElementById('soCustomerName').value,
        mobile: document.getElementById('soMobile').value,
        addressLine1: document.getElementById('soAddr1').value,
        addressLine2: document.getElementById('soAddr2').value,
        city: document.getElementById('soCity').value,
        state: document.getElementById('soState').value,
        postalCode: document.getElementById('soZip').value,
        country: document.getElementById('soCountry').value,
        orderDescription: document.getElementById('soDesc').value,
        weight: document.getElementById('soWeight').value ? parseFloat(document.getElementById('soWeight').value) : null
    };
    
    const res = await apiCall(CTX + '/api/vendor-portal/shipping-orders', 'POST', data);
    if (res && res.ok) {
        if(!createShippingOrderModalInstance) createShippingOrderModalInstance = bootstrap.Modal.getInstance(document.getElementById('createShippingOrderModal'));
        if(createShippingOrderModalInstance) createShippingOrderModalInstance.hide();
        document.getElementById('shippingOrderForm').reset();
        loadPortal(); // Refresh tables
        alert("Shipping Order created successfully!");
    }
}

// Keep a global reference for the modal view
let currentShippingOrders = [];

function renderShippingOrders(orders) {
    currentShippingOrders = orders;
    const tbody = document.getElementById('shippingOrdersTableBody');
    tbody.innerHTML = '';
    orders.forEach(o => {
        const route = (o.routeFrom && o.routeTo) ? (o.routeFrom + ' &rarr; ' + o.routeTo) : 'TBD';
        const date = o.createdAt ? new Date(o.createdAt).toLocaleDateString() : '';
        tbody.innerHTML += `
            <tr>
                <td><strong>\\${o.orderId}</strong></td>
                <td>\${date}</td>
                <td>\\${o.customerFullName}</td>
                <td>\${route}</td>
                <td><span class="badge bg-secondary">\${o.status}</span></td>
                <td><button class="btn btn-sm btn-outline-primary" onclick="viewShippingOrder('\\${o.orderId}')">Details</button></td>
            </tr>
        `;
    });
}

function viewShippingOrder(orderId) {
    const o = currentShippingOrders.find(x => x.orderId === orderId);
    if(!o) return;
    document.getElementById('voId').innerText = o.orderId;
    document.getElementById('voCustomer').innerText = o.customerFullName;
    document.getElementById('voMobile').innerText = o.mobile || 'N/A';
    document.getElementById('voAddress').innerHTML = `\${o.addressLine1}<br>\${o.addressLine2 ? o.addressLine2+'<br>' : ''}\${o.city}, \${o.state} \${o.postalCode}<br>\${o.country}`;
    document.getElementById('voDesc').innerText = o.orderDescription || 'N/A';
    document.getElementById('voWeight').innerText = o.weight || '0';
    document.getElementById('voRoute').innerText = (o.routeFrom && o.routeTo) ? (o.routeFrom + ' -> ' + o.routeTo) : 'TBD';
    document.getElementById('voStatus').innerText = o.status;
    
    if(!viewShippingOrderModalInstance) viewShippingOrderModalInstance = new bootstrap.Modal(document.getElementById('viewShippingOrderModal'));
    viewShippingOrderModalInstance.show();
}

function renderReturns(returns) {
    const tbody = document.getElementById('returnsTableBody');
    tbody.innerHTML = '';
    returns.forEach(r => {
        const date = r.returnedAt ? new Date(r.returnedAt).toLocaleDateString() : '';
        const oid = r.shippingOrder ? r.shippingOrder.orderId : 'Unknown';
        tbody.innerHTML += `
            <tr>
                <td><strong>\${oid}</strong></td>
                <td>\${r.itemName}</td>
                <td>\${r.reason}</td>
                <td>\${date}</td>
            </tr>
        `;
    });
}

function renderAssignedOrdersTable() {
    const tbody = document.getElementById('assignedOrdersTbody');
    if(!tbody) return;
    tbody.innerHTML = '';
    
    // currentShippingOrders has the assigned shipping orders
    const assigned = currentShippingOrders.filter(o => o.vendorDecision === 'PENDING');
    if (assigned.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No pending assigned orders</td></tr>';
        return;
    }

    assigned.forEach(o => {
        const opsName = o.opsAssigneeName || 'Ops Team';
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>\${o.orderId}</strong></td>
            <td>\${o.customerFullName}</td>
            <td>\${o.itemCount || 0}</td>
            <td><span class="badge bg-secondary">\${opsName}</span></td>
            <td><span class="badge bg-warning text-dark">PENDING</span></td>
            <td>
                <button class="btn btn-sm btn-success me-1" onclick="submitDecision('\${o.orderId}', 'ACCEPTED')">Confirm</button>
                <button class="btn btn-sm btn-danger me-1" onclick="promptReject('\${o.orderId}')">Cancel</button>
                <button class="btn btn-sm btn-outline-primary" onclick="viewOrderDetails('\${o.orderId}')">View Details</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function viewOrderDetails(orderId) {
    const o = currentShippingOrders.find(x => x.orderId === orderId);
    if(!o) return;
    document.getElementById('voId').innerText = o.orderId;
    document.getElementById('voCustomerName').innerText = o.customerFullName || 'N/A';
    document.getElementById('voItemCount').innerText = o.itemCount || '0';
    document.getElementById('voDesc').innerText = o.orderDescription || 'N/A';
    document.getElementById('voTimeline').innerText = o.expectedTimeline || 'N/A';
    document.getElementById('voWeight').innerText = o.weight ? o.weight + ' kg' : 'N/A';
    document.getElementById('voRouteFrom').innerText = o.routeFrom || 'N/A';
    document.getElementById('voRouteTo').innerText = o.routeTo || 'N/A';
    document.getElementById('voOpsName').innerText = o.opsAssigneeName || 'Ops Team';
    document.getElementById('voStatus').innerText = o.status || 'N/A';
    
    new bootstrap.Modal(document.getElementById('orderDetailsModal')).show();
}

async function submitDecision(orderId, decision, reason = null, proposedDate = null) {
    const o = currentShippingOrders.find(x => x.orderId === orderId);
    if(!o) return;
    const req = { decision: decision, reason: reason, proposedDate: proposedDate };
    const res = await apiCall(CTX + '/api/vendor-portal/orders/' + o.id + '/decision', 'POST', req);
    if(res && res.ok) {
        showVendorToast("Order " + decision.toLowerCase() + " successfully.");
        loadPortal(); // Reload tables
    }
}

function promptReject(orderId) {
    const reason = prompt("Please provide a reason for cancelling/rejecting this order:");
    if(reason) {
        submitDecision(orderId, 'REJECTED', reason);
    }
}

function renderKanban(pos) {
    
    const colProduction = document.getElementById('col-production');
    const colReady = document.getElementById('col-ready');
     
    colProduction.innerHTML = ''; 
    colReady.innerHTML = '';
    
    pos.forEach(po => {
        if (po.vendorDecision === 'PENDING' || po.vendorDecision === 'PROPOSED_DATE' || po.vendorDecision === 'REJECTED') {
            return; // Handled elsewhere or not active
        }

        let actions = '';
        let targetCol = null;
        let cardClass = 'po-card';
        
        if (po.status === 'IN_PROGRESS') {
            targetCol = 'col-production';
            actions = `<button class="btn btn-sm btn-primary w-100" onclick="updatePOStatus('\${po.id}', 'IN_WAREHOUSE')">Ready for Pickup</button>`;
        } else if (po.status === 'IN_WAREHOUSE') {
            targetCol = 'col-ready';
            cardClass += ' ready';
            actions = `<span class="badge bg-success w-100 p-2">Waiting for Carrier</span>`;
        } else if (po.status === 'SHIPPED' || po.status === 'RECEIVED_SHIPMENT' || po.status === 'ON_DELIVERY' || po.status === 'DELIVERED') {
            targetCol = 'col-ready';
            cardClass += ' ready';
            actions = `<span class="badge bg-secondary w-100 p-2">\${po.status}</span>`;
        }

        if (targetCol) {
            document.getElementById(targetCol).innerHTML += `
                <div class="${cardClass} mb-3">
                    <div class="d-flex justify-content-between">
                        <strong>\${po.orderId}</strong>
                        <span class="badge bg-light text-dark small">\${po.status}</span>
                    </div>
                    <div class="small text-muted mt-2 mb-3">
                        \${po.customerFullName}<br>
                        Items: \${po.itemCount || 0}
                    </div>
                    ${actions}
                </div>
            `;
        }
    });
}

async function updatePOStatus(id, newStatus) {
    const res = await apiCall(CTX + '/api/vendor-portal/orders/' + id + '/status', 'PUT', { status: newStatus });
    if(res && res.ok) {
        showVendorToast("Order status updated.");
        loadPortal();
    }
}
async function updatePO(id, action) {
    const res = await apiCall(CTX + '/api/vendor-portal/orders/' + id + '/' + action, 'PUT');
    if(res && res.ok) loadPortal();
}
function openComplianceModal() {
    if(!complianceModalInstance) complianceModalInstance = new bootstrap.Modal(document.getElementById('complianceModal'));
    complianceModalInstance.show();
}
async function uploadCompliance() {
    const type = document.getElementById('compType').value;
    const fileInput = document.getElementById('compFile');
    if(!fileInput.files[0]) return alert("Select a file");
    const fileName = fileInput.files[0].name;
    const res = await apiCall(CTX + '/api/vendor-portal/compliance/upload?type=' + encodeURIComponent(type) + '&fileName=' + encodeURIComponent(fileName), 'POST');
    if (res && res.ok) {
        complianceModalInstance.hide();
        alert("Compliance document uploaded.");
    }
}

document.addEventListener('DOMContentLoaded', loadPortal);
</script>
<!-- Toast Container -->
<div class="toast-container position-fixed bottom-0 end-0 p-3" style="z-index: 9999;">
  <div id="vendorToast" class="toast align-items-center text-white bg-primary border-0" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body" id="vendorToastBody">
        Notification
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  </div>
</div>
<!-- View Order Details Modal -->
<div class="modal fade" id="orderDetailsModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 shadow">
            <div class="modal-header border-0 pb-0">
                <h5 class="modal-title fw-bold">Order Details - <span id="voId"></span></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="row mb-3">
                    <div class="col-md-6"><small class="text-muted">Customer Name</small><br><strong id="voCustomerName"></strong></div>
                    <div class="col-md-6"><small class="text-muted">Item Count</small><br><strong id="voItemCount"></strong></div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-12"><small class="text-muted">Order Description</small><br><span id="voDesc"></span></div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6"><small class="text-muted">Expected Timeline</small><br><span id="voTimeline"></span></div>
                    <div class="col-md-6"><small class="text-muted">Weight (kg)</small><br><span id="voWeight"></span></div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6"><small class="text-muted">Route From</small><br><span id="voRouteFrom"></span></div>
                    <div class="col-md-6"><small class="text-muted">Route To</small><br><span id="voRouteTo"></span></div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6"><small class="text-muted">Assigned By (Ops)</small><br><span id="voOpsName"></span></div>
                    <div class="col-md-6"><small class="text-muted">Status</small><br><span class="badge bg-primary" id="voStatus"></span></div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

















