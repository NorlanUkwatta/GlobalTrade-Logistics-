package com.globaltrade.logistics.exception;

/**
 * Thrown when an attempt is made to create a user whose username or email
 * already exists in the system (unique constraint violation at business layer).
 *
 * <p>HTTP mapping: <b>409 Conflict</b></p>
 *
 * <h2>Why Catch at Business Layer?</h2>
 * Rather than letting the JPA {@code ConstraintViolationException} bubble up
 * (which would be a system exception), we check for existence before persist
 * and throw this typed application exception. This keeps the transaction clean
 * and gives the API client a meaningful 409 response.
 */
public class UserAlreadyExistsException extends LogisticsApplicationException {

    public UserAlreadyExistsException(String message) {
        super(message, "USER_ALREADY_EXISTS", 409);
    }

    /** Factory for username conflicts. */
    public static UserAlreadyExistsException forUsername(String username) {
        return new UserAlreadyExistsException(
            "A user with username [" + username + "] already exists."
        );
    }

    /** Factory for email conflicts. */
    public static UserAlreadyExistsException forEmail(String email) {
        return new UserAlreadyExistsException(
            "A user with email [" + email + "] is already registered."
        );
    }
}
