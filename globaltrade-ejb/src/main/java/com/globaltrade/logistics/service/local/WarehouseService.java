package com.globaltrade.logistics.service.local;

import com.globaltrade.logistics.entity.InventoryItem;
import com.globaltrade.logistics.entity.PurchaseOrder;
import java.util.List;

public interface WarehouseService {
    List<InventoryItem> findAllInventory();
    InventoryItem updateInventory(String sku, String name, Integer quantity, String location);
    
    List<PurchaseOrder> findAllPurchaseOrders();
    List<PurchaseOrder> findPurchaseOrdersByVendor(Long vendorId);
    PurchaseOrder createPurchaseOrder(Long vendorId, String sku, Integer quantity);
    PurchaseOrder updatePurchaseOrderStatus(Long poId, PurchaseOrder.Status status);
    PurchaseOrder acknowledgePurchaseOrder(Long poId, java.time.LocalDate proposedDate);
}