package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@NamedQueries({
    @NamedQuery(name = "Shipment.findAll", query = "SELECT s FROM Shipment s ORDER BY s.createdAt DESC"),
    @NamedQuery(name = "Shipment.findByVendor", query = "SELECT s FROM Shipment s WHERE s.vendor.id = :vendorId ORDER BY s.createdAt DESC"),
    @NamedQuery(name = "Shipment.findByTracking", query = "SELECT s FROM Shipment s WHERE s.trackingNumber = :tracking")
})
public class Shipment implements Serializable {

    public enum Status { PENDING, IN_PROGRESS, IN_WAREHOUSE, SHIPPED, RECEIVED_SHIPMENT, ON_DELIVERY, DELIVERED, RETURNED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 50)
    private String trackingNumber;

    @Column(nullable = false, length = 100)
    private String origin;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "customer_rating")
    private Integer customerRating;

    @Column(name = "customer_feedback", length = 1000)
    private String customerFeedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "customer_email", length = 150)
    private String customerEmail;

    @Column(name = "customer_name", length = 150)
    private String customerName;

    @Column(name = "public_token", length = 100)
    private String publicToken;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "container_id")
    private Container container;

    @Column(name = "carrier_name", length = 150)
    private String carrierName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Shipment() {}

    public Long getId() { return id; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public Integer getCustomerRating() { return customerRating; }
    public void setCustomerRating(Integer customerRating) { this.customerRating = customerRating; }
    public String getCustomerFeedback() { return customerFeedback; }
    public void setCustomerFeedback(String customerFeedback) { this.customerFeedback = customerFeedback; }
    public void setDestination(String destination) { this.destination = destination; }
    public Status getStatus() { return status; }
    public Vendor getVendor() { return vendor; }
    public void setStatus(Status status) { this.status = status; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }
    public Container getContainer() { return container; }
    public void setContainer(Container container) { this.container = container; }
    public String getCarrierName() { return carrierName; }
    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPublicToken() { return publicToken; }
    public void setPublicToken(String publicToken) { this.publicToken = publicToken; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
