package com.globaltrade.logistics.timer;

import com.globaltrade.logistics.entity.PurchaseOrder;
import com.globaltrade.logistics.entity.Vendor;
import com.globaltrade.logistics.service.DocumentGenerationServiceBean;
import com.globaltrade.logistics.service.NotificationServiceBean;
import com.globaltrade.logistics.service.local.WarehouseService;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

@Singleton
@Startup
public class VendorScorecardTimer {

    private static final Logger LOGGER = Logger.getLogger(VendorScorecardTimer.class.getName());

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @Inject
    private WarehouseService warehouseService;

    @Inject
    private DocumentGenerationServiceBean documentService;

    @Inject
    private NotificationServiceBean notificationService;

    @Schedule(dayOfMonth = "1", hour = "0", info = "Monthly Vendor Scorecard Generation")
    public void generateMonthlyScorecards() {
        LOGGER.info("Starting monthly vendor scorecard generation...");
        
        List<Vendor> vendors = em.createQuery("SELECT v FROM Vendor v", Vendor.class).getResultList();
        
        for (Vendor vendor : vendors) {
            try {
                List<PurchaseOrder> orders = warehouseService.findPurchaseOrdersByVendor(vendor.getId());
                byte[] pdf = documentService.generateVendorScorecard(vendor, orders);
                
                // In a real system, we'd save this to a 'Reports' table or S3
                // Here we just notify the vendor that it's ready
                notificationService.sendEmail(vendor.getEmail(), 
                    "Monthly Performance Scorecard - " + java.time.Month.values()[java.time.LocalDate.now().getMonthValue()-2].name(),
                    "Dear " + vendor.getContactName() + ",\n\nYour monthly scorecard is now available in the portal.\n\n" +
                    "Current Grade: " + calculateGrade(vendor.getPerformanceScore()));
                
                LOGGER.info("Generated scorecard for " + vendor.getCompanyName());
            } catch (Exception e) {
                LOGGER.severe("Failed to generate scorecard for vendor " + vendor.getId() + ": " + e.getMessage());
            }
        }
    }
    
    private String calculateGrade(Double score) {
        if (score == null) return "N/A";
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        return "F";
    }
}
