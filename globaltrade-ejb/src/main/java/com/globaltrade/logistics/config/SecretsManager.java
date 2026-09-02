package com.globaltrade.logistics.config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Application-scoped configuration manager for externalized secrets.
 *
 * <h2>Usage</h2>
 * Reads a standard Java .properties file from a location defined by the
 * JVM argument: -Dglobaltrade.secrets.file
 *
 * This keeps sensitive data (JWT keys, carrier API tokens, database credentials)
 * entirely out of the application's source code and deployment archive.
 */
@ApplicationScoped
public class SecretsManager {

    private static final Logger LOG = LogManager.getLogger(SecretsManager.class);
    private static final String SECRETS_PROPERTY_KEY = "globaltrade.secrets.file";

    private final Properties properties = new Properties();

    @PostConstruct
    public void init() {
        String path = System.getProperty(SECRETS_PROPERTY_KEY);

        if (path == null || path.isBlank()) {
            LOG.warn("No JVM argument [{}] provided. Secrets will not be loaded from an external file.", SECRETS_PROPERTY_KEY);
            return;
        }

        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
            LOG.info("Successfully loaded secrets configuration from: {}", path);
        } catch (IOException e) {
            LOG.error("Failed to load secrets file from {}: {}", path, e.getMessage());
        }
    }

    /**
     * Retrieves a secret by its key.
     * @param key the property key (e.g. "jwt.secret.key")
     * @return the secret value, or null if not found
     */
    public String getSecret(String key) {
        return properties.getProperty(key);
    }

    /**
     * Retrieves a secret by its key, providing a fallback default value.
     */
    public String getSecret(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
