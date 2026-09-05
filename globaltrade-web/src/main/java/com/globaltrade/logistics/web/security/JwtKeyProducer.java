package com.globaltrade.logistics.web.security;

import com.globaltrade.logistics.config.SecretsManager;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * CDI Producer for the JWT cryptographic signing key.
 *
 * <p>It now attempts to load a persistent 256-bit Base64-encoded key
 * from the {@link SecretsManager}. If unavailable, it falls back to
 * a randomly generated ephemeral key (meaning all users will be logged
 * out on the next server restart).</p>
 */
@ApplicationScoped
public class JwtKeyProducer {

    private static final Logger LOG = LogManager.getLogger(JwtKeyProducer.class);
    private static final String JWT_CONFIG_KEY = "jwt.secret.key";

    private SecretKey jwtKey;

    @Inject
    private SecretsManager secretsManager;

    @PostConstruct
    public void init() {
        String base64Secret = secretsManager.getSecret(JWT_CONFIG_KEY);

        if (base64Secret != null && !base64Secret.isBlank()) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
                this.jwtKey = Keys.hmacShaKeyFor(keyBytes);
                LOG.info("JWT signing key loaded successfully from secrets file.");
                return;
            } catch (IllegalArgumentException e) {
                LOG.error("Invalid base64 encoding for '{}'. Falling back to ephemeral key.", JWT_CONFIG_KEY);
            }
        } else {
            LOG.warn("No '{}' found in secrets file. Falling back to ephemeral key.", JWT_CONFIG_KEY);
        }

        // Fallback: Ephemeral Key
        generateEphemeralKey();
    }

    private void generateEphemeralKey() {
        this.jwtKey = io.jsonwebtoken.Jwts.SIG.HS256.key().build();
        LOG.warn("⚠ JWT key is ephemeral (regenerated on startup). All existing tokens will invalidate on server restart.");
    }

    /**
     * Exposes the SecretKey as a CDI bean to be injected anywhere in the web module.
     */
    @Produces
    @ApplicationScoped
    public SecretKey produceJwtKey() {
        return jwtKey;
    }
}
