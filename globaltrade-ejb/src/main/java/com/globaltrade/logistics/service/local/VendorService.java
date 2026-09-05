package com.globaltrade.logistics.service.local;

import com.globaltrade.logistics.entity.Vendor;
import java.util.List;

@jakarta.ejb.Local
public interface VendorService {
    Vendor createVendor(String companyName, String contactName, String email, String phone,
                        String registrationNumber, String headquartersAddress, 
                        Long commodityCategoryId, Integer standardLeadTimeDays,
                        String pickupAddressLine1, String pickupAddressLine2, String pickupCity,
                        String pickupState, String pickupPostalCode, String pickupCountry);
    Vendor findById(Long id);
    List<Vendor> findAll();
}
