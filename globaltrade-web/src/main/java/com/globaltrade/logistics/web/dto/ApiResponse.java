package com.globaltrade.logistics.web.dto;

/**
 * Standard API response envelope used by all REST endpoints.
 *
 * <p>All API responses follow this consistent structure to make
 * client-side error handling predictable:</p>
 *
 * <pre>
 * // Success
 * { "success": true, "message": "User created.", "data": { ... } }
 *
 * // Failure
 * { "success": false, "message": "User not found.", "data": null }
 * </pre>
 *
 * @param <T>     type of the data payload (null on failure)
 * @param success {@code true} on success, {@code false} on business logic error
 * @param message human-readable result message (safe to display to end users)
 * @param data    the response payload, or {@code null} on error
 */
public record ApiResponse<T>(boolean success, String message, T data) {

    /** Factory: success with data and message. */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /** Factory: success with data only (generic "Operation successful" message). */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation successful.", data);
    }

    /** Factory: error with message, no data. */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
