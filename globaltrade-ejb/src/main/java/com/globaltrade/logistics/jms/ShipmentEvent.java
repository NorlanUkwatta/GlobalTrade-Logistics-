package com.globaltrade.logistics.jms;

import com.globaltrade.logistics.entity.Shipment;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ShipmentEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long shipmentId;
    private Shipment.Status newStatus;
    private String location;
    private String remarks;
    private LocalDateTime timestamp;

    public ShipmentEvent(Long shipmentId, Shipment.Status newStatus, String location, String remarks) {
        this.shipmentId = shipmentId;
        this.newStatus = newStatus;
        this.location = location;
        this.remarks = remarks;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public Long getShipmentId() { return shipmentId; }
    public Shipment.Status getNewStatus() { return newStatus; }
    public String getLocation() { return location; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getTimestamp() { return timestamp; }
}