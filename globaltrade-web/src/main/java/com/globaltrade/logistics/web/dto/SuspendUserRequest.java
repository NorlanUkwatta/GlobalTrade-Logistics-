package com.globaltrade.logistics.web.dto;

/**
 * Request payload for suspending a user account (Admin only).
 *
 * @param reason human-readable reason for suspension. This reason is:
 *               <ul>
 *                 <li>Stored in the {@code users.suspension_reason} column</li>
 *                 <li>Written to the audit log</li>
 *                 <li>Returned to the suspended user on subsequent login attempts
 *                     so they know to contact their administrator</li>
 *               </ul>
 *               Keep the reason factual and professional — it is visible to the user.
 */
public record SuspendUserRequest(String reason) {
}
