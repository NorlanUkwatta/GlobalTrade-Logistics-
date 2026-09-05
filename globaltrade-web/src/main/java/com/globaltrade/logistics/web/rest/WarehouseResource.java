package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.InventoryItem;
import com.globaltrade.logistics.entity.PurchaseOrder;
import com.globaltrade.logistics.entity.User;
import com.globaltrade.logistics.service.local.UserService;
import com.globaltrade.logistics.service.local.WarehouseService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/warehouse")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WarehouseResource {

    @Inject
    private WarehouseService warehouseService;
    
    @Inject
    private UserService userService;

    @GET
    @Path("/inventory")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MGR", "LOGISTICS_COORD"})
    public Response getInventory() {
        return Response.ok(ApiResponse.success(warehouseService.findAllInventory())).build();
    }

    @POST
    @Path("/inventory")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MGR"})
    public Response updateInventory(@QueryParam("sku") String sku, @QueryParam("name") String name, @QueryParam("qty") Integer qty, @QueryParam("loc") String loc) {
        InventoryItem item = warehouseService.updateInventory(sku, name, qty, loc);
        return Response.ok(ApiResponse.success(item)).build();
    }

    @GET
    @Path("/orders")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MGR", "LOGISTICS_COORD", "VENDOR_REP"})
    public Response getOrders(@Context SecurityContext ctx) {
        if (ctx.isUserInRole("VENDOR_REP")) {
            User u = userService.findByUsernameForAuth(ctx.getUserPrincipal().getName());
            return Response.ok(ApiResponse.success(warehouseService.findPurchaseOrdersByVendor(u.getVendorId()))).build();
        }
        return Response.ok(ApiResponse.success(warehouseService.findAllPurchaseOrders())).build();
    }

    @POST
    @Path("/orders")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MGR"})
    public Response createOrder(@QueryParam("vendorId") Long vId, @QueryParam("sku") String sku, @QueryParam("qty") Integer qty) {
        PurchaseOrder po = warehouseService.createPurchaseOrder(vId, sku, qty);
        return Response.ok(ApiResponse.success(po)).build();
    }

    @PUT
    @Path("/orders/{id}/status")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MGR", "VENDOR_REP"})
    public Response updateOrderStatus(@PathParam("id") Long id, @QueryParam("status") PurchaseOrder.Status status) {
        PurchaseOrder po = warehouseService.updatePurchaseOrderStatus(id, status);
        return Response.ok(ApiResponse.success(po)).build();
    }
}