package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class OpsServiceBean {

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    public List<ShippingOrder> getUnassignedOrders() {
        return em.createQuery("SELECT o FROM ShippingOrder o WHERE o.vendor IS NULL ORDER BY o.createdAt DESC", ShippingOrder.class)
                 .getResultList();
    }

    public List<ShippingOrder> getAllOrders() {
        return em.createQuery("SELECT o FROM ShippingOrder o ORDER BY o.createdAt DESC", ShippingOrder.class)
                 .getResultList();
    }

    public ShippingOrder assignVendor(Long orderId, Long vendorId) {
        ShippingOrder order = em.find(ShippingOrder.class, orderId);
        if (order == null) throw new IllegalArgumentException("Order not found");
        
        Vendor vendor = em.find(Vendor.class, vendorId);
        if (vendor == null) throw new IllegalArgumentException("Vendor not found");

        order.setVendor(vendor);
        order.setRouteFrom(vendor.getPickupCity() + ", " + vendor.getPickupCountry());
        
        // Also map the payment to this vendor if it exists
        List<PaymentSettlement> payments = em.createQuery("SELECT p FROM PaymentSettlement p WHERE p.shippingOrder.id = :oid", PaymentSettlement.class)
                                             .setParameter("oid", orderId)
                                             .getResultList();
        if (!payments.isEmpty()) {
            payments.get(0).setVendor(vendor);
        }

        return order;
    }

    public List<Shipment> getAllShipments() {
        return em.createQuery("SELECT s FROM Shipment s ORDER BY s.createdAt DESC", Shipment.class)
                 .getResultList();
    }

    public Shipment assignCarrier(Long shipmentId, String carrierName) {
        Shipment shipment = em.find(Shipment.class, shipmentId);
        if (shipment == null) throw new IllegalArgumentException("Shipment not found");
        shipment.setCarrierName(carrierName);
        return shipment;
    }

    public Shipment updateShipmentStatus(Long shipmentId, Shipment.Status status) {
        Shipment shipment = em.find(Shipment.class, shipmentId);
        if (shipment == null) throw new IllegalArgumentException("Shipment not found");
        shipment.setStatus(status);
        return shipment;
    }

    public List<Vendor> getAllVendors() {
        return em.createQuery("SELECT v FROM Vendor v ORDER BY v.companyName ASC", Vendor.class)
                 .getResultList();
    }
}
