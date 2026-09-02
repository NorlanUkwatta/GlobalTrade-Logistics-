-- =============================================================================
-- GlobalTrade Logistics Corporation — Supply Chain Management Database
-- Database: globaltrade_scm
-- Target  : MySQL 8.0+
-- =============================================================================
-- SETUP INSTRUCTIONS:
--   1. mysql -u root -p
--   2. CREATE DATABASE globaltrade_scm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--   3. CREATE USER 'gtl_user'@'localhost' IDENTIFIED BY 'GTL_Pass@2024';
--   4. GRANT ALL PRIVILEGES ON globaltrade_scm.* TO 'gtl_user'@'localhost';
--   5. FLUSH PRIVILEGES;
--   6. USE globaltrade_scm;
--   7. SOURCE schema.sql;
--
-- GlassFish JDBC Connection Pool (XA for JTA):
--   Pool Name            : GlobalTradePool
--   Resource Type        : javax.sql.XADataSource
--   DataSource Classname : com.mysql.cj.jdbc.MysqlXADataSource
--   JNDI Name            : jdbc/GlobalTradeDS
--   Properties:
--     serverName         = localhost
--     portNumber         = 3306
--     databaseName       = globaltrade_logistics
--     user               = root
--     password           = NorlanUk1@
--     characterEncoding  = UTF-8
--     useSSL             = false
--     allowPublicKeyRetrieval = true
--     serverTimezone     = UTC
-- =============================================================================

USE globaltrade_logistics;

-- =============================================================================
-- TABLE: users
-- Stores all platform users: internal staff, vendors, customs officials, customers.
-- Role-based isolation enforced at application layer (vendor_id / customer_id).
-- =============================================================================
CREATE TABLE IF NOT EXISTS users (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    username            VARCHAR(50)     NOT NULL,
    email               VARCHAR(150)    NOT NULL,
    full_name           VARCHAR(200)    NOT NULL,
    password_hash       VARCHAR(255)    NOT NULL COMMENT 'BCrypt 12-round hash',
    role                ENUM(
                            'ADMIN',
                            'LOGISTICS_COORD',
                            'WAREHOUSE_MGR',
                            'VENDOR_REP',
                            'CUSTOMS_AGENT',
                            'CUSTOMER'
                        )               NOT NULL,
    -- For VENDOR_REP: restricts data access to their vendor_id only
    vendor_id           BIGINT          DEFAULT NULL COMMENT 'Not null when role=VENDOR_REP',
    -- For CUSTOMER: restricts tracking to their customer_id only
    customer_id         BIGINT          DEFAULT NULL COMMENT 'Not null when role=CUSTOMER',
    active              TINYINT(1)      NOT NULL DEFAULT 1,
    suspended           TINYINT(1)      NOT NULL DEFAULT 0,
    suspension_reason   TEXT            DEFAULT NULL,
    created_at          DATETIME        NOT NULL,
    updated_at          DATETIME        DEFAULT NULL,
    last_login          DATETIME        DEFAULT NULL,
    created_by          VARCHAR(50)     DEFAULT NULL COMMENT 'Username of creator (SYSTEM for bootstrap)',

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_username (username),
    UNIQUE KEY uq_users_email    (email),
    INDEX idx_users_role         (role),
    INDEX idx_users_vendor_id    (vendor_id),
    INDEX idx_users_customer_id  (customer_id),
    INDEX idx_users_active       (active, suspended)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Platform users with role-based isolation';


-- =============================================================================
-- TABLE: audit_logs
-- Immutable audit trail for all business operations.
-- Written in REQUIRES_NEW transaction → survives main TX rollback.
-- Retention: 365 days (regulatory requirement — customs/trade compliance).
-- =============================================================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    action          VARCHAR(255)    NOT NULL COMMENT 'ClassName.methodName or logical action label',
    caller_username VARCHAR(50)     DEFAULT NULL,
    status          VARCHAR(20)     NOT NULL COMMENT 'SUCCESS | FAILED | ERROR',
    entity_type     VARCHAR(100)    DEFAULT NULL COMMENT 'Entity class affected (e.g. User, Shipment)',
    entity_id       VARCHAR(100)    DEFAULT NULL COMMENT 'ID of affected entity',
    details         TEXT            DEFAULT NULL COMMENT 'Error message or supplementary info',
    duration_ms     BIGINT          DEFAULT NULL COMMENT 'Method execution duration in milliseconds',
    ip_address      VARCHAR(45)     DEFAULT NULL COMMENT 'Client IP (IPv4 or IPv6)',
    timestamp       DATETIME        NOT NULL,

    PRIMARY KEY (id),
    INDEX idx_audit_caller      (caller_username),
    INDEX idx_audit_action      (action(100)),
    INDEX idx_audit_status      (status),
    INDEX idx_audit_timestamp   (timestamp),
    INDEX idx_audit_entity      (entity_type, entity_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Immutable audit trail — do not DELETE or UPDATE rows';


-- =============================================================================
-- PHASE 2+ tables (placeholder definitions to be expanded)
-- Referenced here so FK constraints can be added later without schema migration.
-- =============================================================================

CREATE TABLE IF NOT EXISTS vendors (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    vendor_code     VARCHAR(50)     NOT NULL,
    company_name    VARCHAR(200)    NOT NULL,
    country_code    CHAR(2)         NOT NULL,
    tier            ENUM('PLATINUM','GOLD','SILVER','BRONZE') NOT NULL DEFAULT 'BRONZE',
    performance_score DECIMAL(5,2)  DEFAULT 0.00,
    active          TINYINT(1)      NOT NULL DEFAULT 1,
    created_at      DATETIME        NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_vendor_code (vendor_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Vendor/Supplier profiles — expanded in Phase 2';

CREATE TABLE IF NOT EXISTS customers (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    customer_code   VARCHAR(50)     NOT NULL,
    company_name    VARCHAR(200)    NOT NULL,
    country_code    CHAR(2)         NOT NULL,
    active          TINYINT(1)      NOT NULL DEFAULT 1,
    created_at      DATETIME        NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_customer_code (customer_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Customer profiles — expanded in Phase 2';
