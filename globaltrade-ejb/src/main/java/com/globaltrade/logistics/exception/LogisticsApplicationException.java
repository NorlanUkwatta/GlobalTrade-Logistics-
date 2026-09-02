package com.globaltrade.logistics.exception;

import jakarta.ejb.ApplicationException;

/**
 * Abstract base for all GlobalTrade business exceptions.
 *
 * <h2>EJB ApplicationException Semantics</h2>
 * <ul>
 *   <li>{@code @ApplicationException(rollback=true)}: When this exception
 *       (or any subclass) propagates from an EJB method, the container-managed
 *       transaction is automatically rolled back.</li>
 *   <li>Unlike {@link jakarta.ejb.EJBException} (system exception), this class
 *       is NOT wrapped in a {@code RemoteException}. The caller receives the
 *       exact typed exception for precise programmatic handling.</li>
 *   <li>The JAX-RS {@code GlobalExceptionMapper} maps all subclasses to
 *       appropriate 4xx HTTP responses with structured JSON error bodies.</li>
 * </ul>
 *
 * <h2>Rollback Decision</h2>
 * {@code rollback=true} is set at the base class level. All supply-chain business
 * exceptions should roll back the transaction because a partially-completed
 * logistics operation (e.g. stock reserved but shipment not created) is more
 * dangerous than a complete rollback and retry.
 */
@ApplicationException(rollback = true, inherited = true)
public abstract class LogisticsApplicationException extends RuntimeException {

    /** HTTP status code hint for the JAX-RS exception mapper. */
    private final int httpStatusCode;

    /** Machine-readable error code for API clients (e.g. "USER_NOT_FOUND"). */
    private final String errorCode;

    protected LogisticsApplicationException(String message, String errorCode, int httpStatusCode) {
        super(message);
        this.errorCode      = errorCode;
        this.httpStatusCode = httpStatusCode;
    }

    protected LogisticsApplicationException(String message, String errorCode,
                                             int httpStatusCode, Throwable cause) {
        super(message, cause);
        this.errorCode      = errorCode;
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() { return httpStatusCode; }

    public String getErrorCode() { return errorCode; }
}
