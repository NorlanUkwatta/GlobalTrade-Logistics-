package com.globaltrade.logistics.timer;

import com.globaltrade.logistics.entity.Vendor;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Random;

@Singleton
public class VendorPerformanceTimer {

    private static final Logger LOG = LogManager.getLogger(VendorPerformanceTimer.class);
    
    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;
    
    private final Random random = new Random();

    /**
     * Calculates vendor score based on automated logic. Runs daily in prod, but for demo, every 5 mins.
     */
    @Schedule(hour = "*", minute = "*/5", persistent = false)
    public void calculatePerformanceScores() {
        LOG.info("=== [TIMER] Calculating Vendor Performance Scores ===");
        
        List<Vendor> vendors = em.createQuery("SELECT v FROM Vendor v", Vendor.class).getResultList();
        
        for (Vendor v : vendors) {
            // Simulated scoring logic based on hypothetical late shipments/defects
            double currentScore = v.getPerformanceScore() != null ? v.getPerformanceScore() : 100.0;
            
            // Random fluctuation (-2.0 to +1.5)
            double fluctuation = (random.nextDouble() * 3.5) - 2.0; 
            currentScore += fluctuation;
            
            // Cap between 0 and 100
            if (currentScore > 100) currentScore = 100.0;
            if (currentScore < 0) currentScore = 0.0;
            
            v.setPerformanceScore(currentScore);
            em.merge(v);
            LOG.info("[TIMER] Vendor '{}' new score: {}", v.getCompanyName(), String.format("%.2f", currentScore));
        }
    }
}