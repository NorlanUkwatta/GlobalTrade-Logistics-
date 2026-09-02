package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.Container;
import com.globaltrade.logistics.entity.Shipment;
import com.globaltrade.logistics.entity.Vendor;
import com.globaltrade.logistics.exception.LogisticsSystemException;
import com.globaltrade.logistics.interceptor.annotation.LogisticsAudit;
import com.globaltrade.logistics.interceptor.annotation.VendorIsolation;
import com.globaltrade.logistics.jms.ShipmentEvent;
import com.globaltrade.logistics.service.local.ShipmentService;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.annotation.security.RolesAllowed;
import com.globaltrade.logistics.service.NotificationServiceBean;
import java.util.List;
import java.util.UUID;

@Stateless
@LogisticsAudit
public class ShipmentServiceBean implements ShipmentService {

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    // Jakarta Messaging (JMS 2.0 API)
    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "java:global/jms/ShipmentEventQueue")
    private Queue shipmentQueue;

    @Inject
    private NotificationServiceBean notificationService;

    @Override
    @RolesAllowed({"ADMIN", "VENDOR_REP"})
    public Shipment createShipment(String origin, String destination, Long vendorId, Long containerId, String customerEmail, String customerName, String deliveryAddress) {
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        shipment.setPublicToken(UUID.randomUUID().toString());
        shipment.setOrigin(origin);
        shipment.setDestination(destination);
        shipment.setDeliveryAddress(deliveryAddress);
        shipment.setCustomerEmail(customerEmail);
        shipment.setCustomerName(customerName);
        shipment.setStatus(Shipment.Status.PENDING);

        if (vendorId != null) {
            Vendor v = em.find(Vendor.class, vendorId);
            if (v == null) throw new LogisticsSystemException("VENDOR_NOT_FOUND", "Vendor not found");
            shipment.setVendor(v);
        }

        if (containerId != null) {
            Container c = em.find(Container.class, containerId);
            if (c == null) throw new LogisticsSystemException("CONTAINER_NOT_FOUND", "Container not found");
            shipment.setContainer(c);
        }

        em.persist(shipment);
        
        // Publish event
        publishEvent(shipment.getId(), Shipment.Status.PENDING, origin, "Shipment created");

        return shipment;
    }

    @Override
    @jakarta.annotation.security.PermitAll
    public Shipment findByPublicToken(String token) {
        java.util.List<Shipment> list = em.createQuery("SELECT s FROM Shipment s WHERE s.publicToken = :t", Shipment.class)
            .setParameter("t", token)
            .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    @RolesAllowed({"ADMIN", "VENDOR_REP", "LOGISTICS_COORD"})
    @VendorIsolation
    public Shipment findById(Long id) {
        return em.find(Shipment.class, id);
    }

    @Override
    @RolesAllowed({"ADMIN", "LOGISTICS_COORD"})
    public List<Shipment> findAll() {
        return em.createNamedQuery("Shipment.findAll", Shipment.class).getResultList();
    }

    @Override
    @RolesAllowed({"ADMIN", "VENDOR_REP", "LOGISTICS_COORD"})
    public List<Shipment> findByVendor(Long vendorId) {
        return em.createNamedQuery("Shipment.findByVendor", Shipment.class)
                 .setParameter("vendorId", vendorId)
                 .getResultList();
    }

    @Override
    @RolesAllowed({"ADMIN", "VENDOR_REP", "LOGISTICS_COORD"})
    @VendorIsolation
    public void updateStatus(Shipment shipment, Shipment.Status newStatus, String location, String remarks) {
        if (shipment == null) throw new LogisticsSystemException("SHIPMENT_NOT_FOUND", "Shipment cannot be null");

        Shipment.Status oldStatus = shipment.getStatus();
        shipment.setStatus(newStatus);
        shipment = em.merge(shipment);

        publishEvent(shipment.getId(), newStatus, location, remarks);

        // Notify customer when handed over (IN_TRANSIT)
        if (newStatus == Shipment.Status.SHIPPED && oldStatus != Shipment.Status.SHIPPED) {
            if (shipment.getCustomerEmail() != null && !shipment.getCustomerEmail().isEmpty()) {
                String trackingUrl = "http://localhost:8080/globaltrade-web/tracking.jsp?token=" + shipment.getPublicToken();
                String emailBody = "Dear " + shipment.getCustomerName() + ",\n\n" +
                        "Your shipment " + shipment.getTrackingNumber() + " has been handed over to GlobalTrade Logistics.\n" +
                        "You can track its status here: " + trackingUrl + "\n\n" +
                        "Thank you for using our service.";
                notificationService.sendEmail(shipment.getCustomerEmail(), "Shipment In Transit: " + shipment.getTrackingNumber(), emailBody);
            }
        }
    }

    private void publishEvent(Long shipmentId, Shipment.Status status, String location, String remarks) {
        try {
            ShipmentEvent event = new ShipmentEvent(shipmentId, status, location, remarks);
            jmsContext.createProducer().send(shipmentQueue, event);
        } catch (Exception e) {
            // Log but don't fail transaction if JMS fails in this demo
            e.printStackTrace();
        }
    }
}
