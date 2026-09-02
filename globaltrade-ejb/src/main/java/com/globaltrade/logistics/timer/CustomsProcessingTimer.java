package com.globaltrade.logistics.timer;

import com.globaltrade.logistics.entity.CustomsDeclaration;
import com.globaltrade.logistics.entity.Shipment;
import com.globaltrade.logistics.entity.TrackingEvent;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Singleton
@com.globaltrade.logistics.interceptor.annotation.PerformanceMonitor
public class CustomsProcessingTimer {

    private static final Logger LOG = LogManager.getLogger(CustomsProcessingTimer.class);
    private final Random random = new Random();

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    /**
     * Runs every 1 minute.
     * Bypasses EJB Security restrictions by using the EntityManager directly.
     */
    @Schedule(hour = "*", minute = "*", second = "0", persistent = false)
    public void processPendingCustoms() {
        LOG.info("=== [TIMER] CustomsProcessingTimer triggered. Checking for pending declarations... ===");
        
        List<CustomsDeclaration> pending = em.createNamedQuery("CustomsDeclaration.findByStatus", CustomsDeclaration.class)
                   .setParameter("status", CustomsDeclaration.Status.SUBMITTED)
                   .getResultList();

        if (pending.isEmpty()) {
            LOG.info("[TIMER] No pending customs declarations found.");
            return;
        }

        for (CustomsDeclaration decl : pending) {
            LOG.info("[TIMER] Simulating external API check for Declaration #{}", decl.getId());
            
            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            
            boolean approved = random.nextInt(100) < 80;
            
            if (approved) {
                decl.setStatus(CustomsDeclaration.Status.APPROVED);
                decl.setRemarks("Auto-cleared by GovAPI");
                decl.setClearedAt(LocalDateTime.now());
                em.merge(decl);
                
                Shipment s = decl.getShipment();
                s.setStatus(Shipment.Status.IN_WAREHOUSE);
                em.merge(s);
                
                TrackingEvent event = new TrackingEvent();
                event.setShipment(s);
                event.setStatus(Shipment.Status.IN_WAREHOUSE);
                event.setLocation("Port Warehouse");
                event.setRemarks("Cleared customs.");
                event.setTimestamp(LocalDateTime.now());
                em.persist(event);
                
                LOG.info("[TIMER] Declaration #{} APPROVED. Shipment moved to WAREHOUSE.", decl.getId());
            } else {
                decl.setStatus(CustomsDeclaration.Status.REJECTED);
                decl.setRemarks("Missing commercial invoice via GovAPI");
                decl.setClearedAt(LocalDateTime.now());
                em.merge(decl);
                LOG.warn("[TIMER] Declaration #{} REJECTED.", decl.getId());
            }
        }
    }
}