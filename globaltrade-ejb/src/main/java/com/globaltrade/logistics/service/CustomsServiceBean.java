package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.CustomsDeclaration;
import com.globaltrade.logistics.entity.Shipment;
import com.globaltrade.logistics.exception.LogisticsSystemException;
import com.globaltrade.logistics.service.local.CustomsService;
import com.globaltrade.logistics.service.local.ShipmentService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
@com.globaltrade.logistics.interceptor.annotation.PerformanceMonitor
public class CustomsServiceBean implements CustomsService {

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;
    
    @Inject
    private ShipmentService shipmentService;

    @Override
    @RolesAllowed({"ADMIN", "VENDOR_REP", "LOGISTICS_COORD"})
    public CustomsDeclaration submitDeclaration(Long shipmentId, Double dutyAmount) {
        Shipment shipment = em.find(Shipment.class, shipmentId);
        if (shipment == null) throw new LogisticsSystemException("SHIPMENT_NOT_FOUND", "Shipment not found");
        
        CustomsDeclaration decl = new CustomsDeclaration();
        decl.setShipment(shipment);
        decl.setDutyAmount(dutyAmount);
        decl.setStatus(CustomsDeclaration.Status.SUBMITTED);
        
        em.persist(decl);
        
        // Update shipment status
        shipmentService.updateStatus(shipment, Shipment.Status.IN_PROGRESS, "Port Authority", "Customs declaration submitted.");
        
        return decl;
    }

    @Override
    @RolesAllowed({"ADMIN", "CUSTOMS_AGENT"})
    public CustomsDeclaration updateStatus(Long declarationId, CustomsDeclaration.Status status, String remarks) {
        CustomsDeclaration decl = em.find(CustomsDeclaration.class, declarationId);
        if (decl == null) throw new LogisticsSystemException("DECL_NOT_FOUND", "Declaration not found");
        
        decl.setStatus(status);
        decl.setRemarks(remarks);
        
        if (status == CustomsDeclaration.Status.APPROVED || status == CustomsDeclaration.Status.REJECTED) {
            decl.setClearedAt(LocalDateTime.now());
        }
        
        em.merge(decl);
        return decl;
    }

    @Override
    @RolesAllowed({"ADMIN", "CUSTOMS_AGENT"})
    public List<CustomsDeclaration> findPending() {
        return em.createNamedQuery("CustomsDeclaration.findByStatus", CustomsDeclaration.class)
                 .setParameter("status", CustomsDeclaration.Status.SUBMITTED)
                 .getResultList();
    }

    @Override
    @RolesAllowed({"ADMIN", "CUSTOMS_AGENT"})
    public List<CustomsDeclaration> findAll() {
        return em.createNamedQuery("CustomsDeclaration.findAll", CustomsDeclaration.class).getResultList();
    }
}
