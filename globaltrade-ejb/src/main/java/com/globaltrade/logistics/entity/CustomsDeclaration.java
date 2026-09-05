package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "customs_declarations")
@NamedQueries({
    @NamedQuery(name = "CustomsDeclaration.findAll", query = "SELECT c FROM CustomsDeclaration c ORDER BY c.submittedAt DESC"),
    @NamedQuery(name = "CustomsDeclaration.findByStatus", query = "SELECT c FROM CustomsDeclaration c WHERE c.status = :status")
})
public class CustomsDeclaration implements Serializable {

    public enum Status {
        SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.SUBMITTED;

    @Column(name = "duty_amount")
    private Double dutyAmount;

    @Column(length = 255)
    private String remarks;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "cleared_at")
    private LocalDateTime clearedAt;

    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) submittedAt = LocalDateTime.now();
    }

    public CustomsDeclaration() {}

    public Long getId() { return id; }
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Double getDutyAmount() { return dutyAmount; }
    public void setDutyAmount(Double dutyAmount) { this.dutyAmount = dutyAmount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public LocalDateTime getClearedAt() { return clearedAt; }
    public void setClearedAt(LocalDateTime clearedAt) { this.clearedAt = clearedAt; }
}