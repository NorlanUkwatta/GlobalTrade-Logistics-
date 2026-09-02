package com.globaltrade.logistics.web.dto;

public record CustomerSignupRequest(
    String username,
    String password,
    String email,
    String fullName,
    String companyName,
    String countryCode
) {}
