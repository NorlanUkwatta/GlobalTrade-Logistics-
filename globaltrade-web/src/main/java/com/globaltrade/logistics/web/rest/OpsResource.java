package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.Shipment;
import com.globaltrade.logistics.entity.ShippingOrder;
import com.globaltrade.logistics.service.OpsServiceBean;
import com.globaltrade.logistics.web.dto.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import com.globaltrade.logistics.service.local.UserService;
import com.globaltrade.logistics.entity.User;

@Path("/ops")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class OpsResource {
    @Context
    private SecurityContext securityContext;

    @Inject
    private UserService userService;
    @GET
    @Path("/fixdb")
    public Response fixDb() {
        try {
            opsService.executeNative("ALTER TABLE shipping_orders MODIFY COLUMN product_design_doc_url VARCHAR(2000)");
            opsService.executeNative("ALTER TABLE shipping_orders MODIFY COLUMN quality_standards_doc_url VARCHAR(2000)");
            return Response.ok("DB Fixed").build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @Inject
    private OpsServiceBean opsService;

    @GET
    @Path("/ping")
    @PermitAll
    public Response ping() {
        return Response.ok("OpsResource is alive!").build();
    }
    
    @GET
    @Path("/test_orders")
    @PermitAll
    public Response testOrders() {
        try {
            return Response.ok(ApiResponse.success(opsService.getAllOrders())).build();
        } catch (Exception e) {
            return Response.status(500).entity(ApiResponse.error(e.getClass().getName() + " - " + e.getMessage())).build();
        }
    }

    @GET
    @Path("/orders")
    public Response getAllOrders() {
        try {
            return Response.ok(ApiResponse.success(opsService.getAllOrders())).build();
        } catch (Exception e) {
            return Response.status(500).entity(ApiResponse.error(e.toString() + " - " + e.getMessage())).build();
        }
    }

    @PUT
    @Path("/orders/{id}/assign")
    public Response assignVendor(@PathParam("id") Long orderId, @QueryParam("vendorId") Long vendorId) {
        User opsUser = userService.findByUsernameForAuth(securityContext.getUserPrincipal().getName());
        ShippingOrder order = opsService.assignVendor(orderId, vendorId, opsUser.getId(), opsUser.getFullName());
        return Response.ok(ApiResponse.success(order)).build();
    }

    @GET
    @Path("/vendors")
    public Response getVendors() {
        return Response.ok(ApiResponse.success(opsService.getAllVendors())).build();
    }

    @GET
    @Path("/shipments")
    public Response getAllShipments() {
        return Response.ok(ApiResponse.success(opsService.getAllShipments())).build();
    }

    @PUT
    @Path("/shipments/{id}/carrier")
    public Response assignCarrier(@PathParam("id") Long id, @QueryParam("carrier") String carrierName) {
        Shipment shipment = opsService.assignCarrier(id, carrierName);
        return Response.ok(ApiResponse.success(shipment)).build();
    }

    @PUT
    @Path("/shipments/{id}/status")
    public Response updateShipmentStatus(@PathParam("id") Long id, @QueryParam("status") Shipment.Status status) {
        Shipment shipment = opsService.updateShipmentStatus(id, status);
        return Response.ok(ApiResponse.success(shipment)).build();
    }
    @GET
    @Path("/categories")
    @PermitAll
    public Response getCategories() {
        return Response.ok(ApiResponse.success(opsService.getAllCommodityCategories())).build();
    }

    @POST
    @Path("/categories")
    public Response createCategory(com.globaltrade.logistics.entity.CommodityCategory cat) {
        return Response.ok(ApiResponse.success(opsService.createCommodityCategory(cat.getName(), cat.getDescription()))).build();
    }

    @DELETE
    @Path("/categories/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        opsService.deleteCommodityCategory(id);
        return Response.ok(ApiResponse.success("Category deleted")).build();
    }
}



