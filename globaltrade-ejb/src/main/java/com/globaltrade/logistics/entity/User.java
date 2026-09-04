package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity representing a platform user in the GlobalTrade Logistics system.
 *
 * <h2>Role-Based Data Isolation</h2>
 * <ul>
 *   <li>{@link UserRole#VENDOR_REP}: {@code vendorId} must be non-null.
 *       The application layer enforces that they only access data where
 *       {@code shipment.vendorId == user.vendorId}.</li>
 *   <li>{@link UserRole#CUSTOMER}: {@code customerId} must be non-null.
 *       The application layer restricts tracking to their {@code customerId} scope.</li>
 * </ul>
 *
 * <h2>Password Security</h2>
 * The {@code passwordHash} field stores a BCrypt hash (12 rounds).
 * The plain-text password is NEVER stored and never returned in any DTO.
 *
 * <h2>Account Lifecycle</h2>
 * <pre>
 *   ACTIVE (active=true, suspended=false)
 *     ↓ AdminBootstrapBean.suspend()
 *   SUSPENDED (active=true, suspended=true, suspensionReason != null)
 *     ↓ AdminBootstrapBean.activate()
 *   ACTIVE
 * </pre>
 * Deletion is not permitted — use suspend to preserve audit history.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uq_users_email",    columnNames = "email")
    },
    indexes = {
        @Index(name = "idx_users_role",        columnList = "role"),
        @Index(name = "idx_users_vendor_id",   columnList = "vendor_id"),
        @Index(name = "idx_users_customer_id", columnList = "customer_id")
    }
)
@NamedQueries({
    @NamedQuery(
        name  = User.FIND_BY_USERNAME,
        query = "SELECT u FROM User u WHERE u.username = :username"
    ),
    @NamedQuery(
        name  = User.FIND_ALL_ACTIVE,
        query = "SELECT u FROM User u WHERE u.active = true ORDER BY u.fullName"
    ),
    @NamedQuery(
        name  = User.COUNT_BY_ROLE,
        query = "SELECT COUNT(u) FROM User u WHERE u.role = :role"
    ),
    @NamedQuery(
        name  = User.FIND_BY_VENDOR_ID,
        query = "SELECT u FROM User u WHERE u.vendorId = :vendorId AND u.role = 'VENDOR_REP'"
    )
})
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── Named query constants ──
    public static final String FIND_BY_USERNAME = "User.findByUsername";
    public static final String FIND_ALL_ACTIVE  = "User.findAllActive";
    public static final String COUNT_BY_ROLE    = "User.countByRole";
    public static final String FIND_BY_VENDOR_ID = "User.findByVendorId";

    // ── Primary Key ──
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // ── Identity ──
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    /**
     * BCrypt hash (12 rounds). Never exposed outside this entity or the IdentityStore.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    @jakarta.json.bind.annotation.JsonbTransient
    private String passwordHash;

    // ── Role & Isolation ──
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    /**
     * Non-null only for {@link UserRole#VENDOR_REP}.
     * Enforces vendor data isolation: this user can only access data
     * belonging to this vendor ID.
     */
    @Column(name = "vendor_id")
    private Long vendorId;

    /**
     * Non-null only for {@link UserRole#CUSTOMER}.
     * Enforces customer data isolation: this user sees only their own
     * high-level shipment milestones.
     */
    @Column(name = "customer_id")
    private Long customerId;

    // ── Account State ──
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "suspended", nullable = false)
    private boolean suspended = false;

    /**
     * Required when {@code suspended = true}. Provides regulatory-grade
     * reason for account deactivation for audit trail purposes.
     */
    @Column(name = "suspension_reason", columnDefinition = "TEXT")
    private String suspensionReason;

    // ── Timestamps ──
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * Username of the administrator who created this account.
     * Value is "SYSTEM" for the bootstrap admin account.
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    // ── Lifecycle Callbacks ──

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Constructors ──

    public User() {
        // JPA requires no-arg constructor
    }

    // ── Getters & Setters ──

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }

    public String getSuspensionReason() { return suspensionReason; }
    public void setSuspensionReason(String suspensionReason) { this.suspensionReason = suspensionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    // ── Business helpers ──

    /**
     * Returns {@code true} if this account can authenticate.
     * An account is loginable only when it is active AND not suspended.
     */
    public boolean isLoginable() {
        return active && !suspended;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id
             + ", username='" + username + '\''
             + ", role=" + role
             + ", active=" + active
             + ", suspended=" + suspended + '}';
    }
}
