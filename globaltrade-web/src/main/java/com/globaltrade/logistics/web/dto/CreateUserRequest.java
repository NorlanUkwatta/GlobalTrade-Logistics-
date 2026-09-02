package com.globaltrade.logistics.web.dto;

/**
 * Request payload for creating a new user account (Admin only).
 *
 * @param username    unique username (3-50 chars, lowercase preferred)
 * @param email       valid email address
 * @param fullName    user's full display name
 * @param password    plain-text password (immediately hashed, never stored/logged)
 * @param role        target role: ADMIN | LOGISTICS_COORD | WAREHOUSE_MGR |
 *                    VENDOR_REP | CUSTOMS_AGENT | CUSTOMER
 * @param vendorId    required when role = VENDOR_REP (enforces vendor data isolation)
 * @param customerId  required when role = CUSTOMER (enforces customer data isolation)
 */
public record CreateUserRequest(
    String username,
    String email,
    String fullName,
    String password,
    String role,
    Long   vendorId,
    Long   customerId
) {
}
