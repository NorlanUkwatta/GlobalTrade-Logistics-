package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_settlements")
@NamedQueries({
    @NamedQuery(name = "PaymentSettlement.findByVendor", query = "SELECT p FROM PaymentSettlement p WHERE p.vendor.id = :vendorId ORDER BY p.createdAt DESC")
})
public class PaymentSettlement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    @jakarta.json.bind.annotation.JsonbTransient
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    @jakarta.json.bind.annotation.JsonbTransient
    private PurchaseOrder purchaseOrder;

        @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_order_id")
    @jakarta.json.bind.annotation.JsonbTransient
    private ShippingOrder shippingOrder;

    @Column(name = "customer_id")
    private Long customerId;

    private Double amount;
    
    @Column(name = "is_paid")
    private Boolean isPaid = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }
        public ShippingOrder getShippingOrder() { return shippingOrder; }
    public void setShippingOrder(ShippingOrder shippingOrder) { this.shippingOrder = shippingOrder; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Boolean getIsPaid() { return isPaid; }
    public void setIsPaid(Boolean isPaid) { this.isPaid = isPaid; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}