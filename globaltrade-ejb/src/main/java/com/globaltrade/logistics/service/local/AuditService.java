package com.globaltrade.logistics.service.local;

import com.globaltrade.logistics.entity.AuditLog;
import jakarta.ejb.Local;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Local EJB interface for audit trail management.
 *
 * <h2>Why REQUIRES_NEW?</h2>
 * All methods in this service use {@code @TransactionAttribute(REQUIRES_NEW)}.
 * This ensures audit records are committed to the database even when the
 * calling business transaction rolls back (e.g. a failed payment does not
 * erase the audit evidence that the payment was attempted).
 *
 * <h2>Caller Note</h2>
 * Do NOT annotate {@code AuditServiceBean} with {@link
 * com.globaltrade.logistics.interceptor.annotation.LogisticsAudit} —
 * doing so would create infinite recursive interception.
 */
@Local
public interface AuditService {

    /**
     * Persists an audit log record in its own REQUIRES_NEW transaction.
     * @param log a fully-populated {@link AuditLog} instance (use {@code AuditLog.of(...)})
     */
    void recordEvent(AuditLog log);

    /**
     * Queries the audit trail for a specific caller.
     * @param callerUsername the username to search for
     * @param maxResults     maximum number of records to return
     */
    List<AuditLog> findByCaller(String callerUsername, int maxResults);

    /**
     * Queries all failed operations since a given timestamp.
     * Used by the monitoring dashboard to surface recent errors.
     */
    List<AuditLog> findFailuresSince(LocalDateTime since);
    List<AuditLog> findAll(int maxResults);

    /**
     * Queries the audit trail for a specific entity (e.g. all events for User#42).
     */
    List<AuditLog> findByEntity(String entityType, String entityId);
}
