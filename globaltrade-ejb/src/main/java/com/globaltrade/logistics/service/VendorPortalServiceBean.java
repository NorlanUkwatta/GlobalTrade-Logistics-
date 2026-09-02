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

        return em.merge(vendor);
    }

    @Override
    public List<ShippingOrder> getShippingOrders(Long vendorId) {
        return em.createNamedQuery("ShippingOrder.findByVendor", ShippingOrder.class)
            .setParameter("vendorId", vendorId)
            .getResultList();
    }

    @Override
    public ShippingOrder createShippingOrder(Long vendorId, ShippingOrder order) {
        Vendor vendor = em.find(Vendor.class, vendorId);
        if (vendor == null) return null;
        
        // Auto-generate routing details
        String fromCity = (vendor.getPickupCity() != null && !vendor.getPickupCity().isEmpty()) ? vendor.getPickupCity() : "Origin";
        String fromCountry = (vendor.getPickupCountry() != null && !vendor.getPickupCountry().isEmpty()) ? vendor.getPickupCountry() : "";
        order.setRouteFrom(fromCity + (fromCountry.isEmpty() ? "" : ", " + fromCountry));

        String toCity = (order.getCity() != null && !order.getCity().isEmpty()) ? order.getCity() : "Destination";
        String toCountry = (order.getCountry() != null && !order.getCountry().isEmpty()) ? order.getCountry() : "";
        order.setRouteTo(toCity + (toCountry.isEmpty() ? "" : ", " + toCountry));

        order.setVendor(vendor);
        em.persist(order);
        return order;
    }

    @Override
    public List<ReturnedItem> getReturnedItems(Long vendorId) {
        return em.createNamedQuery("ReturnedItem.findByVendor", ReturnedItem.class)
            .setParameter("vendorId", vendorId)
            .getResultList();
    }
}