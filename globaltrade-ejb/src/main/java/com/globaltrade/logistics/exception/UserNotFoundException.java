package com.globaltrade.logistics.exception;

/**
 * Thrown when a requested user account does not exist in the system.
 *
 * <p>HTTP mapping: <b>404 Not Found</b></p>
 *
 * <h2>Usage Guidance</h2>
 * Throw this exception in service layer lookups when a user ID or username
 * is not found in the database. Do NOT throw this during authentication
 * (use {@link AuthenticationException} with a generic message to prevent
 * user enumeration).
 */
public class UserNotFoundException extends LogisticsApplicationException {

    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND", 404);
    }

    /** Factory for ID-based lookup failures. */
    public static UserNotFoundException byId(Long userId) {
        return new UserNotFoundException("User with ID [" + userId + "] not found.");
    }

    /** Factory for username-based lookup failures. */
    public static UserNotFoundException byUsername(String username) {
        return new UserNotFoundException("User with username [" + username + "] not found.");
    }
}
