package com.globaltrade.logistics.exception;

/**
 * Thrown when an authenticated user whose account has been suspended
 * attempts to log in or perform an operation.
 *
 * <p>HTTP mapping: <b>403 Forbidden</b></p>
 *
 * <h2>Suspension vs. Deactivation</h2>
 * <ul>
 *   <li>A <em>suspended</em> account ({@code active=true, suspended=true}) is
 *       temporarily locked. The admin can re-activate it. This is the appropriate
 *       action for: performance issues, investigation, temporary revocation.</li>
 *   <li>A <em>deactivated</em> account ({@code active=false}) is permanently
 *       disabled. User records are never deleted to preserve audit history.</li>
 * </ul>
 *
 * <h2>Security Consideration</h2>
 * Suspension reason is returned to the user in the error response so they
 * know to contact their administrator. This is intentional — no sensitive
 * information is included in the reason field by design.
 */
public class AccountSuspendedException extends LogisticsApplicationException {

    private final String suspensionReason;

    public AccountSuspendedException(String username, String suspensionReason) {
        super(
            "Account [" + username + "] has been suspended. Reason: " + suspensionReason,
            "ACCOUNT_SUSPENDED",
            403
        );
        this.suspensionReason = suspensionReason;
    }

    public String getSuspensionReason() {
        return suspensionReason;
    }
}
