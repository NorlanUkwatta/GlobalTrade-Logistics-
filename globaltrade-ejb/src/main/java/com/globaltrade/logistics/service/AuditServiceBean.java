package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.AuditLog;
import com.globaltrade.logistics.service.local.AuditService;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Stateless EJB implementing the audit trail persistence service.
 *
 * <h2>REQUIRES_NEW on all write methods</h2>
 * Every write always suspends the calling transaction and starts a new one.
 * This guarantees the audit record is committed even if the business TX
 * rolls back. This is the central guarantee of audit log integrity.
 *
 * <h2>DO NOT apply @LogisticsAudit here</h2>
 * Doing so would cause recursive interception:
 * LogisticsAuditInterceptor -> AuditServiceBean.recordEvent() ->
 * LogisticsAuditInterceptor -> AuditServiceBean.recordEvent() -> ...
 *
 * <h2>@PermitAll on all methods</h2>
 * The audit service is internal infrastructure called only by the
 * LogisticsAuditInterceptor and AdminBootstrapBean. It must be accessible
 * regardless of authenticated role (including during bootstrap, before
 * any principal is established).
 */
@Stateless
public class AuditServiceBean implements AuditService {

    private static final Logger LOG = LogManager.getLogger(AuditServiceBean.class);

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    // ── Write Operations (REQUIRES_NEW — survive calling TX rollback) ──────

    /**
     * Persists a single audit record in its own REQUIRES_NEW transaction.
     * This is the core guarantee of audit integrity — if the calling business
     * transaction rolls back for any reason, this record is still committed.
     */
    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void recordEvent(AuditLog log) {
        try {
            em.persist(log);
            // Flush immediately so the record is committed when this method returns
            em.flush();
        } catch (Exception e) {
            // Log to file but do NOT propagate — audit failure must never
            // cascade back and affect the business method's response to the caller
            LOG.error("Failed to persist audit record [action={}, caller={}]: {}",
                log.getAction(), log.getCallerUsername(), e.getMessage(), e);
        }
    }

    // ── Read Operations (SUPPORTS — no TX needed for read-only queries) ────

    @Override
    @RolesAllowed({"ADMIN"})
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<AuditLog> findByCaller(String callerUsername, int maxResults) {
        try {
            return em.createNamedQuery(AuditLog.FIND_BY_CALLER, AuditLog.class)
                     .setParameter("caller", callerUsername)
                     .setMaxResults(Math.min(maxResults, 500)) // cap at 500
                     .getResultList();
        } catch (Exception e) {
            LOG.error("Failed to query audit log by caller [{}]: {}", callerUsername, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @RolesAllowed({"ADMIN", "LOGISTICS_COORD"})
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<AuditLog> findFailuresSince(LocalDateTime since) {
        try {
            return em.createNamedQuery(AuditLog.FIND_FAILURES_SINCE, AuditLog.class)
                     .setParameter("since", since)
                     .setMaxResults(200)
                     .getResultList();
        } catch (Exception e) {
            LOG.error("Failed to query audit failures since [{}]: {}", since, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @RolesAllowed("ADMIN")
    public List<AuditLog> findAll(int maxResults) {
        return em.createQuery("SELECT a FROM AuditLog a ORDER BY a.timestamp DESC", AuditLog.class)
                 .setMaxResults(maxResults)
                 .getResultList();
    }

    @Override
    @RolesAllowed("ADMIN")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<AuditLog> findByEntity(String entityType, String entityId) {
        try {
            return em.createNamedQuery(AuditLog.FIND_BY_ENTITY, AuditLog.class)
                     .setParameter("type", entityType)
                     .setParameter("entityId", entityId)
                     .setMaxResults(100)
                     .getResultList();
        } catch (Exception e) {
            LOG.error("Failed to query audit log for entity [{}/{}]: {}",
                entityType, entityId, e.getMessage());
            return Collections.emptyList();
        }
    }
}
