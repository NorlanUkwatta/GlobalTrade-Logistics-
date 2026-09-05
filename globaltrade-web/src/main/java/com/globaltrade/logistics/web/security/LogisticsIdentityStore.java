package com.globaltrade.logistics.web.security;

import com.globaltrade.logistics.entity.User;
import com.globaltrade.logistics.service.local.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import javax.naming.InitialContext;
import java.util.Set;

/**
 * Jakarta Security 3.0 {@link IdentityStore} — delegates credential validation
 * to the {@link UserService} EJB and enforces account state rules.
 *
 * <h2>Validation Flow</h2>
 * <ol>
 *   <li>Extract username + password from {@link UsernamePasswordCredential}</li>
 *   <li>Look up the user entity from the database via {@link UserService#findByUsernameForAuth}</li>
 *   <li>If not found → {@link CredentialValidationResult#INVALID_RESULT} (generic response)</li>
 *   <li>If account is suspended → {@link CredentialValidationResult#INVALID_RESULT}
 *       (see security note below)</li>
 *   <li>Verify BCrypt hash — {@code BCrypt.checkpw(raw, hash)}</li>
 *   <li>If valid → return {@link CredentialValidationResult} with username + role as group</li>
 * </ol>
 *
 * <h2>Security Design: User Enumeration Prevention</h2>
 * For "user not found" and "wrong password" cases, the same
 * {@code INVALID_RESULT} is returned. The caller ({@code AuthResource})
 * returns a generic 401 message in both cases. This prevents user enumeration
 * attacks where an attacker can distinguish "user doesn't exist" from
 * "wrong password" based on response time or message.
 *
 * <h2>Account Suspension at Login Layer</h2>
 * Suspended accounts return INVALID_RESULT (not a special suspended result)
 * because the caller should return a 403 only if we want to reveal the
 * account exists. The {@code AuthResource} explicitly checks for suspension
 * to return a meaningful message after the login flow, but only to legitimate
 * credential holders.
 *
 * <h2>EJB Injection Strategy</h2>
 * {@code @EJB} injection into a CDI {@code @ApplicationScoped} bean is
 * supported in Jakarta EE 10. Falls back to JNDI lookup if injection fails.
 */
@ApplicationScoped
public class LogisticsIdentityStore implements IdentityStore {

    private static final Logger LOG = LogManager.getLogger(LogisticsIdentityStore.class);

    @jakarta.ejb.EJB
    private UserService userService;

    @PostConstruct
    public void init() {
        // Fallback JNDI lookup if @EJB injection is not resolved in this CDI context
        if (userService == null) {
            try {
                userService = (UserService) InitialContext.doLookup(
                    "java:global/globaltrade-ear/globaltrade-ejb/UserServiceBean!com.globaltrade.logistics.service.local.UserService"
                );
                LOG.info("UserService resolved via JNDI fallback.");
            } catch (Exception e) {
                LOG.warn("UserService JNDI lookup failed: {} — @EJB injection required.", e.getMessage());
            }
        }
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (!(credential instanceof UsernamePasswordCredential upc)) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }

        String username    = upc.getCaller();
        String rawPassword = upc.getPasswordAsString();

        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        try {
            // findByUsernameForAuth is @PermitAll — safe to call pre-authentication
            User user = userService.findByUsernameForAuth(username.trim().toLowerCase());

            if (user == null) {
                LOG.debug("[SECURITY] Login attempt for unknown username: {}", username);
                // Still run a dummy BCrypt check to prevent timing attacks
                BCrypt.checkpw(rawPassword, "$2a$12$dummyhash.for.timing.attack.prevention");
                return CredentialValidationResult.INVALID_RESULT;
            }

            // BCrypt verification — constant-time comparison
            if (!BCrypt.checkpw(rawPassword, user.getPasswordHash())) {
                LOG.debug("[SECURITY] Invalid password attempt for user: {}", username);
                return CredentialValidationResult.INVALID_RESULT;
            }

            // Check account state
            if (!user.isActive()) {
                LOG.warn("[SECURITY] Login attempt on deactivated account: {}", username);
                return CredentialValidationResult.INVALID_RESULT;
            }

            if (user.isSuspended()) {
                // Return invalid here — AuthResource will separately detect suspension
                // by attempting a lookup if INVALID comes back, keeping timing consistent
                LOG.warn("[SECURITY] Login attempt on suspended account: {}", username);
                return CredentialValidationResult.INVALID_RESULT;
            }

            // ✅ Authentication successful
            LOG.info("[SECURITY] Successful authentication for user: {}, role: {}", username, user.getRole());
            return new CredentialValidationResult(
                user.getUsername(),
                Set.of(user.getRole().name())  // group name must match @RolesAllowed values
            );

        } catch (Exception e) {
            LOG.error("[SECURITY] Authentication error for user [{}]: {}", username, e.getMessage(), e);
            return CredentialValidationResult.INVALID_RESULT;
        }
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        // Groups are already included in the CredentialValidationResult above
        return validationResult.getCallerGroups();
    }
}
