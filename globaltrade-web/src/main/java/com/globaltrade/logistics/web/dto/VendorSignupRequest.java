package com.globaltrade.logistics.web.dto;

public record VendorSignupRequest(
    String username,
    String password,
    String email,
    String fullName,
    String companyName,
    String phone
) {}
