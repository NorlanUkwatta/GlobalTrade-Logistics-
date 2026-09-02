<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Track Shipment Ã¢â‚¬â€ GlobalTrade Logistics</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        body { background-color: #f8f9fa; }
        .tracking-header { background: linear-gradient(135deg, #0d1b2a 0%, #1b4332 100%); color: white; padding: 3rem 0; margin-bottom: 2rem; }
        .status-badge { font-size: 1.1rem; padding: 0.5rem 1.2rem; border-radius: 50px; }
        .card { border: none; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
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
