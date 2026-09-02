package com.globaltrade.logistics.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JWT utility service for token issuance and validation using JJWT 0.12+.
 *
 * <h2>Token Structure</h2>
 * <pre>
 *   Header : { "alg": "HS256", "typ": "JWT" }
 *   Payload: {
 *     "sub"   : "username",
 *     "roles" : ["ADMIN"],
 *     "name"  : "Full Name",
 *     "iat"   : epoch-seconds,
 *     "exp"   : epoch-seconds + TOKEN_VALIDITY_MS
 *   }
 * </pre>
 *
 * <h2>JJWT 0.12 API Usage</h2>
 * <ul>
 *   <li>Sign   : {@code Jwts.builder().signWith(secretKey).compact()}</li>
 *   <li>Parse  : {@code Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)}</li>
 *   <li>Key    : Injected {@link SecretKey} produced by {@link JwtKeyProducer}</li>
 * </ul>
 *
 * <h2>Token Validity</h2>
 * Tokens are valid for 24 hours. For high-security operations (customs clearance,
 * financial transactions), the caller should validate the token's issue time
 * and enforce shorter re-authentication windows.
 */
@ApplicationScoped
public class JwtUtil {

    private static final Logger LOG = LogManager.getLogger(JwtUtil.class);

    /** Token validity: 24 hours in milliseconds. */
    public static final long TOKEN_VALIDITY_MS = 24L * 60 * 60 * 1000;

    /** Custom claim key for roles. */
    private static final String CLAIM_ROLES = "roles";

    /** Custom claim key for display name. */
    private static final String CLAIM_NAME = "name";

    /**
     * The application-scoped SecretKey produced by {@link JwtKeyProducer}.
     * Injected by CDI — never a String literal.
     */
    @Inject
    private SecretKey jwtKey;

    // ── Token Generation ──────────────────────────────────────────────────

    /**
     * Generates a signed JWT for the given user.
     *
     * @param username  the authenticated user's username (becomes JWT subject)
     * @param roles     the user's authority set (role names)
     * @param fullName  the user's display name (embedded in token for UI convenience)
     * @return compact, URL-safe JWT string
     */
    public String generateToken(String username, Set<String> roles, String fullName) {
        Date now     = new Date();
        Date expiry  = new Date(now.getTime() + TOKEN_VALIDITY_MS);

        return Jwts.builder()
            // Standard claims
            .subject(username)
            .issuedAt(now)
            .expiration(expiry)
            // Custom claims
            .claim(CLAIM_ROLES, roles)
            .claim(CLAIM_NAME, fullName)
            // Sign with injected key — JJWT 0.12 API
            .signWith(jwtKey)
            .compact();
    }

    // ── Token Validation ─────────────────────────────────────────────────

    /**
     * Validates a JWT token and returns its claims.
     *
     * @param token the compact JWT string
     * @return parsed {@link Claims} if valid
     * @throws ExpiredJwtException      if the token has expired
     * @throws MalformedJwtException    if the token is malformed
     * @throws SecurityException        if the signature is invalid
     * @throws UnsupportedJwtException  if the token format is not supported
     */
    public Claims validateToken(String token) {
        Jws<Claims> jws = Jwts.parser()
            .verifyWith(jwtKey)   // JJWT 0.12 API — uses SecretKey, not String
            .build()
            .parseSignedClaims(token);

        return jws.getPayload();
    }

    /**
     * Validates a token silently — returns null instead of throwing for invalid tokens.
     * Use this in filters where you want a conditional check without exception handling.
     */
    public Claims validateTokenSilently(String token) {
        try {
            return validateToken(token);
        } catch (ExpiredJwtException e) {
            LOG.warn("JWT expired: {}", e.getMessage());
        } catch (Exception e) {
            LOG.warn("JWT invalid: {}", e.getMessage());
        }
        return null;
    }

    // ── Claims Extraction Helpers ────────────────────────────────────────

    /**
     * Extracts the username (JWT subject) from validated claims.
     */
    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    /**
     * Extracts the roles list from the JWT claims.
     * Returns an empty set if the claim is missing.
     */
    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(Claims claims) {
        Object rolesObj = claims.get(CLAIM_ROLES);
        if (rolesObj instanceof List<?> list) {
            Set<String> roles = new HashSet<>();
            list.forEach(r -> roles.add(String.valueOf(r)));
            return roles;
        }
        return new HashSet<>();
    }

    /**
     * Extracts the display name from the JWT claims.
     */
    public String extractDisplayName(Claims claims) {
        return claims.get(CLAIM_NAME, String.class);
    }

    /**
     * Returns the token validity period in seconds.
     */
    public long getTokenValiditySeconds() {
        return TOKEN_VALIDITY_MS / 1000;
    }
}
