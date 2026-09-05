package com.globaltrade.logistics.web.dto;

/**
 * Login request payload. Submitted as JSON body to {@code POST /api/auth/login}.
 *
 * @param username the user's platform username (case-insensitive — normalised to lowercase)
 * @param password plain-text password (transmitted over HTTPS only, never logged)
 */
public record AuthRequest(String username, String password) {
}
