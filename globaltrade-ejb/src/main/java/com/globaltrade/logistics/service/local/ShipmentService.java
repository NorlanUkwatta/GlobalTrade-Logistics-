package com.globaltrade.logistics.service.local;

import com.globaltrade.logistics.entity.Shipment;
import java.util.List;

public interface ShipmentService {
    Shipment createShipment(String origin, String destination, Long vendorId, Long containerId, String customerEmail, String customerName, String deliveryAddress);
    Shipment findById(Long id);
    Shipment findByPublicToken(String token);
    List<Shipment> findAll();
    List<Shipment> findByVendor(Long vendorId);
    void updateStatus(Shipment shipment, Shipment.Status newStatus, String location, String remarks);
}
