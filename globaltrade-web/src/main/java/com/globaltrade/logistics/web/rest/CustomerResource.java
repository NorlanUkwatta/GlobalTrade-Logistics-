package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.*;
import com.globaltrade.logistics.service.CustomerPortalServiceBean;
import com.globaltrade.logistics.entity.User;
import com.globaltrade.logistics.service.local.ShipmentService;
import com.globaltrade.logistics.service.local.UserService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import com.globaltrade.logistics.service.DocumentGenerationServiceBean;
import com.globaltrade.logistics.service.NotificationServiceBean;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

        @Inject
    private CustomerPortalServiceBean portalService;

    @GET
    @Path("/vendors")
    @RolesAllowed("CUSTOMER")
    public Response getVendors() {
        return Response.ok(ApiResponse.success(portalService.getAllVendors())).build();
    }

    @GET
    @Path("/orders")
    @RolesAllowed("CUSTOMER")
    public Response getOrders() {
        return Response.ok(ApiResponse.success(portalService.getOrders(getCustomerUser().getCustomerId()))).build();
    }

    @POST
    @Path("/orders")
    @RolesAllowed("CUSTOMER")
    public Response placeOrder(ShippingOrder orderData) {
        ShippingOrder created = portalService.placeOrder(getCustomerUser().getCustomerId(), orderData);
        return Response.ok(ApiResponse.success(created)).build();
    }

    @PUT
    @Path("/orders/{id}/cancel")
    @RolesAllowed("CUSTOMER")
    public Response cancelOrder(@PathParam("id") Long id) {
        portalService.cancelOrder(id, getCustomerUser().getCustomerId());
        return Response.ok(ApiResponse.success("Order Cancelled", null)).build();
    }

    @DELETE
    @Path("/orders/{id}")
    @RolesAllowed("CUSTOMER")
    public Response deleteOrder(@PathParam("id") Long id) {
        portalService.deleteOrder(id, getCustomerUser().getCustomerId());
        return Response.ok(ApiResponse.success("Order Deleted", null)).build();
    }

    @GET
    @Path("/payments")
    @RolesAllowed("CUSTOMER")
    public Response getPayments() {
        return Response.ok(ApiResponse.success(portalService.getPayments(getCustomerUser().getCustomerId()))).build();
    }

    @GET
    @Path("/returns")
    @RolesAllowed("CUSTOMER")
    public Response getReturns() {
        return Response.ok(ApiResponse.success(portalService.getReturns(getCustomerUser().getCustomerId()))).build();
    }

    @POST
    @Path("/returns")
    @RolesAllowed("CUSTOMER")
    public Response createReturn(@QueryParam("orderId") Long orderId, ReturnedItem returnData) {
        ReturnedItem created = portalService.createReturn(orderId, getCustomerUser().getCustomerId(), returnData);
        return Response.ok(ApiResponse.success(created)).build();
    }

    @Inject
    private ShipmentService shipmentService;

    @Inject
    private UserService userService;

    @Inject
    private DocumentGenerationServiceBean documentService;

    @Inject
    private NotificationServiceBean notificationService;

    @Context
    private SecurityContext securityContext;

    private User getCustomerUser() {
        return userService.findByUsernameForAuth(securityContext.getUserPrincipal().getName());
    }

    @GET
    @Path("/shipments")
    @RolesAllowed({"CUSTOMER", "ADMIN"})
    public Response getMyTracking() {
        User u = getCustomerUser();
        Long cid = u.getCustomerId();
        
        List<Shipment> all = shipmentService.findAll();
        List<Map<String, Object>> sanitized = all.stream()
            .filter(s -> cid.equals(s.getCustomerId()))
            .map(s -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("trackingNumber", s.getTrackingNumber());
                map.put("origin", s.getOrigin());
                map.put("destination", s.getDestination());
                map.put("deliveryAddress", s.getDeliveryAddress());
                map.put("status", s.getStatus().name());
                return map;
            })
            .collect(Collectors.toList());
            
        return Response.ok(ApiResponse.success(sanitized)).build();
    }

    @PUT
    @Path("/{id}/address")
    @RolesAllowed({"CUSTOMER", "ADMIN"})
    public Response updateDeliveryAddress(@PathParam("id") Long id, @QueryParam("address") String address) {
        notificationService.sendEmail("support@globaltradelogistics.com", "Address Change", "Customer requested address change for Shipment " + id + " to " + address);
        return Response.ok(ApiResponse.success("Address update requested successfully.", address)).build();
    }

    @PUT
    @Path("/{id}/feedback")
    @RolesAllowed("CUSTOMER")
    public Response submitFeedback(@PathParam("id") Long id, @QueryParam("rating") Integer rating, @QueryParam("comments") String comments) {
        notificationService.sendEmail("support@globaltradelogistics.com", "Customer Feedback", "Rating: " + rating + " Stars. Comments: " + comments);
        return Response.ok(ApiResponse.success("Feedback submitted successfully. Thank you!", null)).build();
    }

    @GET
    @Path("/reports/history")
    @RolesAllowed("CUSTOMER")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response downloadSpendHistory() {
        Long customerId = getCustomerUser().getCustomerId();
        List<Shipment> all = shipmentService.findAll();
        List<Shipment> shipments = all.stream().filter(s -> customerId.equals(s.getCustomerId())).collect(Collectors.toList());
        
        byte[] excel = documentService.generateCustomerSpendHistory(shipments);
        return Response.ok(excel)
            .header("Content-Disposition", "attachment; filename=SpendHistory.xlsx")
            .build();
    }
    @PUT
    @Path("/profile")
    @RolesAllowed("CUSTOMER")
    public Response updateProfile(Map<String, String> payload) {
        User u = getCustomerUser();
        portalService.updateProfile(
            u.getId(), 
            u.getCustomerId(), 
            payload.get("fullName"), 
            payload.get("email"), 
            payload.get("companyName")
        );
        return Response.ok(ApiResponse.success("Profile updated successfully", null)).build();
    }
    @GET
    @Path("/profile")
    @RolesAllowed("CUSTOMER")
    public Response getProfile() {
        User u = getCustomerUser();
        Map<String, String> data = new HashMap<>();
        data.put("fullName", u.getFullName());
        data.put("email", u.getEmail());
        if (u.getCustomerId() != null) {
            Customer c = portalService.getCustomer(u.getCustomerId());
            if (c != null) {
                data.put("companyName", c.getCompanyName());
            }
        }
        return Response.ok(ApiResponse.success(data)).build();
    }
}