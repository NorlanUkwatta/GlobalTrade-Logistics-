package com.globaltrade.logistics.exception;

/**
 * Thrown when an authenticated user attempts an operation they do not have
 * permission to perform based on their role.
 *
 * <p>HTTP mapping: <b>403 Forbidden</b></p>
 *
 * <h2>Relationship to @RolesAllowed</h2>
 * The EJB container and JAX-RS runtime enforce {@code @RolesAllowed} declaratively
 * and throw their own security exceptions for role violations. This exception is
 * used for <em>programmatic</em> authorization checks within business logic — for
 * example, when a {@code VENDOR_REP} attempts to access data belonging to a
 * different {@code vendor_id}, or when a {@code CUSTOMER} queries another
 * customer's tracking information.
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 *   if (!currentUser.getVendorId().equals(shipment.getVendorId())) {
 *       throw InsufficientPermissionException.vendorDataIsolation(currentUser.getUsername());
 *   }
 * }</pre>
 */
public class InsufficientPermissionException extends LogisticsApplicationException {

    public InsufficientPermissionException(String message) {
        super(message, "INSUFFICIENT_PERMISSION", 403);
    }

    /** Factory: vendor data isolation violation. */
    public static InsufficientPermissionException vendorDataIsolation(String username) {
        return new InsufficientPermissionException(
            "User [" + username + "] is not authorized to access data belonging to another vendor."
        );
    }

    /** Factory: customer data isolation violation. */
    public static InsufficientPermissionException customerDataIsolation(String username) {
        return new InsufficientPermissionException(
            "User [" + username + "] is not authorized to access tracking data belonging to another customer."
        );
    }

    /** Factory: operation not permitted for this role. */
    public static InsufficientPermissionException forOperation(String username, String operation) {
        return new InsufficientPermissionException(
            "User [" + username + "] does not have permission to perform: " + operation
        );
    }
}
