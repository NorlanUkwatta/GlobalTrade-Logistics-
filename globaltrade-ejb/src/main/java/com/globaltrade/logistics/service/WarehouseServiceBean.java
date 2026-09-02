package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.InventoryItem;
import com.globaltrade.logistics.entity.PurchaseOrder;
import com.globaltrade.logistics.entity.Vendor;
import com.globaltrade.logistics.exception.LogisticsSystemException;
import com.globaltrade.logistics.service.local.WarehouseService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;

@Stateless
public class WarehouseServiceBean implements WarehouseService {

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @Override
    @RolesAllowed({"ADMIN", "WAREHOUSE_MGR", "LOGISTICS_COORD"})
    public List<InventoryItem> findAllInventory() {
        return em.createNamedQuery("InventoryItem.findAll", InventoryItem.class).getResultList();
    }

    @Override
    @RolesAllowed({"ADMIN", "WAREHOUSE_MGR"})
    public InventoryItem updateInventory(String sku, String name, Integer quantity, String location) {
        List<InventoryItem> items = em.createQuery("SELECT i FROM InventoryItem i WHERE i.sku = :sku", InventoryItem.class)
            .setParameter("sku", sku).getResultList();
            
        InventoryItem item;
        if (items.isEmpty()) {
            item = new InventoryItem();
            item.setSku(sku);
            item.setName(name);
            item.setQuantity(quantity);
            item.setLocation(location);
            em.persist(item);
        } else {
            item = items.get(0);
            if (name != null) item.setName(name);
            if (quantity != null) item.setQuantity(quantity);
            if (location != null) item.setLocation(location);
            em.merge(item);
        }
        return item;
    }

    @Override
    @RolesAllowed({"ADMIN", "WAREHOUSE_MGR", "LOGISTICS_COORD"})
    public List<PurchaseOrder> findAllPurchaseOrders() {
        return em.createNamedQuery("PurchaseOrder.findAll", PurchaseOrder.class).getResultList();
    }

    @Override
    @RolesAllowed({"ADMIN", "VENDOR_REP", "WAREHOUSE_MGR"})
    public List<PurchaseOrder> findPurchaseOrdersByVendor(Long vendorId) {
        return em.createNamedQuery("PurchaseOrder.findByVendor", PurchaseOrder.class)
                 .setParameter("vendorId", vendorId).getResultList();
    }

    @Override
    @RolesAllowed({"ADMIN", "WAREHOUSE_MGR"})
    public PurchaseOrder createPurchaseOrder(Long vendorId, String sku, Integer quantity) {
        Vendor v = em.find(Vendor.class, vendorId);
        if (v == null) throw new LogisticsSystemException("VENDOR_NOT_FOUND", "Vendor not found");
        
        PurchaseOrder po = new PurchaseOrder();
        po.setVendor(v);
        po.setSku(sku);
        po.setQuantity(quantity);
        po.setStatus(PurchaseOrder.Status.PENDING);
        em.persist(po);
        return po;
    }

    @Override
    @RolesAllowed({"ADMIN", "WAREHOUSE_MGR", "VENDOR_REP"})
    public PurchaseOrder updatePurchaseOrderStatus(Long poId, PurchaseOrder.Status status) {
        PurchaseOrder po = em.find(PurchaseOrder.class, poId);
        if (po == null) throw new LogisticsSystemException("PO_NOT_FOUND", "Purchase Order not found");
        
        po.setStatus(status);
        em.merge(po);
        return po;
    }

    @Override
    @RolesAllowed({"ADMIN", "VENDOR_REP"})
    public PurchaseOrder acknowledgePurchaseOrder(Long poId, java.time.LocalDate proposedDate) {
        PurchaseOrder po = em.find(PurchaseOrder.class, poId);
        if (po == null) throw new LogisticsSystemException("PO_NOT_FOUND", "Purchase Order not found");
        
        if (proposedDate != null) {
            po.setProposedDeliveryDate(proposedDate);
            po.setStatus(PurchaseOrder.Status.DELAY_REQUESTED);
        } else {
            po.setStatus(PurchaseOrder.Status.ACKNOWLEDGED);
        }
        
        return em.merge(po);
    }
}