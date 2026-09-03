package com.globaltrade.logistics.service.local;
import com.globaltrade.logistics.entity.*;
import java.util.List;
public interface VendorPortalService {
    PurchaseOrder findPurchaseOrder(Long id);
    AdvancedShippingNotice submitASN(Long poId, AdvancedShippingNotice req);
    ComplianceDocument uploadCompliance(Long vendorId, String type, String fileName);
    Vendor findVendor(Long id);
    List<PaymentSettlement> getSettlements(Long vendorId);

    // New Profile & Shipping Order methods
    Vendor updateProfile(Long vendorId, Vendor updatedData);
    ShippingOrder updateVendorDecision(Long vendorId, Long orderId, ShippingOrder.VendorDecision decision, String reason, String proposedDate);
        ShippingOrder getShippingOrder(Long id, Long vendorId);
    ShippingOrder submitVendorDecision(Long id, ShippingOrder.VendorDecision decision, String reason, String date);
    List<ShippingOrder> getShippingOrders(Long vendorId);

    List<ReturnedItem> getReturnedItems(Long vendorId);
}


