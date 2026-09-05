package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.Vendor;
import com.globaltrade.logistics.entity.CommodityCategory;
import com.globaltrade.logistics.service.local.VendorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class VendorServiceBean implements VendorService {

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @Override
    @jakarta.annotation.security.PermitAll
    public Vendor createVendor(String companyName, String contactName, String email, String phone,
                               String registrationNumber, String headquartersAddress, 
                               Long commodityCategoryId, Integer standardLeadTimeDays,
                               String pickupAddressLine1, String pickupAddressLine2, String pickupCity,
                               String pickupState, String pickupPostalCode, String pickupCountry) {
        Vendor v = new Vendor();
        v.setCompanyName(companyName);
        v.setContactName(contactName);
        v.setEmail(email);
        v.setPhone(phone);
        v.setRegistrationNumber(registrationNumber);
        v.setHeadquartersAddress(headquartersAddress);
        v.setStandardLeadTimeDays(standardLeadTimeDays);
        v.setPickupAddressLine1(pickupAddressLine1);
        v.setPickupAddressLine2(pickupAddressLine2);
        v.setPickupCity(pickupCity);
        v.setPickupState(pickupState);
        v.setPickupPostalCode(pickupPostalCode);
        v.setPickupCountry(pickupCountry);
        
        if (commodityCategoryId != null) {
            CommodityCategory cat = em.find(CommodityCategory.class, commodityCategoryId);
            if (cat != null) {
                v.setCommodityCategory(cat);
            }
        }
        
        em.persist(v);
        return v;
    }

    @Override
    @RolesAllowed("ADMIN")
    public Vendor findById(Long id) {
        return em.find(Vendor.class, id);
    }

    @Override
    @RolesAllowed("ADMIN")
    public List<Vendor> findAll() {
        return em.createQuery("SELECT v FROM Vendor v ORDER BY v.companyName ASC", Vendor.class).getResultList();
    }
}
