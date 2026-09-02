package com.globaltrade.logistics.interceptor;

import com.globaltrade.logistics.entity.AuditLog;
import com.globaltrade.logistics.interceptor.annotation.LogisticsAudit;
import com.globaltrade.logistics.service.local.AuditService;
import jakarta.annotation.Priority;
import jakarta.ejb.EJBContext;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * CDI Interceptor: Supply Chain Audit Trail
 *
 * <h2>Responsibility</h2>
 * Creates a permanent, legally compliant audit record for every invocation of
 * a method annotated (or on a class annotated) with {@link LogisticsAudit}.
 *
 * <h2>Priority: APPLICATION + 1000 = 1100</h2>
 * Executes FIRST so that even if a later interceptor (validation, compliance)
 * or the business method itself throws an exception, the audit entry is still
 * committed. The audit uses a {@code REQUIRES_NEW} transaction in
 * {@link AuditService} — it survives the calling transaction's rollback.
 *
 * <h2>Log4j2 Structured Logging</h2>
 * Uses a dedicated {@code AUDIT} logger (configured in log4j2.xml) that routes
 * to a separate rolling file with 365-day retention.
 * {@link ThreadContext} (MDC equivalent) is set per invocation for correlation.
 *
 * <h2>Caller Resolution</h2>
 * Caller identity is resolved in priority order:
 * <ol>
 *   <li>{@code EJBContext.getCallerPrincipal().getName()} (EJB security context)</li>
 *   <li>"SYSTEM" fallback for startup/timer operations with no authenticated caller</li>
 * </ol>
 */
@Interceptor
@LogisticsAudit
@Priority(Interceptor.Priority.APPLICATION + 1000)
public class LogisticsAuditInterceptor implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Dedicated audit logger — routes to globaltrade-audit.log (365-day retention). */
    private static final Logger AUDIT_LOG = LogManager.getLogger("com.globaltrade.logistics.AUDIT");

    /** General application logger. */
    private static final Logger LOG = LogManager.getLogger(LogisticsAuditInterceptor.class);

    /**
     * AuditService: uses REQUIRES_NEW so the audit record commits independently
     * of the calling transaction. Injected as CDI bean (AuditServiceBean is @Stateless
     * which is also a CDI bean in Jakarta EE 10).
     */
    @Inject
    private AuditService auditService;

    // EJBContext is looked up dynamically in resolveCallerName() to avoid
    // strict injection failures in CDI interceptors across different containers.

    // ─── Core Interceptor Logic ──────────────────────────────────────────────────────────

    @AroundInvoke
    public Object auditBusinessMethod(InvocationContext ctx) throws Exception {
        String methodLabel = buildMethodLabel(ctx);
        String caller      = resolveCallerName();
        long   startNs     = System.nanoTime();

        // Set Log4j2 ThreadContext (MDC) for log correlation
        ThreadContext.put("caller",    caller);
        ThreadContext.put("operation", methodLabel);
        ThreadContext.put("timestamp", LocalDateTime.now().toString());

        try {
            Object result = ctx.proceed();

            long durationMs = toMillis(System.nanoTime() - startNs);

            AUDIT_LOG.info("[SUCCESS] operation={} | caller={} | duration={}ms",
                methodLabel, caller, durationMs);

            // Persist audit record asynchronously in its own transaction
            persistAuditRecord(methodLabel, caller, "SUCCESS", null, durationMs,
                extractEntityInfo(ctx));

            return result;

        } catch (Exception ex) {
            long durationMs = toMillis(System.nanoTime() - startNs);

            AUDIT_LOG.warn("[FAILED] operation={} | caller={} | duration={}ms | error={}",
                methodLabel, caller, durationMs, ex.getMessage());

            persistAuditRecord(methodLabel, caller, "FAILED",
                ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                durationMs, extractEntityInfo(ctx));

            throw ex; // never swallow — re-throw for EJB container to handle

        } finally {
            ThreadContext.clearAll();
        }
    }

    // ── Private Helpers ────────────────────────────────────────────────────

    /**
     * Builds a human-readable operation label: {@code ClassName.methodName}.
     */
    private String buildMethodLabel(InvocationContext ctx) {
        return ctx.getMethod().getDeclaringClass().getSimpleName()
             + "."
             + ctx.getMethod().getName();
    }

    private String resolveCallerName() {
        try {
            javax.naming.InitialContext ic = new javax.naming.InitialContext();
            EJBContext ctx = (EJBContext) ic.lookup("java:comp/EJBContext");
            java.security.Principal principal = ctx.getCallerPrincipal();
            if (principal != null && !"ANONYMOUS".equalsIgnoreCase(principal.getName())) {
                return principal.getName();
            }
        } catch (Exception e) {
            LOG.trace("Could not resolve caller principal (not in EJB context or unauthenticated): {}", e.getMessage());
        }
        return "SYSTEM";
    }

    /**
     * Extracts a simple entity info string from method parameters for the audit record.
     * Looks for a Long id or String username parameter as the entity identifier.
     */
    private String[] extractEntityInfo(InvocationContext ctx) {
        Object[] params = ctx.getParameters();
        if (params == null || params.length == 0) return null;

        for (Object param : params) {
            if (param instanceof Long id) {
                return new String[]{"Entity", String.valueOf(id)};
            }
        }
        return null;
    }

    /**
     * Persists the audit record via AuditService (REQUIRES_NEW transaction).
     * Any failure to persist the audit record is logged but does NOT affect
     * the calling business method's result.
     */
    private void persistAuditRecord(String action, String caller, String status,
                                     String details, long durationMs,
                                     String[] entityInfo) {
        try {
            AuditLog log = AuditLog.of(action, caller, status);
            log.setDetails(details);
            log.setDurationMs(durationMs);
            if (entityInfo != null && entityInfo.length == 2) {
                log.setEntityType(entityInfo[0]);
                log.setEntityId(entityInfo[1]);
            }
            auditService.recordEvent(log);
        } catch (Exception e) {
            // Audit persistence failure must never propagate to the caller
            LOG.error("CRITICAL: Failed to persist audit record for [{}] by [{}]: {}",
                action, caller, e.getMessage(), e);
        }
    }

    private long toMillis(long nanos) {
        return nanos / 1_000_000L;
    }
}
