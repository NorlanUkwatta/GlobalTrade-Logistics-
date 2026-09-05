package com.globaltrade.logistics.web.dto;

/**
 * Request payload for password change operations.
 *
 * @param currentPassword the user's existing password (verified before hashing the new one)
 * @param newPassword     the desired new password (must meet the platform password policy:
 *                        ≥8 chars, uppercase, lowercase, digit, special character)
 */
public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
