package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.CustomsDeclaration;
import com.globaltrade.logistics.entity.Shipment;
import com.globaltrade.logistics.service.local.DashboardService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class DashboardServiceBean implements DashboardService {

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @Override
    @RolesAllowed("ADMIN")
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("users", em.createQuery("SELECT COUNT(u) FROM User u").getSingleResult());
        stats.put("vendors", em.createQuery("SELECT COUNT(v) FROM Vendor v").getSingleResult());
        stats.put("shipmentsActive", em.createQuery("SELECT COUNT(s) FROM Shipment s WHERE s.status <> :st")
            .setParameter("st", Shipment.Status.DELIVERED)
            .getSingleResult());
        stats.put("customsPending", em.createQuery("SELECT COUNT(c) FROM CustomsDeclaration c WHERE c.status = :cst")
            .setParameter("cst", CustomsDeclaration.Status.SUBMITTED)
            .getSingleResult());
        return stats;
    }
}