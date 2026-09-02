package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.Vendor;
import com.globaltrade.logistics.service.local.VendorService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;

@Stateless
public class VendorServiceBean implements VendorService {

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @Override
    @jakarta.annotation.security.PermitAll
    public Vendor createVendor(String companyName, String contactName, String email, String phone) {
        Vendor v = new Vendor();
        v.setCompanyName(companyName);
        v.setContactName(contactName);
        v.setEmail(email);
        v.setPhone(phone);
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