package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "returned_items")
@NamedQueries({
    @NamedQuery(name = "ReturnedItem.findByVendor", query = "SELECT r FROM ReturnedItem r WHERE r.shippingOrder.vendor.id = :vendorId ORDER BY r.returnedAt DESC")
})
public class ReturnedItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shipping_order_id", nullable = false)
    private ShippingOrder shippingOrder;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(length = 500)
    private String reason;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @PrePersist
    protected void onCreate() {
        if (returnedAt == null) returnedAt = LocalDateTime.now();
    }

    public ReturnedItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ShippingOrder getShippingOrder() { return shippingOrder; }
    public void setShippingOrder(ShippingOrder shippingOrder) { this.shippingOrder = shippingOrder; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getReturnedAt() { return returnedAt; }
    public void setReturnedAt(LocalDateTime returnedAt) { this.returnedAt = returnedAt; }
}