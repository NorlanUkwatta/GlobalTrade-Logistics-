package com.globaltrade.logistics.web.security;

import io.jsonwebtoken.Claims;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * Jakarta Security 3.0 {@link HttpAuthenticationMechanism} - JWT-based authentication.
 *
 * <h2>Token Extraction (Dual-Channel)</h2>
 * <ol>
 *   <li><b>Authorization Header</b>: {@code Authorization: Bearer <token>}
 *       - used by REST API clients (mobile apps, third-party integrations)</li>
 *   <li><b>Cookie</b>: {@code auth_token=<token>}
 *       - used by browser-based JSP pages (set as HTTP-only by the login endpoint)</li>
 * </ol>
 * The header is checked first and takes priority.
 *
 * <h2>Integration with Jakarta EE Security</h2>
 * When a valid token is found, this mechanism calls
 * {@code context.notifyContainerAboutLogin(username, roles)}, which:
 * <ul>
 *   <li>Sets the caller principal for this request.</li>
 *   <li>Makes the roles available to {@code @RolesAllowed} on EJBs and JAX-RS resources.</li>
 *   <li>Makes {@code SecurityContext.getUserPrincipal()} return the correct user.</li>
 * </ul>
 *
 * <h2>Contract with the EJB Security Layer</h2>
 * The roles populated here (strings matching the {@link com.globaltrade.logistics.entity.UserRole}
 * enum names) must match the strings used in {@code @RolesAllowed} annotations
 * on EJB methods and JAX-RS resources for declarative security to work.
 */
@ApplicationScoped
public class JwtAuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final Logger LOG = LogManager.getLogger(JwtAuthenticationMechanism.class);

    private static final String BEARER_PREFIX  = "Bearer ";
    private static final String AUTH_HEADER    = "Authorization";
    private static final String AUTH_COOKIE    = "auth_token";

    @Inject
    private JwtUtil jwtUtil;

    @Override
    public AuthenticationStatus validateRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpMessageContext context) throws AuthenticationException {

        String token = extractToken(request);

        if (token == null || token.isBlank()) {
            /*
             * No token found. If the resource is protected (@RolesAllowed),
             * the container will return 401 automatically. If it's @PermitAll,
             * doNothing() allows the request to proceed as unauthenticated.
             */
            return context.doNothing();
        }

        Claims claims = jwtUtil.validateTokenSilently(token);
        if (claims == null) {
            /*
             * Token present but invalid/expired. Respond with 401 to signal
             * the client should re-authenticate.
             */
            LOG.debug("Invalid or expired JWT received from {}", request.getRemoteAddr());
            return context.responseUnauthorized();
        }

        // - Valid token: extract identity and notify the container -
        String     username = jwtUtil.extractUsername(claims);
        Set<String> roles   = jwtUtil.extractRoles(claims);

        LOG.debug("JWT authenticated: user={}, roles={}", username, roles);

        /*
         * notifyContainerAboutLogin(username, groups):
         * - 1st arg: caller principal name (matches SecurityContext.getUserPrincipal().getName())
         * - 2nd arg: groups/roles - matched against @RolesAllowed values
         */
        return context.notifyContainerAboutLogin(username, roles);
    }

    @Override
    public void cleanSubject(HttpServletRequest request,
                             HttpServletResponse response,
                             HttpMessageContext context) {
        // Clear auth cookie on logout
        Cookie clearCookie = new Cookie(AUTH_COOKIE, "");
        clearCookie.setMaxAge(0);
        clearCookie.setPath("/");
        clearCookie.setHttpOnly(true);
        response.addCookie(clearCookie);
        context.cleanClientSubject();
    }

    // - Private Helpers -

    /**
     * Extracts the JWT from the request. Checks Authorization header first,
     * then falls back to the auth_token cookie.
     */
    private String extractToken(HttpServletRequest request) {
        // 1. Authorization: Bearer <token>
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length()).trim();
        }

        // 2. Cookie: auth_token=<token>
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AUTH_COOKIE.equals(cookie.getName())) {
                    String val = cookie.getValue();
                    return (val != null && !val.isBlank()) ? val.trim() : null;
                }
            }
        }

        return null;
    }
}
