package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders")
@NamedQueries({
    @NamedQuery(name = "PurchaseOrder.findAll", query = "SELECT p FROM PurchaseOrder p ORDER BY p.createdAt DESC"),
    @NamedQuery(name = "PurchaseOrder.findByVendor", query = "SELECT p FROM PurchaseOrder p WHERE p.vendor.id = :vendorId ORDER BY p.createdAt DESC")
})
public class PurchaseOrder implements Serializable {
    public enum Status { PENDING, ACKNOWLEDGED, IN_PRODUCTION, DELAY_REQUESTED, READY_FOR_PICKUP, FULFILLED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    @jakarta.json.bind.annotation.JsonbTransient
    private Vendor vendor;

    @Column(nullable = false, length = 50)
    private String sku;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "proposed_delivery_date")
    private java.time.LocalDate proposedDeliveryDate;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public PurchaseOrder() {}

    public Long getId() { return id; }
    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public java.time.LocalDate getProposedDeliveryDate() { return proposedDeliveryDate; }
    public void setProposedDeliveryDate(java.time.LocalDate proposedDeliveryDate) { this.proposedDeliveryDate = proposedDeliveryDate; }
}