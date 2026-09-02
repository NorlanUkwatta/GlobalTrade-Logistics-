package com.globaltrade.logistics.web.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS Application bootstrap for GlobalTrade Logistics REST API.
 *
 * <p>Base path: {@code /globaltrade/api/...}</p>
 *
 * <h2>Registered automatically via CDI scanning — no web.xml servlet needed.</h2>
 *
 * <h2>Registered Resources (via classpath scanning)</h2>
 * <ul>
 *   <li>{@code POST /api/auth/login}    — @PermitAll — authenticate and receive JWT</li>
 *   <li>{@code POST /api/auth/logout}   — @PermitAll — clear auth cookie</li>
 *   <li>{@code GET  /api/users}         — @RolesAllowed("ADMIN")</li>
 *   <li>{@code POST /api/users}         — @RolesAllowed("ADMIN")</li>
 *   <li>{@code GET  /api/users/{id}}    — @RolesAllowed(ADMIN, LOGISTICS_COORD)</li>
 *   <li>{@code PUT  /api/users/{id}/suspend}  — @RolesAllowed("ADMIN")</li>
 *   <li>{@code PUT  /api/users/{id}/activate} — @RolesAllowed("ADMIN")</li>
 *   <li>{@code GET  /api/users/me}      — @RolesAllowed(all authenticated roles)</li>
 *   <li>{@code PUT  /api/users/me/password} — @RolesAllowed(all authenticated roles)</li>
 * </ul>
 */
@ApplicationPath("/api")
public class LogisticsApplication extends Application {
    // Empty body — Jersey/JAX-RS scans classpath for @Path-annotated resources
}
