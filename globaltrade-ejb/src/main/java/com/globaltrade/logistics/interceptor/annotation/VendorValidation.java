package com.globaltrade.logistics.interceptor.annotation;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * CDI interceptor binding for vendor data validation.
 *
 * <h2>Purpose</h2>
 * Validates incoming vendor-related payloads against:
 * <ul>
 *   <li>Required field completeness (name, country, certifications)</li>
 *   <li>Internal vendor blacklist checks</li>
 *   <li>Sanctions list screening (OFAC, EU, UN embargo lists)</li>
 *   <li>Vendor tier eligibility for the requested operation</li>
 * </ul>
 *
 * <h2>Priority: 2000 (APPLICATION + 1000)</h2>
 * Runs after {@link LogisticsAudit} so the attempted operation is logged
 * even if validation rejects it. Runs before {@link PerformanceMonitor} and
 * {@link ComplianceCheck}.
 *
 * <h2>Usage</h2>
 * Apply to EJB methods that accept vendor entity creation or update payloads.
 */
@Inherited
@InterceptorBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VendorValidation {
}
