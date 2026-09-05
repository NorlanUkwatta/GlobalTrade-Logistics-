package com.globaltrade.logistics.service.local;

import com.globaltrade.logistics.entity.Customer;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface CustomerService {
    Customer createCustomer(String customerCode, String companyName, String countryCode);
    Customer findById(Long id);
    List<Customer> findAll();
}
