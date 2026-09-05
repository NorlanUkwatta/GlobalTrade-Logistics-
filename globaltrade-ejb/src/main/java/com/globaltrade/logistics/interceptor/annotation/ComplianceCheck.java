package com.globaltrade.logistics.interceptor.annotation;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * CDI interceptor binding for international trade compliance checking.
 *
 * <h2>Purpose</h2>
 * Validates that annotated operations comply with:
 * <ul>
 *   <li>Customs regulations and filing deadlines</li>
 *   <li>Harmonized System (HS) commodity code validity</li>
 *   <li>Trade agreement eligibility (free trade zones, preferential tariffs)</li>
 *   <li>Import/export embargo and restriction checks</li>
 *   <li>Required documentation completeness for cross-border shipments</li>
 * </ul>
 *
 * <h2>Priority: 4000 (APPLICATION + 3000)</h2>
 * Runs LAST in the interceptor chain. Compliance checks may be expensive
 * (external regulatory system lookups) and should only execute if earlier
 * interceptors (audit, validation, performance gate) have passed.
 *
 * <h2>Key Regulatory Frameworks</h2>
 * EU Customs Code, US CBP regulations, WTO trade agreements, Incoterms 2020.
 */
@Inherited
@InterceptorBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComplianceCheck {
}
