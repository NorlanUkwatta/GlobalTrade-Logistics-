package com.globaltrade.logistics.startup;

import com.globaltrade.logistics.entity.*;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class AdminBootstrapBean {

    private static final Logger LOG = LogManager.getLogger(AdminBootstrapBean.class);
    private static final String DEFAULT_ADMIN_PASSWORD = "Password123!";
    private static final int BCRYPT_ROUNDS = 10;

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void initialize() {
        LOG.info("=== GlobalTrade Logistics - Rich Bootstrap Starting ===");
        try {
            LOG.info("Applying schema migrations for ENUM statuses...");
            em.createNativeQuery("ALTER TABLE users MODIFY role VARCHAR(50)").executeUpdate();
            em.createNativeQuery("ALTER TABLE shipping_orders MODIFY status VARCHAR(50)").executeUpdate();
            em.createNativeQuery("ALTER TABLE shipping_orders MODIFY vendor_id BIGINT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE payment_settlements MODIFY vendor_id BIGINT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE shipments MODIFY status VARCHAR(50)").executeUpdate();

            em.createNativeQuery("UPDATE shipping_orders SET status = 'PENDING' WHERE status = 'SUBMITTED'").executeUpdate();
            em.createNativeQuery("UPDATE shipping_orders SET status = 'IN_PROGRESS' WHERE status = 'PROCESSING'").executeUpdate();
            
            em.createNativeQuery("UPDATE shipments SET status = 'SHIPPED' WHERE status = 'IN_TRANSIT'").executeUpdate();
            em.createNativeQuery("UPDATE shipments SET status = 'IN_PROGRESS' WHERE status = 'CUSTOMS_CLEARANCE'").executeUpdate();
            
            em.createNativeQuery("UPDATE shipments SET status = 'IN_WAREHOUSE' WHERE status = 'WAREHOUSE'").executeUpdate();
            
            try {
                em.createNativeQuery("ALTER TABLE regions MODIFY country_id BIGINT NULL").executeUpdate();
                LOG.info("Successfully made regions.country_id NULLable.");
            } catch(Exception ex) {
                LOG.warn("Could not alter regions table: " + ex.getMessage());
            }

        } catch(Exception e) {
            LOG.warn("Migration failed (possibly already applied or tables missing): " + e.getMessage());
        }

        try {
            Long userCount = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
            if (userCount == 0) {
                bootstrapAllData();
            } else {
                LOG.info("Database already populated. Skipping full bootstrap.");
                // Ensure OPS user exists in case of incremental update
                Long opsCount = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = 'ops1'", Long.class).getSingleResult();
                if (opsCount == 0) {
                    LOG.info("Injecting missing ops1 user...");
                    createUser("ops1", "GlobalTrade Operations", "ops@globaltrade.lk", UserRole.OPS, null, null);
                }
                Long itopsCount = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = 'itops1'", Long.class).getSingleResult();
                if (itopsCount == 0) {
                    LOG.info("Injecting missing itops1 user...");
                    createUser("itops1", "IT Operations", "itops@globaltrade.lk", UserRole.ITOPS, null, null);
                }
                
                Long countryCount = em.createQuery("SELECT COUNT(c) FROM Country c", Long.class).getSingleResult();
                if (countryCount == 0) {
                    bootstrapLocations();
                }
            }
        } catch (Exception e) {
            LOG.fatal("BOOTSTRAP FAILURE: {}", e.getMessage(), e);
        }
    }

    private void bootstrapLocations() {
        LOG.info("Injecting location data...");
        Country lk = new Country("Sri Lanka", "LK");
        Country us = new Country("United States", "US");
        Country uk = new Country("United Kingdom", "UK");
        em.persist(lk);
        em.persist(us);
        em.persist(uk);
        
        em.persist(new Region("Western Province"));
        em.persist(new Region("Southern Province"));
        em.persist(new Region("Central Province"));
        
        em.persist(new Region("California"));
        em.persist(new Region("New York"));
        em.persist(new Region("Texas"));
        
        em.persist(new Region("England"));
        em.persist(new Region("Scotland"));
    }

    private void bootstrapAllData() {
        LOG.info("Injecting realistic Sri Lankan trade lifecycle sample data...");

        // 1. Create Users
        User admin = createUser("admin", "System Administrator", "admin@globaltrade.lk", UserRole.ADMIN, null, null);
        
        // 2. Create Vendors
        Vendor maersk = new Vendor();
        maersk.setCompanyName("Maersk Line LK");
        maersk.setContactName("Kamal Perera");
        maersk.setEmail("kamal@maersk.lk");
        maersk.setPhone("+94 11 234 5678");
        maersk.setPerformanceScore(98.5);
        em.persist(maersk);
        
        Vendor cmacgm = new Vendor();
        cmacgm.setCompanyName("CMA CGM Lanka");
        cmacgm.setContactName("Nimal Fernando");
        cmacgm.setEmail("nimal@cmacgm.lk");
        cmacgm.setPhone("+94 11 456 7890");
        cmacgm.setPerformanceScore(91.2);
        em.persist(cmacgm);
        em.flush();

        // 3. Create Rest of Users mapped to Vendors/Customers
        createUser("vendor1", "Kamal Perera", "kamal@maersk.lk", UserRole.VENDOR_REP, maersk.getId(), null);
        createUser("coord1", "Saman Logistics", "coord@globaltrade.lk", UserRole.LOGISTICS_COORD, null, null);
        createUser("customs1", "Sri Lanka Customs (SLPA)", "officer@customs.gov.lk", UserRole.CUSTOMS_AGENT, null, null);
        createUser("warehouse1", "Colombo Port Warehouse", "wh@globaltrade.lk", UserRole.WAREHOUSE_MGR, null, null);
        createUser("customer1", "Dilmah Tea Exports", "logistics@dilmah.lk", UserRole.CUSTOMER, null, 1001L);
        createUser("ops1", "GlobalTrade Operations", "ops@globaltrade.lk", UserRole.OPS, null, null);
        createUser("itops1", "IT Operations", "itops@globaltrade.lk", UserRole.ITOPS, null, null);

        // 4. Create Containers
        Container c1 = new Container();
        c1.setContainerNumber("MSKU-1234567");
        c1.setType(Container.ContainerType.STANDARD_20FT);
        em.persist(c1);

        Container c2 = new Container();
        c2.setContainerNumber("CMAU-9876543");
        c2.setType(Container.ContainerType.REFRIGERATED);
        em.persist(c2);

        // 5. Create Shipments across the lifecycle
        
        // Shipment 1: DELIVERED (Historical)
        Shipment s1 = new Shipment();
        s1.setTrackingNumber("TRK-" + System.currentTimeMillis() + "1");
        s1.setOrigin("Shanghai, China");
        s1.setDestination("Colombo, Sri Lanka");
        s1.setVendor(maersk);
        s1.setContainer(c1);
        s1.setCustomerId(1001L);
        s1.setStatus(Shipment.Status.DELIVERED);
        em.persist(s1);

        // Shipment 2: CUSTOMS_CLEARANCE (Waiting for Agent)
        Shipment s2 = new Shipment();
        s2.setTrackingNumber("TRK-" + System.currentTimeMillis() + "2");
        s2.setOrigin("Singapore");
        s2.setDestination("Colombo, Sri Lanka");
        s2.setVendor(cmacgm);
        s2.setContainer(c2);
        s2.setCustomerId(1001L);
        s2.setStatus(Shipment.Status.IN_PROGRESS);
        em.persist(s2);

        // Customs Declaration for S2
        CustomsDeclaration d2 = new CustomsDeclaration();
        d2.setShipment(s2);
        d2.setDutyAmount(125000.00); // 125,000 Rs
        d2.setStatus(CustomsDeclaration.Status.SUBMITTED);
        em.persist(d2);

        // Shipment 3: IN_TRANSIT
        Shipment s3 = new Shipment();
        s3.setTrackingNumber("TRK-" + System.currentTimeMillis() + "3");
        s3.setOrigin("Dubai, UAE");
        s3.setDestination("Hambantota, Sri Lanka");
        s3.setVendor(maersk);
        s3.setCustomerId(1001L);
        s3.setStatus(Shipment.Status.SHIPPED);
        em.persist(s3);

        // 6. Create Warehouse Inventory
        InventoryItem i1 = new InventoryItem();
        i1.setSku("TEA-BLK-01");
        i1.setName("Ceylon Black Tea 500g");
        i1.setQuantity(5000);
        i1.setLocation("WH-COLOMBO-A1");
        em.persist(i1);

        InventoryItem i2 = new InventoryItem();
        i2.setSku("APP-SHIRT-02");
        i2.setName("Export Garments - Shirts");
        i2.setQuantity(1200);
        i2.setLocation("WH-KATUNAYAKE-B2");
        em.persist(i2);

        bootstrapLocations();

        // 7. Create Purchase Orders
        PurchaseOrder po1 = new PurchaseOrder();
        po1.setVendor(maersk);
        po1.setSku("TEA-BLK-01");
        po1.setQuantity(2000);
        po1.setStatus(PurchaseOrder.Status.ACKNOWLEDGED);
        em.persist(po1);
        
        LOG.info("=== Rich Sample Data Injected Successfully ===");
    }

    private User createUser(String username, String fullName, String email, UserRole role, Long vendorId, Long customerId) {
        User u = new User();
        u.setUsername(username);
        u.setFullName(fullName);
        u.setEmail(email);
        u.setRole(role);
        u.setPasswordHash(BCrypt.hashpw(DEFAULT_ADMIN_PASSWORD, BCrypt.gensalt(BCRYPT_ROUNDS)));
        u.setVendorId(vendorId);
        u.setCustomerId(customerId);
        u.setActive(true);
        u.setSuspended(false);
        u.setCreatedBy("BOOTSTRAP");
        em.persist(u);
        return u;
    }
}




