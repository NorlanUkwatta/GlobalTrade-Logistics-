package com.globaltrade.logistics.entity;

/**
 * Platform security roles for GlobalTrade Logistics Corporation.
 *
 * <p>Used as a JPA {@code @Enumerated(EnumType.STRING)} column on the {@code users}
 * table and as the authority source for Jakarta Security {@code @RolesAllowed}.</p>
 *
 * <ul>
 *   <li><b>ADMIN</b> — Full platform access. Manages all users, monitors all operations.</li>
 *   <li><b>LOGISTICS_COORD</b> — Internal staff: full read/write on shipments, routes,
 *       carriers, inventory. Can override timer-generated route optimizations.</li>
 *   <li><b>WAREHOUSE_MGR</b> — Internal staff: scoped to inventory counts, damaged-goods
 *       logging, inbound/outbound confirmation. Can approve auto-generated POs.</li>
 *   <li><b>VENDOR_REP</b> — External partner: isolated to their own {@code vendor_id}.
 *       Reads POs/invoices/shipments linked to their vendor. Uploads compliance docs.
 *       Views their own automated performance score.</li>
 *   <li><b>CUSTOMS_AGENT</b> — External government regulator: read-only on commercial
 *       invoices, bills of lading, HS codes for a given shipment. Has one write action:
 *       setting "Customs Cleared" or "Customs Hold" status (creates legally binding audit).</li>
 *   <li><b>CUSTOMER</b> — End-user corporation: isolated to their own {@code customer_id}.
 *       Read-only high-level milestones. No vendor, inventory, or financial visibility.</li>
 * </ul>
 */
public enum UserRole {

    /**
     * System administrator. Created only by the {@code AdminBootstrapBean} on first deploy
     * or by an existing ADMIN. Can create all other roles.
     */
    ADMIN,

    /**
     * Logistics Coordinator — internal GlobalTrade employee.
     * High access: shipments, routes, inventory, vendor relationships, exception handling.
     */
    LOGISTICS_COORD,

    /**
     * Warehouse Manager — internal GlobalTrade employee stationed at distribution centers.
     * Scoped access: inventory adjustments, shipment receive/dispatch confirmation, PO approval.
     */
    WAREHOUSE_MGR,

    /**
     * Vendor / Supplier Representative — external partner.
     * Strictly isolated to their {@code vendor_id}. Cannot see competing vendor data.
     */
    VENDOR_REP,

    /**
     * Customs Official / Agent — external government regulator.
     * Read-only on trade documents. Single write: Customs Cleared / Customs Hold.
     */
    CUSTOMS_AGENT,

    /**
     * Customer — multinational corporation end-user.
     * Isolated to their {@code customer_id}. High-level tracking milestones only.
     */
    CUSTOMER,
    
    /**
     * Operations — Internal team managing order assignment and transport.
     */
    OPS
}
