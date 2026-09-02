package com.globaltrade.logistics.service.local;

import com.globaltrade.logistics.entity.Vendor;
import java.util.List;

@jakarta.ejb.Local
public interface VendorService {
    Vendor createVendor(String companyName, String contactName, String email, String phone);
    Vendor findById(Long id);
    List<Vendor> findAll();
}