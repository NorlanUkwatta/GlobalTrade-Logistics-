package com.globaltrade.logistics.web.dto;

public record VendorSignupRequest(
    String username,
    String password,
    String email,
    String fullName,
    String companyName,
    String phone,
    String registrationNumber,
    String headquartersAddress,
    Long commodityCategoryId,
    Integer standardLeadTimeDays,
    String pickupAddressLine1,
    String pickupAddressLine2,
    String pickupCity,
    String pickupState,
    String pickupPostalCode,
    String pickupCountry
) {}
