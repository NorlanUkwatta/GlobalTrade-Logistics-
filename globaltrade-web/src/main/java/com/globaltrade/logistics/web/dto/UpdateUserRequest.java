package com.globaltrade.logistics.web.dto;

/**
 * Request payload for updating an existing user's profile fields (Admin only).
 * Does NOT allow changing username, role, or password via this DTO
 * (use dedicated endpoints for role changes and password resets).
 *
 * @param email      updated email address
 * @param fullName   updated display name
 * @param vendorId   updated vendor isolation ID (for VENDOR_REP adjustments)
 * @param customerId updated customer isolation ID (for CUSTOMER adjustments)
 */
public record UpdateUserRequest(
    String email,
    String fullName,
    Long   vendorId,
    Long   customerId
) {
}
