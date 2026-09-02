package com.globaltrade.logistics.interceptor;

import com.globaltrade.logistics.entity.Shipment;
import com.globaltrade.logistics.entity.User;
import com.globaltrade.logistics.exception.InsufficientPermissionException;
import com.globaltrade.logistics.interceptor.annotation.VendorIsolation;
import com.globaltrade.logistics.service.local.UserService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.security.enterprise.SecurityContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.security.Principal;

/**
 * Interceptor that enforces Multi-Tenant Data Isolation for VENDOR_REP users.
 * <p>
 * If the caller has the VENDOR_REP role, this interceptor examines method
 * arguments. If it finds a Shipment, it asserts that the Shipment belongs
 * to the Vendor ID associated with the caller's User profile.
 * </p>
 */
@Interceptor
@VendorIsolation
@Priority(Interceptor.Priority.APPLICATION + 200)
public class VendorIsolationInterceptor implements Serializable {

    private static final Logger LOG = LogManager.getLogger(VendorIsolationInterceptor.class);

    @Inject
    private SecurityContext securityContext;

    @Inject
    private UserService userService;

    @AroundInvoke
    public Object enforceVendorIsolation(InvocationContext ctx) throws Exception {
        Principal principal = securityContext.getCallerPrincipal();
        
        // If system/internal or no principal, let it pass (or fail at @RolesAllowed)
        if (principal == null || "ANONYMOUS".equalsIgnoreCase(principal.getName())) {
            return ctx.proceed();
        }

        // If Admin or other roles, skip isolation check
        if (!securityContext.isCallerInRole("VENDOR_REP")) {
            return ctx.proceed();
        }

        // Caller is a VENDOR_REP. Find their vendorId.
        String username = principal.getName();
        User caller = userService.findByUsernameForAuth(username);
        
        if (caller == null || caller.getVendorId() == null) {
            LOG.error("SECURITY: User {} is VENDOR_REP but has no vendor_id!", username);
            throw new InsufficientPermissionException("Account configuration error: No vendor association.");
        }
        
        Long callerVendorId = caller.getVendorId();

        // Check arguments for Shipment entity
        Object[] parameters = ctx.getParameters();
        if (parameters != null) {
            for (Object param : parameters) {
                if (param instanceof Shipment) {
                    Shipment shipment = (Shipment) param;
                    if (shipment.getVendor() != null && !callerVendorId.equals(shipment.getVendor().getId())) {
                        LOG.warn("SECURITY VIOLATION: Vendor {} attempted to access/modify Shipment belonging to Vendor {}.",
                            callerVendorId, shipment.getVendor().getId());
                        throw new InsufficientPermissionException("You are not authorized to manage shipments for other vendors.");
                    }
                }
            }
        }

        // Proceed with the business method
        Object result = ctx.proceed();
        
        // Also protect read operations (e.g., findById)
        if (result instanceof Shipment) {
            Shipment shipment = (Shipment) result;
            if (shipment.getVendor() != null && !callerVendorId.equals(shipment.getVendor().getId())) {
                LOG.warn("SECURITY VIOLATION (READ): Vendor {} attempted to read Shipment belonging to Vendor {}.",
                    callerVendorId, shipment.getVendor().getId());
                throw new InsufficientPermissionException("You are not authorized to view this shipment.");
            }
        }
        
        return result;
    }
}