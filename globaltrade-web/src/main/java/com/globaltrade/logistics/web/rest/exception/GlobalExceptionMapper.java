package com.globaltrade.logistics.web.rest.exception;

import com.globaltrade.logistics.exception.LogisticsApplicationException;
import com.globaltrade.logistics.exception.LogisticsSystemException;
import com.globaltrade.logistics.web.dto.ApiResponse;
import jakarta.ejb.EJBAccessException;
import jakarta.ejb.EJBException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JAX-RS global exception mapper.
 *
 * <h2>Exception Classification Strategy</h2>
 *
 * <h3>Application Exceptions → 4xx Client Errors</h3>
 * {@link LogisticsApplicationException} subclasses carry an {@code httpStatusCode}
 * that maps directly to the HTTP response code. These are expected business conditions:
 * invalid input, not found, conflict, permission denied.
 *
 * <h3>EJBAccessException → 403 Forbidden</h3>
 * Thrown by the EJB container when {@code @RolesAllowed} is violated. The caller
 * attempted an operation they are not authorized for.
 *
 * <h3>EJBException (wrapping ApplicationException) → Unwrap and re-map</h3>
 * When an {@code @ApplicationException} propagates through an EJB call on a
 * remote or local interface boundary, the EJB container sometimes wraps it in
 * {@link EJBException}. We unwrap the cause and re-map.
 *
 * <h3>LogisticsSystemException → 503 Service Unavailable</h3>
 * Infrastructure failures: database down, carrier system outage. Always logged
 * at ERROR level with full stack trace.
 *
 * <h3>Catch-all Throwable → 500 Internal Server Error</h3>
 * Anything not caught above. Full stack trace is logged but NOT returned to
 * the client (prevents information leakage).
 *
 * <h2>Error Response Format</h2>
 * All errors follow the {@link ApiResponse} envelope structure:
 * <pre>
 * { "success": false, "message": "...", "data": null }
 * </pre>
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LogManager.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {

        // ── 1. BusinessLogic Exceptions (Application Layer) ──────────────
        if (exception instanceof LogisticsApplicationException appEx) {
            LOG.info("[API-ERROR] {} (HTTP {}): {}",
                appEx.getErrorCode(), appEx.getHttpStatusCode(), appEx.getMessage());
            return Response.status(appEx.getHttpStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error(appEx.getMessage()))
                .build();
        }

        // ── 2. EJBAccessException — @RolesAllowed violation ──────────────
        if (exception instanceof EJBAccessException) {
            LOG.warn("[SECURITY] Access denied by @RolesAllowed: {}", exception.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error(
                    "You do not have permission to perform this operation."))
                .build();
        }

        // ── 3. EJBException — unwrap cause and re-process ─────────────────
        if (exception instanceof EJBException ejbEx) {
            Exception cause = ejbEx.getCausedByException();
            if (cause instanceof LogisticsApplicationException appEx) {
                // ApplicationException was wrapped by EJB container — unwrap it
                LOG.info("[API-ERROR] Unwrapped EJBException → {}: {}",
                    appEx.getErrorCode(), appEx.getMessage());
                return Response.status(appEx.getHttpStatusCode())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ApiResponse.error(appEx.getMessage()))
                    .build();
            }
            // EJBException with non-application cause = system failure
            LOG.error("[SYSTEM-ERROR] EJBException: {}", ejbEx.getMessage(), ejbEx);
            return systemError("A system error occurred. Our team has been notified.");
        }

        // ── 4. LogisticsSystemException (extends EJBException) ───────────
        if (exception instanceof LogisticsSystemException sysEx) {
            LOG.error("[SYSTEM-ERROR] Component [{}]: {}",
                sysEx.getComponent(), sysEx.getMessage(), sysEx);
            return Response.status(sysEx.getHttpStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error(sysEx.getMessage()))
                .build();
        }

        // ── 5. JAX-RS Not Found ───────────────────────────────────────────
        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error("The requested resource was not found."))
                .build();
        }

        // ── 6. JAX-RS Method Not Allowed ─────────────────────────────────
        if (exception instanceof NotAllowedException) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error("HTTP method not allowed for this endpoint."))
                .build();
        }

        
        // Handle JAX-RS standard exceptions (e.g., NotAuthorizedException, ForbiddenException, NotFoundException)
        if (exception instanceof WebApplicationException webAppEx) {
            LOG.warn("[JAX-RS] Framework exception: {}", webAppEx.getMessage());
            return Response.status(webAppEx.getResponse().getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error(webAppEx.getMessage()))
                .build();
        }

        
        // Handle JAX-RS standard exceptions (e.g., NotAuthorizedException, ForbiddenException, NotFoundException)
        if (exception instanceof WebApplicationException webAppEx) {
            LOG.warn("[JAX-RS] Framework exception: {}", webAppEx.getMessage());
            return Response.status(webAppEx.getResponse().getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error(webAppEx.getMessage()))
                .build();
        }

        // ── 7. Catch-all — never expose internals to the client ──────────
        LOG.error("[UNHANDLED-ERROR] Unexpected exception: {}", exception.getMessage(), exception);
        
        java.io.StringWriter sw = new java.io.StringWriter();
        exception.printStackTrace(new java.io.PrintWriter(sw));
        String stackTrace = sw.toString();
        
        return systemError("Error: " + exception.getMessage() + (exception.getCause() != null ? " Cause: " + exception.getCause().getMessage() : "") + " STACK: " + stackTrace);
    }

    private Response systemError(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .type(MediaType.APPLICATION_JSON)
            .entity(ApiResponse.error(message))
            .build();
    }
}
