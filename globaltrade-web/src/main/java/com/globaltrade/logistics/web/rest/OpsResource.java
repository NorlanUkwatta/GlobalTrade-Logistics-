package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.Country;
import com.globaltrade.logistics.entity.Region;
import com.globaltrade.logistics.entity.Warehouse;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import com.globaltrade.logistics.entity.Shipment;
import com.globaltrade.logistics.entity.ShippingOrder;
import com.globaltrade.logistics.entity.Carrier;
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
import java.util.Map;

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
            opsService.executeNative("ALTER TABLE regions MODIFY COLUMN country_id BIGINT NULL");
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
    public Response assignVendor(@PathParam("id") Long id, Map<String, String> req) {
        String assigneeName = req.get("opsAssigneeName");
        String assignee = assigneeName != null ? assigneeName : securityContext.getUserPrincipal().getName();
        Long vendorId = Long.valueOf(req.get("vendorId").toString());
        ShippingOrder order = opsService.assignVendor(id, vendorId, null, assignee);
        return Response.ok(ApiResponse.success("Vendor assigned successfully", order)).build();
    }

    @PUT
    @Path("/orders/{id}/assign-warehouse")
    public Response assignWarehouse(@PathParam("id") Long id, Map<String, String> payload) {
        String warehouseName = payload.get("warehouseName");
        ShippingOrder order = opsService.assignWarehouseToShippingOrder(id, warehouseName);
        return Response.ok(ApiResponse.success("Warehouse assigned successfully", order)).build();
    }

    @PUT
    @Path("/orders/{id}/assign-carrier")
    public Response assignCarrierToOrder(@PathParam("id") Long id, Map<String, String> payload) {
        Long carrierId = Long.parseLong(payload.get("carrierId"));
        String dimensions = payload.get("dimensions");
        String shipmentDateTime = payload.get("shipmentDateTime");
        ShippingOrder order = opsService.assignCarrierToShippingOrder(id, carrierId, dimensions, shipmentDateTime);
        return Response.ok(ApiResponse.success("Carrier assigned successfully", order)).build();
    }

    @GET
    @Path("/carriers")
    public Response getCarriers() {
        return Response.ok(ApiResponse.success("Fetched carriers", opsService.getAllCarriers())).build();
    }

        @POST
    @Path("/carriers")
    public Response createCarrier(Carrier c) {
        Carrier created = opsService.createCarrier(c);
        return Response.ok(ApiResponse.success("Carrier created", created)).build();
    }

    @PUT
    @Path("/carriers/{id}")
    public Response updateCarrier(@PathParam("id") Long id, Carrier c) {
        Carrier updated = opsService.updateCarrier(id, c);
        return Response.ok(ApiResponse.success("Carrier updated", updated)).build();
    }

    @PUT
    @Path("/orders/{id}/verify-receipt")
    @RolesAllowed({"WAREHOUSE_MGR", "ADMIN"})
    public Response verifyWarehouseReceipt(@PathParam("id") Long id) {
        ShippingOrder order = opsService.verifyWarehouseReceipt(id);
        return Response.ok(ApiResponse.success("Order receipt verified", order)).build();
    }

    @PUT
    @Path("/orders/{id}/ship")
    @RolesAllowed({"WAREHOUSE_MGR", "ADMIN"})
    public Response shipOrder(@PathParam("id") Long id) {
        ShippingOrder order = opsService.shipOrder(id);
        return Response.ok(ApiResponse.success("Order shipped", order)).build();
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
    public static class AcceptVendorRequest {
        public Double weight;
        public String returnType;
        public String returnReason;
        public Integer returnQuantity;
    }

    @PUT
    @Path("/orders/{id}/accept-vendor")
    @RolesAllowed({"WAREHOUSE_MGR", "ADMIN"})
    public Response acceptOrderFromVendor(@PathParam("id") Long id, AcceptVendorRequest req) {
        ShippingOrder order = opsService.acceptOrderFromVendor(id, req.weight, req.returnType, req.returnReason, req.returnQuantity);
        return Response.ok(ApiResponse.success("Order accepted from vendor", order)).build();
    }

    @GET
    @Path("/countries")
    public Response getCountries() {
        return Response.ok(ApiResponse.success(opsService.getAllCountries())).build();
    }

    @POST
    @Path("/countries")
    public Response addCountry(Country country) {
        Country c = opsService.createCountry(country.getName(), country.getCode());
        return Response.ok(ApiResponse.success("Country created", c)).build();
    }

    @GET
    @Path("/regions")
    public Response getRegions() {
        return Response.ok(ApiResponse.success(opsService.getAllRegions())).build();
    }

    @POST
    @Path("/regions")
    public Response addRegion(Region region) {
        Region r = opsService.createRegion(region.getName());
        return Response.ok(ApiResponse.success("Region created", r)).build();
    }

    @GET
    @Path("/warehouse-managers")
    public Response getWarehouseManagers() {
        List<com.globaltrade.logistics.web.dto.UserDTO> dtos = opsService.getWarehouseManagers().stream()
            .map(com.globaltrade.logistics.web.dto.UserDTO::from)
            .collect(Collectors.toList());
        return Response.ok(ApiResponse.success(dtos)).build();
    }

    @GET
    @Path("/warehouses")
    public Response getWarehouses() {
        return Response.ok(ApiResponse.success(opsService.getAllWarehouses())).build();
    }

    @POST
    @Path("/warehouses")
    public Response addWarehouse(Map<String, Object> payload) {
        String name = (String) payload.get("name");
        String addr1 = (String) payload.get("addressLine1");
        String addr2 = (String) payload.get("addressLine2");
        String city = (String) payload.get("city");
        String state = (String) payload.get("state");
        String zip = (String) payload.get("postalCode");
        
        Long countryId = payload.get("countryId") != null && !payload.get("countryId").toString().isEmpty() ? Long.valueOf(payload.get("countryId").toString()) : null;
        Long regionId = payload.get("regionId") != null && !payload.get("regionId").toString().isEmpty() ? Long.valueOf(payload.get("regionId").toString()) : null;
        Long managerId = payload.get("managerId") != null && !payload.get("managerId").toString().isEmpty() ? Long.valueOf(payload.get("managerId").toString()) : null;
        
        boolean active = true;
        if (payload.containsKey("active")) {
            active = Boolean.parseBoolean(payload.get("active").toString());
        }
        
        Warehouse w = opsService.createWarehouse(name, addr1, addr2, city, state, zip, countryId, regionId, managerId, active);
        return Response.ok(ApiResponse.success("Warehouse created", w)).build();
    }
}
