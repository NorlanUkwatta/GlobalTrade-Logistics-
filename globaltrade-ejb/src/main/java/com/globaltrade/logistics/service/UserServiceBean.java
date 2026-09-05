package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.Customer;
import com.globaltrade.logistics.entity.Vendor;
import com.globaltrade.logistics.entity.User;
import com.globaltrade.logistics.entity.UserRole;
import com.globaltrade.logistics.exception.*;
import com.globaltrade.logistics.interceptor.annotation.LogisticsAudit;
import com.globaltrade.logistics.interceptor.annotation.PerformanceMonitor;
import com.globaltrade.logistics.service.local.CustomerService;
import com.globaltrade.logistics.service.local.VendorService;
import com.globaltrade.logistics.service.local.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Stateless EJB for user account lifecycle management.
 *
 * <h2>Interceptor Chain Applied</h2>
 * <ul>
 *   <li>{@code @LogisticsAudit} (class-level, Priority 1100): every method
 *       invocation produces an immutable audit record in a REQUIRES_NEW
 *       transaction - legally required for account management actions.</li>
 *   <li>{@code @PerformanceMonitor} (class-level, Priority 3100): alerts if
 *       user management operations exceed the 2s SLA.</li>
 * </ul>
 *
 * <h2>Transaction Strategy (CMT)</h2>
 * <ul>
 *   <li>Write methods: {@code REQUIRED} - participates in or starts a TX.</li>
 *   <li>{@code updateLastLogin}: {@code REQUIRES_NEW} - committed independently
 *       so a downstream failure doesn't erase the login timestamp.</li>
 *   <li>{@code findByUsernameForAuth}: {@code NOT_SUPPORTED} - no TX overhead
 *       for a lightweight pre-auth lookup.</li>
 *   <li>Read methods: {@code SUPPORTS} - no TX started if none active.</li>
 * </ul>
 *
 * <h2>Password Policy</h2>
 * BCrypt (12 rounds) is used for all password hashing. Minimum requirements:
 * 8 characters, at least one uppercase, one lowercase, one digit, one special char.
 */
@Stateless
@LogisticsAudit
@PerformanceMonitor
public class UserServiceBean implements UserService {

    private static final Logger LOG = LogManager.getLogger(UserServiceBean.class);

    /** BCrypt cost factor - 12 is the recommended production minimum. */
    private static final int BCRYPT_ROUNDS = 12;

    /** Minimum password length enforced before hashing. */
    private static final int MIN_PASSWORD_LENGTH = 8;

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @EJB
    private CustomerService customerService;

    @EJB
    private VendorService vendorService;

    // - Authentication Support -

    /**
     * Used ONLY by {@code LogisticsIdentityStore} during authentication.
     * Returns the full entity including password hash.
     * MUST be @PermitAll - called before any principal is established.
     */
    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public User findByUsernameForAuth(String username) {
        if (username == null || username.isBlank()) return null;
        try {
            return em.createNamedQuery(User.FIND_BY_USERNAME, User.class)
                     .setParameter("username", username.trim().toLowerCase())
                     .getSingleResult();
        } catch (NoResultException e) {
            return null; // Caller (IdentityStore) handles null -> INVALID_RESULT
        }
    }

    // - User Creation (ADMIN only)

    @Override
    @RolesAllowed("ADMIN")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User createUser(String username, String email, String fullName,
                           String rawPassword, UserRole role,
                           Long vendorId, Long customerId, String createdBy) {

        // Normalise inputs
        String normUsername = username.trim().toLowerCase();
        String normEmail    = email.trim().toLowerCase();

        // Validate uniqueness before persist (business-layer check)
        checkUsernameAvailable(normUsername);
        checkEmailAvailable(normEmail);

        // Validate password policy
        validatePasswordPolicy(rawPassword);

        // Role-specific field requirements
        if (role == UserRole.VENDOR_REP && vendorId == null) {
            throw new LogisticsApplicationException(
                "vendorId is required when creating a VENDOR_REP account.", "VALIDATION_ERROR", 400) {};
        }
        if (role == UserRole.CUSTOMER && customerId == null) {
            throw new LogisticsApplicationException(
                "customerId is required when creating a CUSTOMER account.", "VALIDATION_ERROR", 400) {};
        }

        // Build entity
        User user = new User();
        user.setUsername(normUsername);
        user.setEmail(normEmail);
        user.setFullName(fullName.trim());
        user.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_ROUNDS)));
        user.setRole(role);
        user.setVendorId(vendorId);
        user.setCustomerId(customerId);
        user.setActive(true);
        user.setSuspended(false);
        user.setCreatedBy(createdBy);

        em.persist(user);
        em.flush(); // get the generated ID

        LOG.info("User created: username={}, role={}, createdBy={}", normUsername, role, createdBy);
        return user;
    }

    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User registerCustomer(String username, String email, String fullName,
                                 String rawPassword, String companyName, String countryCode) {

        String normUsername = username.trim().toLowerCase();
        String normEmail    = email.trim().toLowerCase();

        checkUsernameAvailable(normUsername);
        checkEmailAvailable(normEmail);
        validatePasswordPolicy(rawPassword);

        // Create Customer record first
        String customerCode = "CUST-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Customer customer = customerService.createCustomer(customerCode, companyName, countryCode);

        // Build User entity
        User user = new User();
        user.setUsername(normUsername);
        user.setEmail(normEmail);
        user.setFullName(fullName.trim());
        user.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_ROUNDS)));
        user.setRole(UserRole.CUSTOMER);
        user.setCustomerId(customer.getId());
        user.setActive(true);
        user.setSuspended(false);
        user.setCreatedBy("SELF-SIGNUP");

        em.persist(user);
        em.flush();

        LOG.info("Customer registered: username={}, company={}, customerId={}", 
                 normUsername, companyName, customer.getId());
        return user;
    }

    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User registerVendor(String username, String email, String fullName, String rawPassword, String companyName, String phone, String registrationNumber, String headquartersAddress, Long commodityCategoryId, Integer standardLeadTimeDays, String pickupAddressLine1, String pickupAddressLine2, String pickupCity, String pickupState, String pickupPostalCode, String pickupCountry) {

        String normUsername = username.trim().toLowerCase();
        String normEmail    = email.trim().toLowerCase();

        checkUsernameAvailable(normUsername);
        checkEmailAvailable(normEmail);
        validatePasswordPolicy(rawPassword);

        // Create Vendor record first
        Vendor vendor = vendorService.createVendor(companyName, fullName.trim(), normEmail, phone, registrationNumber, headquartersAddress, commodityCategoryId, standardLeadTimeDays, pickupAddressLine1, pickupAddressLine2, pickupCity, pickupState, pickupPostalCode, pickupCountry);

        // Build User entity
        User user = new User();
        user.setUsername(normUsername);
        user.setEmail(normEmail);
        user.setFullName(fullName.trim());
        user.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_ROUNDS)));
        user.setRole(UserRole.VENDOR_REP);
        user.setVendorId(vendor.getId());
        user.setActive(true);
        user.setSuspended(false);
        user.setCreatedBy("SELF-SIGNUP-VENDOR");

        em.persist(user);
        em.flush();

        LOG.info("Vendor registered: username={}, company={}, vendorId={}", 
                 normUsername, companyName, vendor.getId());
        return user;
    }

    // - User Update (ADMIN only) -

    @Override
    @RolesAllowed("ADMIN")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User updateUser(Long userId, String email, String fullName,
                           Long vendorId, Long customerId, String updatedBy) {
        User user = findById(userId); // throws UserNotFoundException if absent

        String normEmail = email.trim().toLowerCase();
        // Check email uniqueness only if it's actually changing
        if (!normEmail.equals(user.getEmail())) {
            checkEmailAvailable(normEmail);
        }

        user.setEmail(normEmail);
        user.setFullName(fullName.trim());
        user.setVendorId(vendorId);
        user.setCustomerId(customerId);

        LOG.info("User updated: id={}, updatedBy={}", userId, updatedBy);
        return user; // JPA dirty-check will persist on TX commit
    }

    // - Account Suspension / Activation (ADMIN only) -

    @Override
    @RolesAllowed("ADMIN")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User suspendUser(Long userId, String reason, String suspendedBy) {
        User user = findById(userId);

        if (user.isSuspended()) {
            LOG.warn("Attempted to suspend already-suspended user: id={}", userId);
        }

        user.setSuspended(true);
        user.setSuspensionReason(reason);

        LOG.warn("[SECURITY] User account SUSPENDED: id={}, username={}, reason='{}', by={}",
            userId, user.getUsername(), reason, suspendedBy);
        return user;
    }

    @Override
    @RolesAllowed("ADMIN")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User activateUser(Long userId, String activatedBy) {
        User user = findById(userId);
        user.setSuspended(false);
        user.setSuspensionReason(null);
        user.setActive(true);

        LOG.info("[SECURITY] User account RE-ACTIVATED: id={}, username={}, by={}",
            userId, user.getUsername(), activatedBy);
        return user;
    }

    // - Password Change (any authenticated user for their own account) -

    @Override
    @RolesAllowed({"ADMIN", "LOGISTICS_COORD", "WAREHOUSE_MGR", "VENDOR_REP", "CUSTOMS_AGENT", "CUSTOMER"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void changePassword(Long userId, String currentPassword,
                               String newPassword, String callerUsername) {
        User user = findById(userId);

        // Verify current password - use generic error message (prevent enumeration)
        if (!BCrypt.checkpw(currentPassword, user.getPasswordHash())) {
            throw AuthenticationException.invalidCredentials();
        }

        validatePasswordPolicy(newPassword);
        user.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt(BCRYPT_ROUNDS)));

        LOG.info("[SECURITY] Password changed for user id={} by caller={}", userId, callerUsername);
    }

    // - Read Operations -

    @Override
    @RolesAllowed({"ADMIN", "LOGISTICS_COORD", "WAREHOUSE_MGR", "VENDOR_REP", "CUSTOMS_AGENT", "CUSTOMER"})
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public User findById(Long userId) {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw UserNotFoundException.byId(userId);
        }
        return user;
    }

    @Override
    @RolesAllowed("ADMIN")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<User> listAll() {
        return em.createNamedQuery(User.FIND_ALL_ACTIVE, User.class).getResultList();
    }

    @Override
    @RolesAllowed({"ADMIN", "LOGISTICS_COORD"})
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<User> listByRole(UserRole role) {
        return em.createQuery("SELECT u FROM User u WHERE u.role = :role ORDER BY u.fullName", User.class)
                 .setParameter("role", role)
                 .getResultList();
    }

    // - Last Login Update (REQUIRES_NEW - independent commit) -

    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateLastLogin(Long userId) {
        try {
            User user = em.find(User.class, userId);
            if (user != null) {
                user.setLastLogin(LocalDateTime.now());
                em.flush();
            }
        } catch (Exception e) {
            // Non-critical - don't propagate timestamp update failure
            LOG.warn("Failed to update last login for userId={}: {}", userId, e.getMessage());
        }
    }

    // - Private Validation Helpers -

    private void checkUsernameAvailable(String username) {
        Long count = em.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
            .setParameter("username", username)
            .getSingleResult();
        if (count > 0) {
            throw UserAlreadyExistsException.forUsername(username);
        }
    }

    private void checkEmailAvailable(String email) {
        Long count = em.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
            .setParameter("email", email)
            .getSingleResult();
        if (count > 0) {
            throw UserAlreadyExistsException.forEmail(email);
        }
    }

    /**
     * Enforces the GlobalTrade password policy:
     * >= 8 characters, uppercase, lowercase, digit, special character.
     */
    private void validatePasswordPolicy(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new LogisticsApplicationException(
                "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.", "VALIDATION_ERROR", 400) {};
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new LogisticsApplicationException(
                "Password must contain at least one uppercase letter.", "VALIDATION_ERROR", 400) {};
        }
        if (!password.matches(".*[a-z].*")) {
            throw new LogisticsApplicationException(
                "Password must contain at least one lowercase letter.", "VALIDATION_ERROR", 400) {};
        }
        if (!password.matches(".*\\d.*")) {
            throw new LogisticsApplicationException(
                "Password must contain at least one digit.", "VALIDATION_ERROR", 400) {};
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new LogisticsApplicationException(
                "Password must contain at least one special character.", "VALIDATION_ERROR", 400) {};
        }
    }

    @Override
    @RolesAllowed("ADMIN")
    public String resetPassword(Long userId, String callerUsername) {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new LogisticsSystemException("USER_NOT_FOUND", "User not found");
        }
        if (user.getRole() == UserRole.ADMIN && !user.getUsername().equals(callerUsername)) {
            throw new LogisticsSystemException("UNAUTHORIZED", "Admins cannot reset other admins' passwords");
        }
        String tempPass = "Temp-" + java.util.UUID.randomUUID().toString().substring(0, 8) + "!";
        user.setPasswordHash(org.mindrot.jbcrypt.BCrypt.hashpw(tempPass, org.mindrot.jbcrypt.BCrypt.gensalt(10)));
        em.merge(user);
        LOG.info("Password reset for user [{}] by admin [{}]", user.getUsername(), callerUsername);
        return tempPass;
    }
}

