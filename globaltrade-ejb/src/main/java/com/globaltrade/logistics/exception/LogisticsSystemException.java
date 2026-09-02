package com.globaltrade.logistics.exception;

import jakarta.ejb.EJBException;

/**
 * Represents infrastructure / system-level failures in the logistics platform.
 *
 * <h2>System Exception vs Application Exception</h2>
 * <ul>
 *   <li><b>System exceptions</b> ({@link EJBException} and subclasses) indicate
 *       unexpected failures: database connectivity loss, JTA coordinator failure,
 *       external carrier API timeouts, JVM errors. The EJB container:
 *       <ol>
 *         <li>Marks the transaction for rollback.</li>
 *         <li>Discards the EJB bean instance (pool contamination prevention).</li>
 *         <li>Wraps and re-throws as {@link EJBException}.</li>
 *       </ol>
 *   </li>
 *   <li>This class wraps the root cause in an {@link EJBException} so the
 *       container handles cleanup correctly, while also exposing enough context
 *       for the JAX-RS exception mapper to return a 503 or 500 response.</li>
 * </ul>
 *
 * <h2>DO NOT SWALLOW</h2>
 * Never catch this exception and return silently. At minimum, log it with
 * Log4j2 at ERROR level including the full stack trace before rethrowing.
 *
 * <h2>Recovery Strategies</h2>
 * <ul>
 *   <li>Database failures → circuit breaker, retry with exponential back-off.</li>
 *   <li>Carrier API failures → queue for async retry, alert LOGISTICS_COORD.</li>
 *   <li>Persistent failures → page on-call team, activate manual fallback SOP.</li>
 * </ul>
 *
 * <p>HTTP mapping: <b>503 Service Unavailable</b> (or 500 for unexpected errors)</p>
 */
public class LogisticsSystemException extends EJBException {

    private final String component;
    private final int    httpStatusCode;

    public LogisticsSystemException(String component, String message, Throwable cause) {
        super(message, cause instanceof Exception e ? e : new RuntimeException(cause));
        this.component      = component;
        this.httpStatusCode = 503;
    }

    public LogisticsSystemException(String component, String message) {
        super(message);
        this.component      = component;
        this.httpStatusCode = 500;
    }

    public String getComponent() { return component; }

    public int getHttpStatusCode() { return httpStatusCode; }

    /** Factory: database connectivity failure. */
    public static LogisticsSystemException databaseFailure(String component, Throwable cause) {
        return new LogisticsSystemException(
            component,
            "Database operation failed in component [" + component + "]. "
            + "The system is experiencing temporary difficulties. Please retry.",
            cause
        );
    }

    /** Factory: external carrier system outage. */
    public static LogisticsSystemException carrierSystemOutage(String carrierCode, Throwable cause) {
        return new LogisticsSystemException(
            "CarrierAPI[" + carrierCode + "]",
            "Carrier system [" + carrierCode + "] is currently unavailable. "
            + "Alternative routing has been triggered.",
            cause
        );
    }
}
