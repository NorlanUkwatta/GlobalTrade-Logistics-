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

        <li class="nav-item">
            <button class="nav-link" id="locations-tab" data-bs-toggle="tab" data-bs-target="#locations-pane" type="button"><i class="bi bi-geo-alt me-2"></i>Add countries and regions</button>
        </li>
        <li class="nav-item">
            <button class="nav-link" id="warehouses-tab" data-bs-toggle="tab" data-bs-target="#warehouses-pane" type="button"><i class="bi bi-building me-2"></i>Add warehouses</button>
        </li>
        <li class="nav-item">
                    <li class="nav-item">
            <button class="nav-link" id="subscriptions-tab" data-bs-toggle="tab" data-bs-target="#subscriptions-pane" type="button"><i class="bi bi-credit-card me-2"></i>Platform Subscriptions</button>
        </li>
            <button class="nav-link" id="freight-calc-tab" data-bs-toggle="tab" data-bs-target="#freight-calc-pane" type="button"><i class="bi bi-calculator me-2"></i>Freight Calculator</button>
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
            <div class="row mb-4">
                <div class="col-md-8">
                    <div class="table-card bg-white h-100">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h3 class="section-header">Carrier Companies</h3>
                                <p class="section-sub mb-0">Manage registered shipping partners.</p>
                            </div>
                        </div>
                        <table class="table table-hover align-middle">
                            <thead class="table-light"><tr><th>Company ID</th><th>Name</th><th>Mother Company Address</th><th class="text-end">Actions</th></tr></thead>
                            <tbody id="carriersBody"></tbody>
                        </table>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="table-card bg-white h-100">
                        <h3 class="section-header" id="carrierFormTitle">Add New Carrier</h3>
                        <input type="hidden" id="editCarrierId">
                        <div class="mb-3">
                            <label class="form-label">Company Name</label>
                            <input type="text" id="newCarrierName" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Mother Company Address</label>
                            <textarea id="newCarrierAddress" class="form-control" rows="2"></textarea>
                        </div>
                        <div class="d-flex gap-2"><button class="btn btn-primary w-100" id="saveCarrierBtn" onclick="addCarrier()">Register</button><button class="btn btn-secondary w-100 d-none" id="cancelEditCarrierBtn" onclick="cancelEditCarrier()">Cancel</button></div>
                    </div>
                </div>
            </div>
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

        <!-- LOCATIONS PANE -->
        <div class="tab-pane fade" id="locations-pane" role="tabpanel">
            <div class="row">
                <div class="col-md-6">
                    <div class="table-card bg-white h-100">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h3 class="section-header">Countries</h3>
                                <p class="section-sub mb-0">Manage operating countries.</p>
                            </div>
                            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCountryModal"><i class="bi bi-plus-circle me-1"></i>New Country</button>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light"><tr><th>ID</th><th>Code</th><th>Name</th></tr></thead>
                                <tbody id="countriesTbody"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="table-card bg-white h-100">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h3 class="section-header">Regions</h3>
                                <p class="section-sub mb-0">Manage global regions (e.g., Asia Pacific).</p>
                            </div>
                            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addRegionModal"><i class="bi bi-plus-circle me-1"></i>New Region</button>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light"><tr><th>ID</th><th>Name</th></tr></thead>
                                <tbody id="regionsTbody"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- WAREHOUSES PANE -->
        <div class="tab-pane fade" id="warehouses-pane" role="tabpanel">
            <div class="table-card bg-white">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h3 class="section-header">Warehouses</h3>
                        <p class="section-sub mb-0">Manage warehouse facilities.</p>
                    </div>
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addWarehouseModal" onclick="prepareWarehouseForm()"><i class="bi bi-plus-circle me-1"></i>Add Warehouse</button>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Name</th>
                                <th>Address</th>
                                <th>Location</th>
                                <th>Manager</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody id="warehousesTbody">
                            <tr><td colspan="5" class="text-center text-muted">Loading...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        
        <!-- SUBSCRIPTIONS PANE -->
        <div class="tab-pane fade" id="subscriptions-pane" role="tabpanel">
            <div class="table-card bg-white">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h3 class="section-header">Platform & Operations Subscriptions</h3>
                        <p class="section-sub mb-0">Manage fixed prices for platform usage subscriptions.</p>
                    </div>
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#subscriptionModal" onclick="prepareSubscriptionForm()"><i class="bi bi-plus-circle me-1"></i>New Plan</button>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Plan Name</th>
                                <th>Price (USD)</th>
                                <th>Billing Cycle</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="subscriptionsTbody">
                            <tr><td colspan="4" class="text-center text-muted">Loading...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
            
            <div class="table-card bg-white mt-4">
                <h4 class="mb-3">Assign Subscription to Customer</h4>
                <div class="row">
                    <div class="col-md-5">
                        <label>Customer</label>
                        <select id="assignSubCustomer" class="form-select"></select>
                    </div>
                    <div class="col-md-5">
                        <label>Subscription Plan</label>
                        <select id="assignSubPlan" class="form-select"></select>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button class="btn btn-success w-100" onclick="assignSubscription()">Assign</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- FREIGHT CALCULATOR PANE -->
        <div class="tab-pane fade" id="freight-calc-pane" role="tabpanel">
            <div class="table-card bg-white">
                <h3 class="section-header"><i class="bi bi-calculator me-2"></i>Freight Cost Calculator</h3>
                <p class="section-sub">Calculate LCL (Less-than-Container) or FCL (Full Container Load) shipment costs in USD.</p>

                <!-- Mode Selector -->
                <ul class="nav nav-pills mb-4" id="freightModeTabs">
                    <li class="nav-item">
                        <button class="nav-link active" id="lcl-tab" onclick="switchFreightMode('lcl')">
                            <i class="bi bi-boxes me-1"></i> LCL - Less-than-Container Load
                        </button>
                    </li>
                    <li class="nav-item ms-2">
                        <button class="nav-link" id="fcl-tab" onclick="switchFreightMode('fcl')">
                            <i class="bi bi-box-seam me-1"></i> FCL - Full Container Load
                        </button>
                    </li>
                </ul>

                <!-- LCL FORM -->
                <div id="lcl-form">
                    <div class="row g-3 mb-4">
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Description / Reference</label>
                            <input type="text" id="lcl-ref" class="form-control" placeholder="e.g. 1 Pallet of Electronics">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Length (m)</label>
                            <input type="number" id="lcl-length" class="form-control" step="0.01" placeholder="e.g. 1.2">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Width (m)</label>
                            <input type="number" id="lcl-width" class="form-control" step="0.01" placeholder="e.g. 1.0">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Height (m)</label>
                            <input type="number" id="lcl-height" class="form-control" step="0.01" placeholder="e.g. 1.5">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Actual Weight (kg)</label>
                            <input type="number" id="lcl-weight" class="form-control" step="0.01" placeholder="e.g. 250">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Freight Rate (USD per CBM)</label>
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" id="lcl-rate-cbm" class="form-control" step="0.01" placeholder="e.g. 150">
                            </div>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Alternate Rate (USD per kg)</label>
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" id="lcl-rate-kg" class="form-control" step="0.0001" placeholder="e.g. 1.50">
                            </div>
                        </div>
                    </div>
                    <button class="btn btn-primary px-4" onclick="calculateLCL()"><i class="bi bi-calculator me-2"></i>Calculate LCL Cost</button>
                </div>

                <!-- FCL FORM -->
                <div id="fcl-form" style="display:none;">
                    <div class="row g-3 mb-4">
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Description / Reference</label>
                            <input type="text" id="fcl-ref" class="form-control" placeholder="e.g. 10,000 pairs of Jeans">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Container Type</label>
                            <select id="fcl-container" class="form-select">
                                <option value="20ft">20ft Standard Container (TEU)</option>
                                <option value="40ft">40ft Standard Container (FEU)</option>
                                <option value="40hc">40ft High Cube Container</option>
                                <option value="45hc">45ft High Cube Container</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Route (Origin --- Destination)</label>
                            <input type="text" id="fcl-route" class="form-control" placeholder="e.g. Shanghai to Los Angeles">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-bold">Base Freight Rate (USD)</label>
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" id="fcl-base" class="form-control" step="1" placeholder="e.g. 4500">
                            </div>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-bold">BAF - Bunker Adjustment (USD)</label>
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" id="fcl-baf" class="form-control" step="1" placeholder="e.g. 300">
                            </div>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-bold">Port Handling Fees (USD)</label>
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" id="fcl-port" class="form-control" step="1" placeholder="e.g. 0">
                            </div>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-bold">GlobalTrade Markup (USD)</label>
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" id="fcl-markup" class="form-control" step="1" placeholder="e.g. 500">
                            </div>
                        </div>
                    </div>
                    <button class="btn btn-primary px-4" onclick="calculateFCL()"><i class="bi bi-calculator me-2"></i>Calculate FCL Cost</button>
                </div>

                <!-- RESULT CARD -->
                <div id="freight-result" class="mt-4" style="display:none;">
                    <hr>
                    <h5 class="fw-bold mb-3"><i class="bi bi-receipt me-2"></i>Calculation Breakdown</h5>
                    <div id="freight-result-body"></div>
                </div>
            </div>
        </div>

        </div></div>  
    <!-- Subscription Modal -->
    <div class="modal fade" id="subscriptionModal" tabindex="-1">
        <div class="modal-dialog"><div class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Subscription Plan</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <input type="hidden" id="subId">
                <div class="mb-2">
                    <label>Plan Name</label>
                    <input type="text" id="subName" class="form-control" placeholder="e.g. Monthly Standard">
                </div>
                <div class="mb-2">
                    <label>Price (USD)</label>
                    <input type="number" id="subPrice" class="form-control" step="0.01">
                </div>
                <div class="mb-2">
                    <label>Billing Cycle</label>
                    <select id="subCycle" class="form-select">
                        <option value="MONTHLY">Monthly</option>
                        <option value="YEARLY">Yearly</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer"><button class="btn btn-primary" onclick="saveSubscription()" data-bs-dismiss="modal">Save</button></div>
        </div></div>
    </div>

    <!-- Add Country Modal -->
    <div class="modal fade" id="addCountryModal" tabindex="-1">
        <div class="modal-dialog"><div class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Add Country</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <input type="text" id="countryName" class="form-control mb-2" placeholder="Country Name"/>
                <input type="text" id="countryCode" class="form-control" placeholder="Country Code (e.g. US, LK)"/>
            </div>
            <div class="modal-footer"><button class="btn btn-primary" onclick="addCountry()" data-bs-dismiss="modal">Add</button></div>
        </div></div>
    </div>
    
    <!-- Add Region Modal -->
    <div class="modal fade" id="addRegionModal" tabindex="-1">
        <div class="modal-dialog"><div class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Add Region</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <input type="text" id="regionName" class="form-control" placeholder="Region Name"/>
            </div>
            <div class="modal-footer"><button class="btn btn-primary" onclick="addRegion()" data-bs-dismiss="modal">Add</button></div>
        </div></div>
    </div>
    
    <!-- Add Warehouse Modal -->
    <div class="modal fade" id="addWarehouseModal" tabindex="-1">
        <div class="modal-dialog modal-lg"><div class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Add Warehouse</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12 mb-2"><label class="form-label text-dark fw-bold">Warehouse Name</label><input type="text" id="whName" class="form-control"/></div>
                    <div class="col-md-6 mb-2"><label class="form-label text-dark fw-bold">Address Line 1</label><input type="text" id="whLine1" class="form-control"/></div>
                    <div class="col-md-6 mb-2"><label class="form-label text-dark fw-bold">Address Line 2</label><input type="text" id="whLine2" class="form-control"/></div>
                    <div class="col-md-6 mb-2"><label class="form-label text-dark fw-bold">City</label><input type="text" id="whCity" class="form-control"/></div>
                    <div class="col-md-6 mb-2"><label class="form-label text-dark fw-bold">State/Province</label><input type="text" id="whState" class="form-control"/></div>
                    <div class="col-md-6 mb-2"><label class="form-label text-dark fw-bold">Postal Code</label><input type="text" id="whPostal" class="form-control"/></div>
                    <div class="col-md-6 mb-2"><label class="form-label text-dark fw-bold">Country</label><select id="whCountry" class="form-select" ></select></div>
                    <div class="col-md-6 mb-2"><label class="form-label text-dark fw-bold">Region</label><select id="whRegion" class="form-select"></select></div>
                    <div class="col-md-6 mb-2"><label class="form-label text-dark fw-bold">Manager</label><select id="whManager" class="form-select"></select></div>
                    <div class="col-md-12 mb-2"><label class="form-label text-dark fw-bold">Status</label>
                        <select id="whActive" class="form-select"><option value="true">Active</option><option value="false">Inactive</option></select>
                    </div>
                </div>
            </div>
            <div class="modal-footer"><button class="btn btn-primary" onclick="addWarehouse()" data-bs-dismiss="modal">Add</button></div>
        </div></div>
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
    loadCategories(); loadSubscriptions(); loadCustomersForAssignment(); loadCarriers();
}
async function deleteCategory(id) {
    if(!confirm('Are you sure you want to delete this category?')) return;
    await apiCall(CTX + '/api/ops/categories/' + id, 'DELETE');
    loadCategories(); loadSubscriptions(); loadCustomersForAssignment(); loadCarriers();
}

async function loadOps() {
    await loadOrders();
    await loadShipments();
            loadCategories(); loadSubscriptions(); loadCustomersForAssignment(); loadCarriers();
    await loadCategories(); loadSubscriptions(); loadCustomersForAssignment(); loadCarriers();
    firstLoad = false;
    
    // Start real-time polling every 3 seconds
    if (!opsPollingInterval) {
        opsPollingInterval = setInterval(() => {
            loadOrders();
            loadShipments();
            loadCategories(); loadSubscriptions(); loadCustomersForAssignment(); loadCarriers();
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
            loadCategories(); loadSubscriptions(); loadCustomersForAssignment(); loadCarriers();
}

document.addEventListener('DOMContentLoaded', loadOps);


// ==================== FREIGHT CALCULATOR ====================
function switchFreightMode(mode) {
    document.getElementById('lcl-form').style.display = mode === 'lcl' ? '' : 'none';
    document.getElementById('fcl-form').style.display = mode === 'fcl' ? '' : 'none';
    document.getElementById('lcl-tab').classList.toggle('active', mode === 'lcl');
    document.getElementById('fcl-tab').classList.toggle('active', mode === 'fcl');
    document.getElementById('freight-result').style.display = 'none';
}
function usd(n) { return 'USD ' + Number(n).toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2}); }
function calcRow(label, value, hi) { return '<tr class="' + (hi ? 'table-success fw-bold' : '') + '"><td>' + label + '</td><td class="text-end">' + value + '</td></tr>'; }

function calculateLCL() {
    var L = parseFloat(document.getElementById('lcl-length').value) || 0;
    var W = parseFloat(document.getElementById('lcl-width').value) || 0;
    var H = parseFloat(document.getElementById('lcl-height').value) || 0;
    var kg = parseFloat(document.getElementById('lcl-weight').value) || 0;
    var rCBM = parseFloat(document.getElementById('lcl-rate-cbm').value) || 0;
    var rKg = parseFloat(document.getElementById('lcl-rate-kg').value) || 0;
    var ref = document.getElementById('lcl-ref').value || 'LCL Shipment';
    if (!L || !W || !H || !kg || !rCBM) { alert('Please fill in all required fields.'); return; }
    var vol = L * W * H;
    var volKg = vol * 1000;
    var charKg = Math.max(kg, volKg);
    var charCBM = charKg / 1000;
    var useVol = volKg > kg;
    var price = charCBM * rCBM;
    var altPrice = rKg > 0 ? (kg * rKg) : null;
    var html = '<div class="table-responsive"><table class="table table-bordered align-middle"><thead class="table-light"><tr><th>Calculation Step</th><th class="text-end">Value</th></tr></thead><tbody>' +
        calcRow('Reference', ref, false) +
        calcRow('Dimensions (L x W x H)', L + 'm x ' + W + 'm x ' + H + 'm', false) +
        calcRow('Actual Weight', kg.toLocaleString() + ' kg', false) +
        calcRow('Step 1 - Actual Volume = ' + L + ' x ' + W + ' x ' + H, vol.toFixed(4) + ' CBM', false) +
        calcRow('Step 2 - Volumetric Weight (1 CBM = 1,000 kg) = ' + vol.toFixed(4) + ' x 1000', volKg.toLocaleString() + ' kg', false) +
        calcRow('Step 3 - Chargeable Weight (' + (useVol ? 'Volumetric ' + volKg.toLocaleString() + ' kg > Actual ' + kg + ' kg' : 'Actual ' + kg + ' kg > Volumetric ' + volKg.toLocaleString() + ' kg') + ')', charKg.toLocaleString() + ' kg = ' + charCBM.toFixed(4) + ' CBM', true) +
        calcRow('Freight Rate', usd(rCBM) + ' per CBM', false) +
        calcRow('Step 4 - Final Price = ' + charCBM.toFixed(4) + ' CBM x ' + usd(rCBM), usd(price), true);
    if (altPrice !== null) html += calcRow('Alternative (per kg): ' + kg + ' kg x ' + usd(rKg), usd(altPrice), false);
    html += '</tbody></table></div><div class="alert alert-success mt-3 fs-5"><strong>Final Chargeable Amount: </strong><span class="fw-bold ms-2">' + usd(price) + '</span></div>';
    document.getElementById('freight-result-body').innerHTML = html;
    document.getElementById('freight-result').style.display = '';
}

function calculateFCL() {
    var ref = document.getElementById('fcl-ref').value || 'FCL Shipment';
    var contSel = document.getElementById('fcl-container');
    var cont = contSel.options[contSel.selectedIndex].text;
    var route = document.getElementById('fcl-route').value || 'N/A';
    var base = parseFloat(document.getElementById('fcl-base').value) || 0;
    var baf = parseFloat(document.getElementById('fcl-baf').value) || 0;
    var port = parseFloat(document.getElementById('fcl-port').value) || 0;
    var markup = parseFloat(document.getElementById('fcl-markup').value) || 0;
    if (!base) { alert('Please enter the Base Freight Rate.'); return; }
    var total = base + baf + port + markup;
    var html = '<div class="table-responsive"><table class="table table-bordered align-middle"><thead class="table-light"><tr><th>Component</th><th class="text-end">Amount (USD)</th></tr></thead><tbody>' +
        calcRow('Reference', ref, false) +
        calcRow('Container Type', cont, false) +
        calcRow('Route', route, false) +
        calcRow('Base Freight Rate', usd(base), false) +
        calcRow('BAF - Bunker Adjustment Factor (Fuel Surcharge)', usd(baf), false) +
        calcRow('Port Handling Fees', usd(port), false) +
        calcRow('GlobalTrade Markup', usd(markup), false) +
        calcRow('TOTAL = ' + usd(base) + ' + ' + usd(baf) + ' + ' + usd(port) + ' + ' + usd(markup), usd(total), true) +
        '</tbody></table></div><div class="alert alert-success mt-3 fs-5"><strong>Final FCL Cost: </strong><span class="fw-bold ms-2">' + usd(total) + '</span></div>';
    document.getElementById('freight-result-body').innerHTML = html;
    document.getElementById('freight-result').style.display = '';
}
// ==================== END FREIGHT CALCULATOR ====================

// ==================== SUBSCRIPTIONS ====================
let allSubs = [];

async function loadSubscriptions() {
    const res = await apiCall(CTX + '/api/billing/subscriptions');
    if (res && res.ok) {
        const d = await res.json();
        allSubs = d.data;
        const tbody = document.getElementById('subscriptionsTbody');
        const sel = document.getElementById('assignSubPlan');
        tbody.innerHTML = '';
        sel.innerHTML = '<option value="">Select Plan...</option>';
        if (allSubs.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No subscription plans found.</td></tr>';
            return;
        }
        allSubs.forEach(s => {
            tbody.innerHTML += '<tr>' +
                '<td><strong>' + s.name + '</strong></td>' +
                '<td>$' + s.price.toFixed(2) + '</td>' +
                '<td>' + s.billingCycle + '</td>' +
                '<td><button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#subscriptionModal" onclick="editSubscription(' + s.id + ')">Edit</button></td>' +
                '</tr>';
            sel.innerHTML += '<option value="' + s.id + '">' + s.name + ' ($' + s.price + '/' + s.billingCycle + ')</option>';
        });
    }
}

function prepareSubscriptionForm() {
    document.getElementById('subId').value = '';
    document.getElementById('subName').value = '';
    document.getElementById('subPrice').value = '';
    document.getElementById('subCycle').value = 'MONTHLY';
}

function editSubscription(id) {
    const sub = allSubs.find(s => s.id === id);
    if (sub) {
        document.getElementById('subId').value = sub.id;
        document.getElementById('subName').value = sub.name;
        document.getElementById('subPrice').value = sub.price;
        document.getElementById('subCycle').value = sub.billingCycle;
    }
}

async function saveSubscription() {
    const id = document.getElementById('subId').value;
    const payload = {
        name: document.getElementById('subName').value,
        price: parseFloat(document.getElementById('subPrice').value),
        billingCycle: document.getElementById('subCycle').value
    };
    if (id) {
        await apiCall(CTX + '/api/billing/subscriptions/' + id, 'PUT', payload);
    } else {
        await apiCall(CTX + '/api/billing/subscriptions', 'POST', payload);
    }
    loadSubscriptions();
}

async function assignSubscription() {
    const cid = document.getElementById('assignSubCustomer').value;
    const sid = document.getElementById('assignSubPlan').value;
    if (!cid || !sid) {
        alert("Please select both Customer and Plan.");
        return;
    }
    await apiCall(CTX + '/api/billing/customer-subscription/' + cid + '/' + sid, 'PUT');
    alert("Subscription assigned successfully!");
}

async function loadCustomersForAssignment() {
    const res = await apiCall(CTX + '/api/ops/customers');
    if (res && res.ok) {
        const d = await res.json();
        const sel = document.getElementById('assignSubCustomer');
        sel.innerHTML = '<option value="">Select Customer...</option>';
        d.data.forEach(c => {
            sel.innerHTML += '<option value="' + c.id + '">' + c.companyName + ' (' + c.customerCode + ')</option>';
        });
    }
}

// =======================================================




// ==================== CARRIERS ====================
async function loadCarriers() {
    const res = await apiCall(CTX + '/api/ops/carriers');
    if(res && res.ok) {
        const d = await res.json();
        const tbody = document.getElementById('carriersBody');
        const assignSel = document.getElementById('assignCarrierSelect');
        let newHtml = '';
        let optsHtml = '<option value="">Select Carrier...</option>';
        d.data.forEach(c => {
            newHtml += '<tr>' +
                    '<td class="fw-bold">' + (c.companyId || '-') + '</td>' +
                    '<td>' + c.companyName + '</td>' +
                    '<td>' + (c.motherCompanyAddress || '-') + '</td>' +
                    '<td class="text-end">' +
                        '<button class="btn btn-sm btn-outline-primary" onclick="editCarrier(' + c.id + ', \'' + c.companyName + '\', \'' + (c.motherCompanyAddress||'') + '\')">Edit</button>' +
                    '</td>' +
                '</tr>';
            optsHtml += '<option value="'+c.id+'">'+c.companyName+'</option>';
        });
        if(d.data.length === 0) newHtml = '<tr><td colspan="4" class="text-center text-muted">No carriers registered.</td></tr>';
        
        if (tbody && tbody.innerHTML !== newHtml) { tbody.innerHTML = newHtml; }
        if (assignSel && assignSel.innerHTML !== optsHtml) { assignSel.innerHTML = optsHtml; }
    }
}

async function addCarrier() {
    const id = document.getElementById('editCarrierId').value;
    const name = document.getElementById('newCarrierName').value;
    const addr = document.getElementById('newCarrierAddress').value;
    if(!name) { alert('Carrier name is required.'); return; }
    
    let payload = { companyName: name, motherCompanyAddress: addr };
    
    if (id) {
        await apiCall(CTX + '/api/ops/carriers/' + id, 'PUT', payload);
    } else {
        await apiCall(CTX + '/api/ops/carriers', 'POST', payload);
    }
    
    cancelEditCarrier();
    loadCarriers();
}

function editCarrier(id, name, addr) {
    document.getElementById('editCarrierId').value = id;
    document.getElementById('newCarrierName').value = name;
    document.getElementById('newCarrierAddress').value = addr;
    document.getElementById('carrierFormTitle').innerText = 'Edit Carrier';
    document.getElementById('saveCarrierBtn').innerText = 'Update';
    document.getElementById('cancelEditCarrierBtn').classList.remove('d-none');
}

function cancelEditCarrier() {
    document.getElementById('editCarrierId').value = '';
    document.getElementById('newCarrierName').value = '';
    document.getElementById('newCarrierAddress').value = '';
    document.getElementById('carrierFormTitle').innerText = 'Add New Carrier';
    document.getElementById('saveCarrierBtn').innerText = 'Register';
    document.getElementById('cancelEditCarrierBtn').classList.add('d-none');
}

async function assignCarrierPrompt(id) {
    document.getElementById('assignCarrierOrderId').value = id;
    if(!carrierModal) carrierModal = new bootstrap.Modal(document.getElementById('assignCarrierModal'));
    carrierModal.show();
}

async function submitCarrierAssign() {
    const orderId = document.getElementById('assignCarrierOrderId').value;
    const carrierId = document.getElementById('assignCarrierSelect').value;
    const dim = document.getElementById('assignCarrierDimensions').value;
    const dt = document.getElementById('assignCarrierDateTime').value;
    
    if(!carrierId) { alert('Please select a carrier.'); return; }
    
    const payload = {
        carrierId: carrierId,
        dimensions: dim,
        shipmentDateTime: dt
    };
    
    await apiCall(CTX + '/api/ops/orders/' + orderId + '/assign-carrier', 'PUT', payload);
    carrierModal.hide();
    loadOrders();
}

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

                <h6 class="fw-bold mt-3 border-top pt-3">Logistics</h6>
                <p><strong>Carrier:</strong> <span id="opsOrdCarrier"></span></p>
                <p><strong>Dimensions:</strong> <span id="opsOrdDims"></span></p>
                <p><strong>Shipment Date/Time:</strong> <span id="opsOrdDateTime"></span></p>
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

<!-- Assign Warehouse Modal -->
<div class="modal fade" id="assignWarehouseModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Assign Warehouse</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="assignWhOrderId">
                <div class="mb-3">
                    <label class="form-label">Select Warehouse</label>
                    <select id="assignWhSelect" class="form-select">
                        <option value="">Loading warehouses...</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="submitWarehouseAssign()">Assign & Notify Vendor</button>
            </div>
        </div>
    </div>
</div>

<!-- Assign Carrier Modal -->
<div class="modal fade" id="assignCarrierModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Assign Carrier</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="assignCarrierOrderId">
                <div class="mb-3">
                    <label class="form-label">Carrier Company</label>
                    <select id="assignCarrierSelect" class="form-select"></select>
                </div>
                <div class="mb-3">
                    <label class="form-label">Order Dimensions (LxWxH or Vol.)</label>
                    <input type="text" id="assignCarrierDimensions" class="form-control" placeholder="e.g. 50x40x30 cm">
                </div>
                <div class="mb-3">
                    <label class="form-label">Shipment Date & Time</label>
                    <input type="datetime-local" id="assignCarrierDateTime" class="form-control">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="submitCarrierAssign()">Assign Carrier</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>






























