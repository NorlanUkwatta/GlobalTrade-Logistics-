package com.globaltrade.logistics.exception;

/**
 * Thrown when authentication fails — invalid credentials, account not found,
 * or the account state prevents login.
 *
 * <p>HTTP mapping: <b>401 Unauthorized</b></p>
 *
 * <h2>Critical Security Note</h2>
 * The exception message returned to the client MUST be generic
 * (e.g. "Invalid username or password") to prevent user enumeration attacks.
 * Internal details (account not found vs. wrong password) should be logged
 * server-side only via the {@code LogisticsAuditInterceptor}.
 */
public class AuthenticationException extends LogisticsApplicationException {

    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_FAILED", 401);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, "AUTHENTICATION_FAILED", 401, cause);
    }

    /** Factory: generic message safe to return to API clients. */
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid username or password.");
    }
}
