package com.globaltrade.logistics.web.dto;

import java.util.Set;

/**
 * JWT login response payload. Returned by {@code POST /api/auth/login} on success.
 *
 * @param token          the compact JWT string — include as {@code Authorization: Bearer <token>}
 *                       in subsequent API requests
 * @param expiresInSeconds token validity in seconds (currently 86400 = 24 hours)
 * @param roles          the authenticated user's role set (e.g. ["ADMIN"])
 * @param username       the authenticated username (for UI display convenience)
 * @param fullName       the authenticated user's display name
 * @param role           the primary role string (convenience field, same as first element of roles)
 */
public record JwtResponse(
    String      token,
    long        expiresInSeconds,
    Set<String> roles,
    String      username,
    String      fullName,
    String      role
) {
}
