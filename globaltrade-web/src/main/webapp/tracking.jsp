<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Track Shipment Ã¢â‚¬â€ GlobalTrade Logistics</title>
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

<div class="tracking-header text-center">
    <div class="container">
        <i class="bi bi-box-seam" style="font-size: 3rem;"></i>
        <h1 class="mt-3">Shipment Tracking</h1>
        <p class="lead">Real-time status of your parcel</p>
    </div>
</div>

<div class="container" style="max-width: 800px;">
    <div id="loading" class="text-center py-5">
        <div class="spinner-border text-success" role="status"></div>
        <p class="mt-2 text-muted">Fetching shipment details...</p>
    </div>

    <div id="error-card" class="card p-5 text-center d-none">
        <i class="bi bi-exclamation-triangle text-danger" style="font-size: 3rem;"></i>
        <h3 class="mt-3">Tracking Information Not Found</h3>
        <p class="text-muted">The link you followed may be invalid or expired. Please check your email and try again.</p>
        <a href="login.jsp" class="btn btn-outline-secondary mt-3">Back to Home</a>
    </div>

    <div id="shipment-card" class="d-none">
        <div class="card mb-4">
            <div class="card-body p-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h6 class="text-muted text-uppercase small fw-bold">Tracking Number</h6>
                        <h3 id="trackingNumber" class="mb-0">TRK-XXXXXXX</h3>
                    </div>
                    <div>
                        <span id="statusBadge" class="badge status-badge">PENDING</span>
                    </div>
                </div>

                <div class="row g-4">
                    <div class="col-md-6">
                        <label class="text-muted small fw-bold">RECIPIENT</label>
                        <p id="customerName" class="fs-5 mb-0">-</p>
                        <p id="deliveryAddress" class="text-muted small">-</p>
                    </div>
                    <div class="col-md-6 text-md-end">
                        <label class="text-muted small fw-bold">VENDOR</label>
                        <p id="vendorName" class="fs-5 mb-0">-</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header bg-white py-3">
                <h5 class="mb-0 fw-bold">Journey Details</h5>
            </div>
            <div class="card-body p-4">
                <div class="row text-center">
                    <div class="col-5">
                        <h6 class="text-muted small fw-bold">ORIGIN</h6>
                        <p id="origin" class="fs-5">-</p>
                    </div>
                    <div class="col-2 d-flex align-items-center justify-content-center">
                        <i class="bi bi-arrow-right fs-4 text-muted"></i>
                    </div>
                    <div class="col-5">
                        <h6 class="text-muted small fw-bold">DESTINATION</h6>
                        <p id="destination" class="fs-5">-</p>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="text-center mt-4 text-muted small">
            <p>&copy; 2024 GlobalTrade Logistics Corporation. All rights reserved.</p>
        </div>
    </div>
</div>

<script>
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    const CTX = '${pageContext.request.contextPath}';

    async function loadTracking() {
        if (!token) {
            showError();
            return;
        }

        try {
            const resp = await fetch(CTX + '/api/public/tracking/' + token);
            const data = await resp.json();

            if (resp.ok && data.success) {
                renderShipment(data.data);
            } else {
                showError();
            }
        } catch (err) {
            console.error(err);
            showError();
        }
    }

    function renderShipment(s) {
        document.getElementById('loading').classList.add('d-none');
        document.getElementById('shipment-card').classList.remove('d-none');

        document.getElementById('trackingNumber').innerText = s.trackingNumber;
        document.getElementById('customerName').innerText = s.customerName;
        document.getElementById('deliveryAddress').innerText = s.deliveryAddress || 'Address not specified';
        document.getElementById('vendorName').innerText = s.vendor ? s.vendor.companyName : 'Direct Shipment';
        document.getElementById('origin').innerText = s.origin;
        document.getElementById('destination').innerText = s.destination;

        const badge = document.getElementById('statusBadge');
        badge.innerText = s.status;
        
        // Color coding
        if (s.status === 'DELIVERED') badge.classList.add('bg-success');
        else if (s.status === 'SHIPPED') badge.classList.add('bg-primary');
        else if (s.status === 'CANCELLED') badge.classList.add('bg-danger');
        else badge.classList.add('bg-warning', 'text-dark');
    }

    function showError() {
        document.getElementById('loading').classList.add('d-none');
        document.getElementById('error-card').classList.remove('d-none');
    }

    window.onload = loadTracking;
</script>
</body>
</html>

