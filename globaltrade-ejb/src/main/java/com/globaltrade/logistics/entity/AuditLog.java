package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * JPA entity representing an immutable audit log entry.
 *
 * <h2>Persistence Strategy</h2>
 * <ul>
 *   <li>Written by {@code AuditServiceBean.recordEvent()} in a
 *       {@code REQUIRES_NEW} transaction so the record survives
 *       even if the calling business transaction rolls back.</li>
 *   <li>Only INSERT operations are permitted — no UPDATE, no DELETE.
 *       The {@code @PreUpdate} callback throws an exception to
 *       enforce this at the application level.</li>
 *   <li>Indexed on {@code callerUsername}, {@code action},
 *       {@code status}, and {@code timestamp} for efficient
 *       regulatory and compliance queries.</li>
 * </ul>
 *
 * <h2>Retention Policy</h2>
 * Rows older than 365 days should be archived, not deleted.
 * Customs and international trade regulations require audit records
 * to be retained for a minimum of 5 years in some jurisdictions.
 */
@Entity
@Table(
    name = "audit_logs",
    indexes = {
        @Index(name = "idx_audit_caller",    columnList = "caller_username"),
        @Index(name = "idx_audit_action",    columnList = "action"),
        @Index(name = "idx_audit_status",    columnList = "status"),
        @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
        @Index(name = "idx_audit_entity",    columnList = "entity_type, entity_id")
    }
)
@NamedQueries({
    @NamedQuery(
        name  = AuditLog.FIND_BY_CALLER,
        query = "SELECT a FROM AuditLog a WHERE a.callerUsername = :caller ORDER BY a.timestamp DESC"
    ),
    @NamedQuery(
        name  = AuditLog.FIND_BY_ENTITY,
        query = "SELECT a FROM AuditLog a WHERE a.entityType = :type AND a.entityId = :entityId ORDER BY a.timestamp DESC"
    ),
    @NamedQuery(
        name  = AuditLog.FIND_FAILURES_SINCE,
        query = "SELECT a FROM AuditLog a WHERE a.status = 'FAILED' AND a.timestamp >= :since ORDER BY a.timestamp DESC"
    )
})
public class AuditLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String FIND_BY_CALLER      = "AuditLog.findByCaller";
    public static final String FIND_BY_ENTITY      = "AuditLog.findByEntity";
    public static final String FIND_FAILURES_SINCE = "AuditLog.findFailuresSince";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Fully-qualified action: e.g. {@code UserServiceBean.createUser} or
     * logical label like {@code SHIPMENT_STATUS_OVERRIDE}.
     */
    @Column(name = "action", nullable = false, length = 255)
    private String action;

    /**
     * Username of the authenticated caller, or "SYSTEM" for automated operations,
     * or "ANONYMOUS" for unauthenticated attempts.
     */
    @Column(name = "caller_username", length = 50)
    private String callerUsername;

    /**
     * Outcome: {@code SUCCESS}, {@code FAILED}, or {@code ERROR}.
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /** Entity class affected by the operation (e.g. "User", "Shipment"). */
    @Column(name = "entity_type", length = 100)
    private String entityType;

    /** String representation of the affected entity's primary key. */
    @Column(name = "entity_id", length = 100)
    private String entityId;

    /** Error message, supplementary detail, or truncated payload for context. */
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    /** Method execution time in milliseconds (from PerformanceMonitorInterceptor). */
    @Column(name = "duration_ms")
    private Long durationMs;

    /** Client IP address (IPv4 or IPv6, max 45 chars). */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /** UTC timestamp of the audit event — set at entity creation, never modified. */
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    // ── Lifecycle ──

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Audit records are immutable — reject any update attempt
        throw new UnsupportedOperationException(
            "AuditLog records are immutable. Attempted to update log ID: " + id
        );
    }

    // ── Constructor ──

    protected AuditLog() {
        // JPA
    }

    /** Builder-style factory method for creating audit entries. */
    public static AuditLog of(String action, String caller, String status) {
        AuditLog log = new AuditLog();
        log.action = action;
        log.callerUsername = caller;
        log.status = status;
        log.timestamp = LocalDateTime.now();
        return log;
    }

    // ── Getters ──

    public Long getId() { return id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getCallerUsername() { return callerUsername; }
    public void setCallerUsername(String callerUsername) { this.callerUsername = callerUsername; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "AuditLog{id=" + id
             + ", action='" + action + '\''
             + ", caller='" + callerUsername + '\''
             + ", status='" + status + '\''
             + ", timestamp=" + timestamp + '}';
    }
}
