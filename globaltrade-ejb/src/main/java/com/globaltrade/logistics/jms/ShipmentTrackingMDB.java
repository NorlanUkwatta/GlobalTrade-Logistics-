package com.globaltrade.logistics.jms;

import com.globaltrade.logistics.entity.Shipment;
import com.globaltrade.logistics.entity.TrackingEvent;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSDestinationDefinition;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Message-Driven Bean that listens to the ShipmentEventQueue.
 * Processes tracking updates asynchronously to avoid blocking the REST API.
 */
@JMSDestinationDefinition(
    name = "java:global/jms/ShipmentEventQueue",
    interfaceName = "jakarta.jms.Queue",
    destinationName = "ShipmentEventQueue"
)
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:global/jms/ShipmentEventQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class ShipmentTrackingMDB implements MessageListener {

    private static final Logger LOG = LogManager.getLogger(ShipmentTrackingMDB.class);

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMsg = (ObjectMessage) message;
                if (objMsg.getObject() instanceof ShipmentEvent) {
                    ShipmentEvent event = (ShipmentEvent) objMsg.getObject();
                    processEvent(event);
                } else {
                    LOG.warn("Received unknown object type in ShipmentEventQueue: {}", objMsg.getObject().getClass().getName());
                }
            } else {
                LOG.warn("Received non-ObjectMessage in ShipmentEventQueue: {}", message.getClass().getName());
            }
        } catch (Exception e) {
            LOG.error("Failed to process JMS message: {}", e.getMessage(), e);
            // In a real system, might throw exception to trigger message redelivery/DLQ
        }
    }

    private void processEvent(ShipmentEvent event) {
        LOG.info("ASYNC PROCESSING: Updating tracking for Shipment ID {} -> {}", event.getShipmentId(), event.getNewStatus());
        
        Shipment shipment = em.find(Shipment.class, event.getShipmentId());
        if (shipment == null) {
            LOG.error("Shipment ID {} not found during async tracking update!", event.getShipmentId());
            return;
        }

        // 1. Create and persist the tracking event
        TrackingEvent tracking = new TrackingEvent();
        tracking.setShipment(shipment);
        tracking.setStatus(event.getNewStatus());
        tracking.setLocation(event.getLocation());
        tracking.setRemarks(event.getRemarks());
        tracking.setTimestamp(event.getTimestamp());
        
        em.persist(tracking);

        // 2. Simulate heavy operation (e.g., sending webhooks to customers)
        simulateWebhookNotification(shipment, event);
    }

    private void simulateWebhookNotification(Shipment shipment, ShipmentEvent event) {
        try {
            Thread.sleep(500); // simulate network delay
            LOG.info("WEBHOOK DELIVERED: Customer notified that Shipment {} is now {}.", shipment.getTrackingNumber(), event.getNewStatus());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}