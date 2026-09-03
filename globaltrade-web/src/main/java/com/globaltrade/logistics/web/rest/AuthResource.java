package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.User;
import com.globaltrade.logistics.exception.AccountSuspendedException;
import com.globaltrade.logistics.service.local.UserService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import com.globaltrade.logistics.web.dto.AuthRequest;
import com.globaltrade.logistics.web.dto.JwtResponse;
import com.globaltrade.logistics.web.dto.UserDTO;
import com.globaltrade.logistics.web.security.JwtUtil;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * JAX-RS resource for authentication operations.
 *
 * <h2>Endpoints</h2>
 * <ul>
 *   <li>{@code POST /api/auth/login}  - @PermitAll - credential verification + JWT issuance</li>
 *   <li>{@code POST /api/auth/logout} - @PermitAll - clears auth cookie</li>
 *   <li>{@code GET  /api/auth/me}     - any authenticated role - returns current user profile</li>
 * </ul>
 *
 * <h2>Cookie Strategy</h2>
 * On successful login, the JWT is both:
 * <ol>
 *   <li>Returned in the JSON body (for REST API / mobile clients)</li>
 *   <li>Set as an HTTP-only cookie {@code auth_token} (for browser/JSP clients)</li>
 * </ol>
 * The HTTP-only flag prevents JS from reading the cookie directly, protecting
 * against XSS attacks for the browser-based client.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class AuthResource {

    @EJB
    private com.globaltrade.logistics.service.ITOpsServiceBean itOpsService;

    private static final Logger LOG = LogManager.getLogger(AuthResource.class);
    private static final String AUTH_COOKIE_NAME = "auth_token";

    @Inject
    private IdentityStore identityStore;

    @Inject
    private JwtUtil jwtUtil;

    @EJB
    private UserService userService;

    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/reset-password")
    @PermitAll
    public Response resetPassword(java.util.Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");
        if (token == null || newPassword == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ApiResponse.error("Missing token or password")).build();
        }
        boolean success = itOpsService.resetPasswordWithToken(token, newPassword);
        if (success) {
            return Response.ok(ApiResponse.success("Password reset successful", null)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(ApiResponse.error("Invalid or expired token")).build();
        }
    }

    // -
    //  POST /api/auth/login - @PermitAll
    // -

    /**
     * Authenticates a user and returns a JWT.
     *
     * <p>On success: JWT in body + HTTP-only {@code auth_token} cookie.</p>
     * <p>On failure: 401 with generic message (user enumeration prevention).</p>
     */
    @POST
    @Path("/login")
    @PermitAll
    public Response login(AuthRequest request, @Context HttpHeaders headers) {

        if (request == null || request.username() == null || request.password() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("Username and password are required."))
                .build();
        }

        String username = request.username().trim().toLowerCase();
        String password = request.password();

        LOG.info("[AUTH] Login attempt from user: {}", username);

        // - Validate via IdentityStore -
        CredentialValidationResult result = identityStore.validate(
            new UsernamePasswordCredential(username, password)
        );

        if (result.getStatus() != CredentialValidationResult.Status.VALID) {

            // Check specifically for suspended account (to give informative message)
            User foundUser = userService.findByUsernameForAuth(username);
            if (foundUser != null && foundUser.isSuspended()) {
                LOG.warn("[AUTH] Login denied - suspended account: {}", username);
                return Response.status(Response.Status.FORBIDDEN)
                    .entity(ApiResponse.error(
                        "Your account has been suspended. Reason: "
                        + foundUser.getSuspensionReason()
                        + ". Please contact your administrator."))
                    .build();
            }

            LOG.warn("[AUTH] Authentication failed for: {}", username);
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(ApiResponse.error("Invalid username or password."))
                .build();
        }

        // - Authentication succeeded -
        String     callerName = result.getCallerPrincipal().getName();
        Set<String> roles     = result.getCallerGroups();
        String     primaryRole = roles.isEmpty() ? "" : roles.iterator().next();

        // Fetch full name for the JWT claim
        User user = userService.findByUsernameForAuth(callerName);
        String fullName = (user != null) ? user.getFullName() : callerName;

        String token = jwtUtil.generateToken(callerName, roles, fullName);

        // Update last login asynchronously (REQUIRES_NEW - won't affect this TX)
        if (user != null) {
            userService.updateLastLogin(user.getId());
        }

        LOG.info("[AUTH] Login successful: user={}, roles={}", callerName, roles);

        // - Build HTTP-only cookie -
        NewCookie authCookie = new NewCookie.Builder(AUTH_COOKIE_NAME)
            .value(token)
            .path("/")
            .maxAge((int) jwtUtil.getTokenValiditySeconds())
            .httpOnly(true)
            .sameSite(NewCookie.SameSite.LAX)
            .comment("GlobalTrade Logistics Session")
            .build();

        JwtResponse jwtResponse = new JwtResponse(
            token,
            jwtUtil.getTokenValiditySeconds(),
            roles,
            callerName,
            fullName,
            primaryRole
        );

        return Response.ok(ApiResponse.success("Login successful.", jwtResponse))
            .cookie(authCookie)
            .build();
    }

    // -
    //  POST /api/auth/logout - @PermitAll
    // -

    /**
     * Logs out the current user by clearing the auth cookie.
     * The JWT itself is stateless - it remains valid until expiry,
     * but the cookie removal prevents browser-based JSP access.
     */
    @POST
    @Path("/logout")
    @PermitAll
    public Response logout() {
        // Clear auth_token cookie
        NewCookie clearCookie = new NewCookie.Builder(AUTH_COOKIE_NAME)
            .value("")
            .path("/")
            .maxAge(0)
            .httpOnly(true)
            .build();

        String username = (securityContext != null && securityContext.getUserPrincipal() != null)
            ? securityContext.getUserPrincipal().getName()
            : "unknown";

        LOG.info("[AUTH] Logout: user={}", username);

        return Response.ok(ApiResponse.success("Logged out successfully.", null))
            .cookie(clearCookie)
            .build();
    }

    // -
    //  GET /api/auth/me - any authenticated role
    // -

    /**
     * Returns the current authenticated user's profile.
     * Used by the JSP frontend to populate the navbar and role-based UI.
     */
    @GET
    @Path("/me")
    @RolesAllowed({"ADMIN", "LOGISTICS_COORD", "WAREHOUSE_MGR", "VENDOR_REP", "CUSTOMS_AGENT", "CUSTOMER"})
    public Response getCurrentUser() {
        String username = securityContext.getUserPrincipal().getName();
        User user = userService.findByUsernameForAuth(username);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error("User profile not found."))
                .build();
        }
        return Response.ok(ApiResponse.success(UserDTO.from(user))).build();
    }

    // -
    //  DELETE /api/auth - @DenyAll - prevent accidental routing
    // -

    @DELETE
    @DenyAll
    public Response deleteNotPermitted() {
        return Response.status(Response.Status.METHOD_NOT_ALLOWED)
            .entity(ApiResponse.error("Method not allowed."))
            .build();
    }
}

