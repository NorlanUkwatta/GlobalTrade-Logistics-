package com.globaltrade.logistics.web.dto;

import com.globaltrade.logistics.entity.User;
import java.time.LocalDateTime;

/**
 * User data transfer object. Returned by all user management API endpoints.
 *
 * <p><b>NEVER includes passwordHash.</b> The password hash is an internal
 * implementation detail and must never cross the API boundary.</p>
 *
 * @param id                platform-assigned user ID
 * @param username          unique username (lowercase)
 * @param email             contact email address
 * @param fullName          display name
 * @param role              the user's security role (enum name as string)
 * @param active            whether the account is active
 * @param suspended         whether the account is suspended
 * @param suspensionReason  reason for suspension (null if not suspended)
 * @param vendorId          vendor isolation ID (non-null for VENDOR_REP role)
 * @param customerId        customer isolation ID (non-null for CUSTOMER role)
 * @param createdAt         account creation timestamp
 * @param lastLogin         most recent login timestamp (null if never logged in)
 * @param createdBy         username of the administrator who created this account
 */
public record UserDTO(
    Long          id,
    String        username,
    String        email,
    String        fullName,
    String        role,
    boolean       active,
    boolean       suspended,
    String        suspensionReason,
    Long          vendorId,
    Long          customerId,
    LocalDateTime createdAt,
    LocalDateTime lastLogin,
    String        createdBy
) {
    /**
     * Factory method to convert a {@link User} entity to a safe DTO.
     * Explicitly excludes {@code passwordHash}.
     */
    public static UserDTO from(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().name(),
            user.isActive(),
            user.isSuspended(),
            user.getSuspensionReason(),
            user.getVendorId(),
            user.getCustomerId(),
            user.getCreatedAt(),
            user.getLastLogin(),
            user.getCreatedBy()
        );
    }
}
