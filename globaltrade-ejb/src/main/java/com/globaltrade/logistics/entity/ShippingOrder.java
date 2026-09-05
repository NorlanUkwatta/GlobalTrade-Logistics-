package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipping_orders")
@NamedQueries({
    @NamedQuery(name = "ShippingOrder.findByVendor", query = "SELECT o FROM ShippingOrder o WHERE o.vendor.id = :vendorId ORDER BY o.createdAt DESC"),
    @NamedQuery(name = "ShippingOrder.findByOrderId", query = "SELECT o FROM ShippingOrder o WHERE o.orderId = :orderId")
})
public class ShippingOrder implements Serializable {

        public enum Status { PENDING, IN_PROGRESS, ORDER_COMPLETED, READY_FOR_DELIVERY, IN_WAREHOUSE, WAREHOUSE_VERIFIED, SHIPPED, RECEIVED_SHIPMENT, ON_DELIVERY, DELIVERED, RETURNED, CANCELLED }
    public enum VendorDecision { PENDING, ACCEPTED, REJECTED, PROPOSED_DATE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true, length = 50)
    private String orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "customer_full_name", nullable = false, length = 150)
    private String customerFullName;

    @Column(length = 20)
    private String mobile;

    @Column(name = "address_line1", length = 200)
    private String addressLine1;

    @Column(name = "address_line2", length = 200)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 100)
    private String country;

    @Column(name = "order_description", length = 2000)
    private String orderDescription;

    private Double weight;

    @Column(name = "item_count")
    private Integer itemCount;

    @Column(name = "expected_timeline")
    private String expectedTimeline;

    @Column(name = "route_from", length = 150)
    private String routeFrom;

    @Column(name = "route_to", length = 150)
    private String routeTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

        @Enumerated(EnumType.STRING)
    @Column(name = "vendor_decision", length = 20)
    private VendorDecision vendorDecision;

    @Column(name = "vendor_decision_reason", length = 500)
    private String vendorDecisionReason;

    @Column(name = "vendor_proposed_date", length = 50)
    private String vendorProposedDate;

    @Column(name = "product_design_doc_url", columnDefinition = "LONGTEXT")
    private String productDesignDocUrl;

    @Column(name = "quality_standards_doc_url", columnDefinition = "LONGTEXT")
    private String qualityStandardsDocUrl;

    @Column(name = "customer_id")
    private Long customerId;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (orderId == null || orderId.isEmpty()) {
            orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public ShippingOrder() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }

    public String getCustomerFullName() { return customerFullName; }
    public void setCustomerFullName(String customerFullName) { this.customerFullName = customerFullName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getOrderDescription() { return orderDescription; }
    public void setOrderDescription(String orderDescription) { this.orderDescription = orderDescription; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }

    public String getExpectedTimeline() { return expectedTimeline; }
    public void setExpectedTimeline(String expectedTimeline) { this.expectedTimeline = expectedTimeline; }

    public String getRouteFrom() { return routeFrom; }
    public void setRouteFrom(String routeFrom) { this.routeFrom = routeFrom; }

    public String getRouteTo() { return routeTo; }
    public void setRouteTo(String routeTo) { this.routeTo = routeTo; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public VendorDecision getVendorDecision() { return vendorDecision; }
    public void setVendorDecision(VendorDecision vendorDecision) { this.vendorDecision = vendorDecision; }

    public String getVendorDecisionReason() { return vendorDecisionReason; }
    public void setVendorDecisionReason(String vendorDecisionReason) { this.vendorDecisionReason = vendorDecisionReason; }

    public String getVendorProposedDate() { return vendorProposedDate; }
    public void setVendorProposedDate(String vendorProposedDate) { this.vendorProposedDate = vendorProposedDate; }

        @Column(name = "ops_assignee_id")
    private Long opsAssigneeId;

    @Column(name = "ops_assignee_name", length = 150)
    private String opsAssigneeName;

    public Long getOpsAssigneeId() { return opsAssigneeId; }
    public void setOpsAssigneeId(Long opsAssigneeId) { this.opsAssigneeId = opsAssigneeId; }

    public String getOpsAssigneeName() { return opsAssigneeName; }
    public void setOpsAssigneeName(String opsAssigneeName) { this.opsAssigneeName = opsAssigneeName; }

    public String getProductDesignDocUrl() { return productDesignDocUrl; }
    public void setProductDesignDocUrl(String productDesignDocUrl) { this.productDesignDocUrl = productDesignDocUrl; }

    public String getQualityStandardsDocUrl() { return qualityStandardsDocUrl; }
    public void setQualityStandardsDocUrl(String qualityStandardsDocUrl) { this.qualityStandardsDocUrl = qualityStandardsDocUrl; }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_warehouse_id")
    private Warehouse assignedWarehouse;

    @ManyToOne
    @JoinColumn(name = "assigned_carrier_id")
    private Carrier assignedCarrier;

    @Column(name = "order_dimensions", length = 255)
    private String orderDimensions;

    public String getOrderDimensions() { return orderDimensions; }
    public void setOrderDimensions(String orderDimensions) { this.orderDimensions = orderDimensions; }

    @Column(name = "shipment_date_time", length = 100)
    private String shipmentDateTime;

    public String getShipmentDateTime() { return shipmentDateTime; }
    public void setShipmentDateTime(String shipmentDateTime) { this.shipmentDateTime = shipmentDateTime; }

    public Warehouse getAssignedWarehouse() { return assignedWarehouse; }
    public void setAssignedWarehouse(Warehouse assignedWarehouse) { this.assignedWarehouse = assignedWarehouse; }

    public Carrier getAssignedCarrier() { return assignedCarrier; }

    @Column(name = "return_type", length = 20)
    private String returnType; // "FULL", "PARTIAL"

    @Column(name = "return_reason", length = 255)
    private String returnReason;

    @Column(name = "return_quantity")
    private Integer returnQuantity;

    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }

    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }

    public Integer getReturnQuantity() { return returnQuantity; }
    public void setReturnQuantity(Integer returnQuantity) { this.returnQuantity = returnQuantity; }
    public void setAssignedCarrier(Carrier assignedCarrier) { this.assignedCarrier = assignedCarrier; }
}






