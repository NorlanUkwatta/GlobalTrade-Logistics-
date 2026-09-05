package com.globaltrade.logistics.interceptor.annotation;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * CDI interceptor binding for supply chain audit logging.
 *
 * <h2>Purpose</h2>
 * Apply this annotation to any EJB or CDI bean class/method where a permanent,
 * legally binding audit trail must be created. The {@link
 * com.globaltrade.logistics.interceptor.LogisticsAuditInterceptor} intercepts
 * ALL annotated invocations and writes an {@link
 * com.globaltrade.logistics.entity.AuditLog} record in its own
 * {@code REQUIRES_NEW} transaction.
 *
 * <h2>Priority: 1000 (APPLICATION + 0)</h2>
 * Runs FIRST in the interceptor chain. This guarantees that even if a later
 * interceptor (e.g. validation) throws an exception, the audit entry for the
 * attempted invocation is still recorded.
 *
 * <h2>Application Targets</h2>
 * <ul>
 *   <li>All methods in {@code UserServiceBean}</li>
 *   <li>All customs declaration submissions</li>
 *   <li>All security-sensitive operations (password changes, role assignments)</li>
 *   <li>Customs Cleared / Customs Hold status updates</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 *   @Stateless
 *   @LogisticsAudit          // class-level: audits ALL methods
 *   public class UserServiceBean implements UserService { ... }
 *
 *   @Stateless
 *   public class ShipmentServiceBean implements ShipmentService {
 *       @LogisticsAudit     // method-level: audit only this operation
 *       public void overrideRoute(...) { ... }
 *   }
 * }</pre>
 */
@Inherited
@InterceptorBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogisticsAudit {
}
