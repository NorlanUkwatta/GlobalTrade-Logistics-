package com.globaltrade.logistics.service;
import com.globaltrade.logistics.entity.*;
import com.globaltrade.logistics.service.local.VendorPortalService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class VendorPortalServiceBean implements VendorPortalService {

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @Override
    public PurchaseOrder findPurchaseOrder(Long id) {
        return em.find(PurchaseOrder.class, id);
    }

    @Override
    public AdvancedShippingNotice submitASN(Long poId, AdvancedShippingNotice req) {
        PurchaseOrder po = em.find(PurchaseOrder.class, poId);
        if (po == null) return null;

        AdvancedShippingNotice asn = new AdvancedShippingNotice();
        asn.setPurchaseOrder(po);
        asn.setDimensions(req.getDimensions());
        asn.setWeight(req.getWeight());
        asn.setPalletCount(req.getPalletCount());
        asn.setReceiverName(req.getReceiverName());
        asn.setReceiverEmail(req.getReceiverEmail());
        asn.setReceiverMobile(req.getReceiverMobile());
        asn.setReceiverAddress(req.getReceiverAddress());
        
        em.persist(asn);
        po.setStatus(PurchaseOrder.Status.READY_FOR_PICKUP);
        em.merge(po);
        return asn;
    }

    @Override
    public ComplianceDocument uploadCompliance(Long vendorId, String type, String fileName) {
        Vendor vendor = em.find(Vendor.class, vendorId);
        ComplianceDocument doc = new ComplianceDocument();
        doc.setVendor(vendor);
        doc.setType(type);
        doc.setFilePath("/uploads/compliance/" + fileName);
        em.persist(doc);
        return doc;
    }

    @Override
    public Vendor findVendor(Long id) {
        return em.find(Vendor.class, id);
    }

    @Override
    public List<PaymentSettlement> getSettlements(Long vendorId) {
        return em.createNamedQuery("PaymentSettlement.findByVendor", PaymentSettlement.class)
            .setParameter("vendorId", vendorId)
            .getResultList();
    }

    @Override
    public Vendor updateProfile(Long vendorId, Vendor updatedData) {
        Vendor vendor = em.find(Vendor.class, vendorId);
        if (vendor == null) return null;

        vendor.setCompanyName(updatedData.getCompanyName());
        vendor.setContactName(updatedData.getContactName());
        vendor.setEmail(updatedData.getEmail());
        vendor.setPhone(updatedData.getPhone());
        vendor.setPickupAddressLine1(updatedData.getPickupAddressLine1());
        vendor.setPickupAddressLine2(updatedData.getPickupAddressLine2());
        vendor.setPickupCity(updatedData.getPickupCity());
        vendor.setPickupState(updatedData.getPickupState());
        vendor.setPickupPostalCode(updatedData.getPickupPostalCode());
        vendor.setPickupCountry(updatedData.getPickupCountry());
        vendor.setRegistrationNumber(updatedData.getRegistrationNumber());
        vendor.setHeadquartersAddress(updatedData.getHeadquartersAddress());
        vendor.setStandardLeadTimeDays(updatedData.getStandardLeadTimeDays());
        
        // We do not update the commodity category here to prevent unauthorized changes 
        // without Ops approval, but the fields are synced.

        return em.merge(vendor);
    }

    @Override
    public ShippingOrder updateVendorDecision(Long vendorId, Long orderId, ShippingOrder.VendorDecision decision, String reason, String proposedDate) {
        ShippingOrder order = em.find(ShippingOrder.class, orderId);
        if (order == null || order.getVendor() == null || !order.getVendor().getId().equals(vendorId)) {
            throw new IllegalArgumentException("Order not found or not assigned to this vendor");
        }
        order.setVendorDecision(decision);
        order.setVendorDecisionReason(reason);
        order.setVendorProposedDate(proposedDate);
        return order;
    }

    @Override
        public ShippingOrder getShippingOrder(Long id, Long vendorId) {
        ShippingOrder order = em.find(ShippingOrder.class, id);
        if (order != null && order.getVendor() != null && order.getVendor().getId().equals(vendorId)) {
            return order;
        }
        return null;
    }

    public ShippingOrder submitVendorDecision(Long id, ShippingOrder.VendorDecision decision, String reason, String date) {
        ShippingOrder order = em.find(ShippingOrder.class, id);
        if(order != null) {
            order.setVendorDecision(decision);
            order.setVendorDecisionReason(reason);
            order.setVendorProposedDate(date);
            if (decision == ShippingOrder.VendorDecision.ACCEPTED) {
                order.setStatus(ShippingOrder.Status.IN_WAREHOUSE);
            } else if (decision == ShippingOrder.VendorDecision.REJECTED) {
                order.setStatus(ShippingOrder.Status.PENDING);
                order.setVendor(null); // Unassign the vendor so ops can reassign
            }
            em.merge(order);
        }
        return order;
    }

    public List<ShippingOrder> getShippingOrders(Long vendorId) {
        return em.createNamedQuery("ShippingOrder.findByVendor", ShippingOrder.class)
            .setParameter("vendorId", vendorId)
            .getResultList();
    }

    @Override
    public List<ReturnedItem> getReturnedItems(Long vendorId) {
        return em.createNamedQuery("ReturnedItem.findByVendor", ReturnedItem.class)
            .setParameter("vendorId", vendorId)
            .getResultList();
    }
}


