package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.Shipment;
import com.globaltrade.logistics.entity.User;
import com.globaltrade.logistics.service.local.ShipmentService;
import com.globaltrade.logistics.service.local.UserService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import com.globaltrade.logistics.web.dto.ShipmentRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/shipments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShipmentResource {

    @Inject
    private ShipmentService shipmentService;
    
    @Inject
    private UserService userService;

    @GET
    @RolesAllowed({"ADMIN", "LOGISTICS_COORD", "VENDOR_REP"})
    public Response getShipments(@Context SecurityContext securityContext) {
        List<Shipment> shipments;
        
        if (securityContext.isUserInRole("VENDOR_REP")) {
            String username = securityContext.getUserPrincipal().getName();
            User user = userService.findByUsernameForAuth(username);
            shipments = shipmentService.findByVendor(user.getVendorId());
        } else {
            shipments = shipmentService.findAll();
        }
        
        return Response.ok(ApiResponse.success(shipments)).build();
    }
    
    @POST
    @RolesAllowed({"ADMIN", "VENDOR_REP"})
    public Response createShipment(ShipmentRequest req, @Context SecurityContext securityContext) {
        Long vendorId = req.getVendorId();
        
        // Force VENDOR_REP to only create for their own vendor ID
        if (securityContext.isUserInRole("VENDOR_REP")) {
            User user = userService.findByUsernameForAuth(securityContext.getUserPrincipal().getName());
            vendorId = user.getVendorId();
        }
        
        Shipment created = shipmentService.createShipment(
            req.getOrigin(), 
            req.getDestination(), 
            vendorId, 
            req.getContainerId(), 
            req.getCustomerEmail(),
            req.getCustomerName(),
            req.getDeliveryAddress()
        );
        return Response.ok(ApiResponse.success(created)).build();
    }
    
    @PUT
    @Path("/{id}/status")
    @RolesAllowed({"ADMIN", "VENDOR_REP", "LOGISTICS_COORD"})
    public Response updateStatus(@PathParam("id") Long id, @QueryParam("status") Shipment.Status status, @QueryParam("location") String location, @QueryParam("remarks") String remarks) {
        Shipment shipment = shipmentService.findById(id);
        
        // Note: The ShipmentServiceBean intercepts this call with @VendorIsolation
        // and throws an exception if a VENDOR_REP tries to update a shipment they don't own!
        shipmentService.updateStatus(shipment, status, location, remarks);
        
        return Response.ok(ApiResponse.success("Status updated to " + status)).build();
    }
}
