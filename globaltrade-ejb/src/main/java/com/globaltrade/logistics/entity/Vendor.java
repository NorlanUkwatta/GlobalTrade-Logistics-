package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendors")
public class Vendor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, unique = true, length = 100)
    private String companyName;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "performance_score")
    private Double performanceScore = 100.0;

    @Column(name = "defect_rate")
    private Double defectRate = 0.0;

    @Column(name = "on_time_delivery_rate")
    private Double onTimeDeliveryRate = 1.0;

    @Column(name = "pickup_address_line1")
    private String pickupAddressLine1;

    @Column(name = "pickup_address_line2")
    private String pickupAddressLine2;

    @Column(name = "pickup_city")
    private String pickupCity;

    @Column(name = "pickup_state")
    private String pickupState;

    @Column(name = "pickup_postal_code")
    private String pickupPostalCode;

    @Column(name = "pickup_country")
    private String pickupCountry;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Column(name = "headquarters_address", length = 255)
    private String headquartersAddress;

    @jakarta.json.bind.annotation.JsonbTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commodity_category_id")
    private CommodityCategory commodityCategory;

    @Column(name = "standard_lead_time_days")
    private Integer standardLeadTimeDays;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Vendor() {}

    public Long getId() { return id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Double performanceScore) { this.performanceScore = performanceScore; }

    public Double getDefectRate() { return defectRate; }
    public void setDefectRate(Double defectRate) { this.defectRate = defectRate; }

    public Double getOnTimeDeliveryRate() { return onTimeDeliveryRate; }
    public void setOnTimeDeliveryRate(Double onTimeDeliveryRate) { this.onTimeDeliveryRate = onTimeDeliveryRate; }
    public String getPickupAddressLine1() { return pickupAddressLine1; }
    public void setPickupAddressLine1(String pickupAddressLine1) { this.pickupAddressLine1 = pickupAddressLine1; }
    public String getPickupAddressLine2() { return pickupAddressLine2; }
    public void setPickupAddressLine2(String pickupAddressLine2) { this.pickupAddressLine2 = pickupAddressLine2; }
    public String getPickupCity() { return pickupCity; }
    public void setPickupCity(String pickupCity) { this.pickupCity = pickupCity; }
    public String getPickupState() { return pickupState; }
    public void setPickupState(String pickupState) { this.pickupState = pickupState; }
    public String getPickupPostalCode() { return pickupPostalCode; }
    public void setPickupPostalCode(String pickupPostalCode) { this.pickupPostalCode = pickupPostalCode; }
    public String getPickupCountry() { return pickupCountry; }
    public void setPickupCountry(String pickupCountry) { this.pickupCountry = pickupCountry; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getHeadquartersAddress() { return headquartersAddress; }
    public void setHeadquartersAddress(String headquartersAddress) { this.headquartersAddress = headquartersAddress; }
    public CommodityCategory getCommodityCategory() { return commodityCategory; }
    public void setCommodityCategory(CommodityCategory commodityCategory) { this.commodityCategory = commodityCategory; }
    public Integer getStandardLeadTimeDays() { return standardLeadTimeDays; }
    public void setStandardLeadTimeDays(Integer standardLeadTimeDays) { this.standardLeadTimeDays = standardLeadTimeDays; }
}