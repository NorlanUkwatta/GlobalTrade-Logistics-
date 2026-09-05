package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_documents")
@NamedQueries({
    @NamedQuery(name = "ComplianceDocument.findByVendor", query = "SELECT d FROM ComplianceDocument d WHERE d.vendor.id = :vendorId ORDER BY d.createdAt DESC")
})
public class ComplianceDocument implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    @jakarta.json.bind.annotation.JsonbTransient
    private Vendor vendor;

    @Column(nullable = false)
    private String type; // e.g., Certificate of Origin, Safety Data Sheet

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public ComplianceDocument() {}

    public Long getId() { return id; }
    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
