package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CustomerPortalServiceBean {

    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    public List<Vendor> getAllVendors() {
        return em.createQuery("SELECT v FROM Vendor v", Vendor.class).getResultList();
    }

    public List<ShippingOrder> getOrders(Long customerId) {
        return em.createQuery("SELECT o FROM ShippingOrder o WHERE o.customerId = :cid ORDER BY o.createdAt DESC", ShippingOrder.class)
                 .setParameter("cid", customerId)
                 .getResultList();
    }

    public ShippingOrder placeOrder(Long customerId, ShippingOrder orderData) {
        orderData.setCustomerId(customerId);
        
        // Auto routing based on customer destination (origin will be set later by OPS)
        orderData.setRouteFrom("Pending Assignment");
        orderData.setRouteTo(orderData.getCity() + ", " + orderData.getCountry());
        
        em.persist(orderData);

        // Create Payment
        PaymentSettlement payment = new PaymentSettlement();
        payment.setShippingOrder(orderData);
        payment.setCustomerId(customerId);
        double amount = 50.0;
        if (orderData.getItemCount() != null) {
            amount = orderData.getItemCount() * 1.5; // $1.50 per item
        } else if (orderData.getWeight() != null) {
            amount = orderData.getWeight() * 12.5;
        }
        payment.setAmount(amount);
        payment.setIsPaid(true); // Auto-paid for demo purposes
        em.persist(payment);

        return orderData;
    }

    public void cancelOrder(Long orderId, Long customerId) {
        ShippingOrder order = em.find(ShippingOrder.class, orderId);
        if (order != null && order.getCustomerId().equals(customerId) && order.getStatus() == ShippingOrder.Status.PENDING) {
            order.setStatus(ShippingOrder.Status.CANCELLED);
            em.merge(order);
        }
    }

    public void deleteOrder(Long orderId, Long customerId) {
        ShippingOrder order = em.find(ShippingOrder.class, orderId);
        if (order != null && order.getCustomerId().equals(customerId) && order.getStatus() == ShippingOrder.Status.CANCELLED) {
            em.remove(order);
        }
    }

    public List<PaymentSettlement> getPayments(Long customerId) {
        return em.createQuery("SELECT p FROM PaymentSettlement p WHERE p.customerId = :cid ORDER BY p.createdAt DESC", PaymentSettlement.class)
                 .setParameter("cid", customerId)
                 .getResultList();
    }

    public List<ReturnedItem> getReturns(Long customerId) {
        return em.createQuery("SELECT r FROM ReturnedItem r WHERE r.shippingOrder.customerId = :cid ORDER BY r.returnedAt DESC", ReturnedItem.class)
                 .setParameter("cid", customerId)
                 .getResultList();
    }

    public ReturnedItem createReturn(Long orderId, Long customerId, ReturnedItem returnData) {
        ShippingOrder order = em.find(ShippingOrder.class, orderId);
        if (order == null || !order.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Invalid Order");
        }
        returnData.setShippingOrder(order);
        em.persist(returnData);
        
        // Update order status
        order.setStatus(ShippingOrder.Status.RETURNED);
        em.merge(order);
        
        return returnData;
    }
    public void updateProfile(Long userId, Long customerId, String fullName, String email, String companyName) {
        User user = em.find(User.class, userId);
        if (user != null) {
            user.setFullName(fullName);
            user.setEmail(email);
            em.merge(user);
        }
        if (customerId != null) {
            Customer customer = em.find(Customer.class, customerId);
            if (customer != null) {
                customer.setCompanyName(companyName);
                em.merge(customer);
            }
        }
    }
    public Customer getCustomer(Long customerId) {
        return em.find(Customer.class, customerId);
    }
}