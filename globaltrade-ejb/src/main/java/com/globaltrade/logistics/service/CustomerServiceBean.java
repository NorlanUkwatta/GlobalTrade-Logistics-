package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.Customer;
import com.globaltrade.logistics.service.local.CustomerService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CustomerServiceBean implements CustomerService {

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @Override
    public Customer createCustomer(String customerCode, String companyName, String countryCode) {
        Customer customer = new Customer();
        customer.setCustomerCode(customerCode);
        customer.setCompanyName(companyName);
        customer.setCountryCode(countryCode);
        customer.setActive(true);
        em.persist(customer);
        return customer;
    }

    @Override
    public Customer findById(Long id) {
        return em.find(Customer.class, id);
    }

    @Override
    public List<Customer> findAll() {
        return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }
}
