package com.globaltrade.logistics.service.local;

import com.globaltrade.logistics.entity.User;
import com.globaltrade.logistics.entity.UserRole;
import jakarta.ejb.Local;
import java.util.List;

/**
 * Local EJB interface for user account management.
 *
 * <h2>Transaction Strategy (Container-Managed)</h2>
 * <ul>
 *   <li>Write operations ({@code createUser}, {@code updateUser},
 *       {@code suspendUser}, {@code activateUser}): {@code REQUIRED} —
 *       participates in or starts a transaction.</li>
 *   <li>Last-login update: {@code REQUIRES_NEW} — committed independently
 *       so a session creation failure doesn't roll back the login audit.</li>
 *   <li>Authentication lookup ({@code findByUsernameForAuth}): {@code NOT_SUPPORTED}
 *       — read-only, lightweight, no TX overhead needed.</li>
 *   <li>Read operations ({@code findById}, {@code listAll}): {@code SUPPORTS} —
 *       joins active TX if present, otherwise runs without one.</li>
 * </ul>
 *
 * <h2>Security on Methods</h2>
 * @RolesAllowed is declared on the bean implementation to enforce EJB security.
 */
@Local
public interface UserService {

    /**
     * Looks up a user by username for authentication purposes.
     * Returns the entity including password hash — NEVER expose to external callers.
     * This method must be @PermitAll since it's called before authentication.
     */
    User findByUsernameForAuth(String username);

    /**
     * Creates a new user account. Performs duplicate username/email checks
     * and throws {@link com.globaltrade.logistics.exception.UserAlreadyExistsException}
     * if a conflict is found.
     *
     * @param username      desired username
     * @param email         contact email
     * @param fullName      display name
     * @param rawPassword   plain-text password (immediately BCrypt-hashed, never stored)
     * @param role          the role to assign
     * @param vendorId      required when role = VENDOR_REP
     * @param customerId    required when role = CUSTOMER
     * @param createdBy     username of the creating administrator
     * @return the persisted User entity
     */
    User createUser(String username, String email, String fullName,
                    String rawPassword, UserRole role,
                    Long vendorId, Long customerId, String createdBy);

    /**
     * Registers a new customer account publicly.
     */
    User registerCustomer(String username, String email, String fullName,
                          String rawPassword, String companyName, String countryCode);

    /**
     * Registers a new vendor account publicly.
     */
    User registerVendor(String username, String email, String fullName, String rawPassword, String companyName, String phone, String registrationNumber, String headquartersAddress, Long commodityCategoryId, Integer standardLeadTimeDays, String pickupAddressLine1, String pickupAddressLine2, String pickupCity, String pickupState, String pickupPostalCode, String pickupCountry);

    /**
     * Updates mutable profile fields for an existing user.
     * Does NOT update role, username, or password (use dedicated methods).
     */
    User updateUser(Long userId, String email, String fullName,
                    Long vendorId, Long customerId, String updatedBy);

    /**
     * Suspends an account. The user will be denied login immediately.
     * The suspension reason is stored and returned to the user on login attempt.
     */
    User suspendUser(Long userId, String reason, String suspendedBy);

    /**
     * Re-activates a previously suspended account.
     */
    User activateUser(Long userId, String activatedBy);

    /**
     * Changes a user's password. Verifies the current password before updating.
     * Throws {@link com.globaltrade.logistics.exception.AuthenticationException}
     * if the current password is incorrect.
     */
    void changePassword(Long userId, String currentPassword,
                        String newPassword, String callerUsername);

    String resetPassword(Long userId, String callerUsername);

    /**
     * Finds a user by ID. Throws {@link com.globaltrade.logistics.exception.UserNotFoundException}
     * if not found.
     */
    User findById(Long userId);

    /**
     * Returns all users, ordered by full name. Admin use only.
     */
    List<User> listAll();

    /**
     * Returns all users with a specific role.
     */
    List<User> listByRole(UserRole role);

    /**
     * Updates the last_login timestamp in its own transaction.
     * Called after a successful authentication.
     */
    void updateLastLogin(Long userId);
}

