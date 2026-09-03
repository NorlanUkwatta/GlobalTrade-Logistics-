package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.*;
import com.globaltrade.logistics.service.DocumentGenerationServiceBean;
import com.globaltrade.logistics.service.IntegrationServiceBean;
import com.globaltrade.logistics.service.NotificationServiceBean;
import com.globaltrade.logistics.service.local.UserService;
import com.globaltrade.logistics.service.local.WarehouseService;
import com.globaltrade.logistics.service.local.VendorPortalService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/vendor-portal")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VendorPortalResource {

    @EJB
    private VendorPortalService vendorPortalService;

    @Inject
    private WarehouseService warehouseService;

    @Inject
    private UserService userService;

    @Inject
    private DocumentGenerationServiceBean documentService;

    @Inject
    private NotificationServiceBean notificationService;

    @Inject
    private IntegrationServiceBean integrationService;

    @Context
    private SecurityContext securityContext;

    private User getVendorUser() {
        String username = securityContext.getUserPrincipal().getName();
        return userService.findByUsernameForAuth(username);
    }

    @GET
    @Path("/orders")
    @RolesAllowed("VENDOR_REP")
    public Response getMyOrders() {
        User user = getVendorUser();
        if (user.getVendorId() == null) {
            return Response.status(Response.Status.FORBIDDEN).entity(ApiResponse.error("User not linked to a vendor.")).build();
        }
        List<PurchaseOrder> orders = warehouseService.findPurchaseOrdersByVendor(user.getVendorId());
        return Response.ok(ApiResponse.success(orders)).build();
    }

    @POST
    @Path("/orders/{id}/decision")
    @RolesAllowed("VENDOR_REP")
    public Response submitDecision(@PathParam("id") Long id, Map<String, String> payload) {
        ShippingOrder order = vendorPortalService.getShippingOrder(id, getVendorUser().getVendorId());
        if(order == null) return Response.status(404).entity(ApiResponse.error("Order not found")).build();
        
        String decision = payload.get("decision");
        String reason = payload.get("reason");
        String date = payload.get("proposedDate");
        
        ShippingOrder.VendorDecision dec = ShippingOrder.VendorDecision.valueOf(decision);
        order = vendorPortalService.submitVendorDecision(id, dec, reason, date);
        
        return Response.ok(ApiResponse.success(order)).build();
    }

    @POST
    @Path("/orders/{id}/asn")
    @RolesAllowed("VENDOR_REP")
    public Response submitASN(@PathParam("id") Long id, AdvancedShippingNotice req) {
        AdvancedShippingNotice asn = vendorPortalService.submitASN(id, req);
        if (asn == null) return Response.status(404).entity(ApiResponse.error("PO not found")).build();

        // Automated Document Actions
        byte[] commercialInvoice = documentService.generateCommercialInvoice(asn);
        byte[] packingList = documentService.generatePackingList(asn);
        
        // Automated Transmission
        integrationService.transmitDocuments(asn, commercialInvoice, packingList);

        return Response.ok(ApiResponse.success("ASN submitted and documents transmitted to Carrier/Customs.", asn)).build();
    }

    @POST
    @Path("/compliance/upload")
    @RolesAllowed("VENDOR_REP")
    public Response uploadCompliance(@QueryParam("type") String type, @QueryParam("fileName") String fileName) {
        User user = getVendorUser();
        ComplianceDocument doc = vendorPortalService.uploadCompliance(user.getVendorId(), type, fileName);
        return Response.ok(ApiResponse.success("Compliance document '" + type + "' uploaded.", doc)).build();
    }

    @GET
    @Path("/reports/scorecard")
    @RolesAllowed("VENDOR_REP")
    @Produces("application/pdf")
    public Response downloadScorecard() {
        User user = getVendorUser();
        Vendor v = vendorPortalService.findVendor(user.getVendorId());
        List<PurchaseOrder> orders = warehouseService.findPurchaseOrdersByVendor(user.getVendorId());
        byte[] pdf = documentService.generateVendorScorecard(v, orders);
        return Response.ok(pdf)
            .header("Content-Disposition", "attachment; filename=Scorecard.pdf")
            .build();
    }
    
    @GET
    @Path("/reports/settlements")
    @RolesAllowed("VENDOR_REP")
    public Response getSettlements() {
        User user = getVendorUser();
        List<PaymentSettlement> settlements = vendorPortalService.getSettlements(user.getVendorId());
        return Response.ok(ApiResponse.success(settlements)).build();
    }

    @GET
    @Path("/reports/settlements/pdf")
    @RolesAllowed("VENDOR_REP")
    @Produces("application/pdf")
    public Response downloadSettlementReport() {
        User user = getVendorUser();
        List<PaymentSettlement> settlements = vendorPortalService.getSettlements(user.getVendorId());
        byte[] pdf = documentService.generatePaymentSettlementReport(settlements);
        return Response.ok(pdf)
            .header("Content-Disposition", "attachment; filename=Settlements.pdf")
            .build();
    }

    @PUT
    @Path("/orders/{id}/status")
    @RolesAllowed("VENDOR_REP")
    public Response updateOrderStatus(@PathParam("id") Long id, Map<String, String> payload) {
        String newStatus = payload.get("status");
        ShippingOrder order = vendorPortalService.updateOrderStatus(id, ShippingOrder.Status.valueOf(newStatus));
        return Response.ok(ApiResponse.success("Order status updated.", order)).build();
    }

    @PUT
    @Path("/orders/{id}/complete")
    @RolesAllowed("VENDOR_REP")
    public Response completeOrder(@PathParam("id") Long id) {
        vendorPortalService.completeOrder(getVendorUser().getVendorId(), id);
        return Response.ok(ApiResponse.success("Order completed.", null)).build();
    }

    @PUT
    @Path("/orders/{id}/ready")
    @RolesAllowed("VENDOR_REP")
    public Response readyForDelivery(@PathParam("id") Long id, java.util.Map<String, Double> payload) {
        Double weight = payload.get("weight");
        if (weight == null) return Response.status(400).entity(ApiResponse.error("Weight is required")).build();
        vendorPortalService.readyForDelivery(getVendorUser().getVendorId(), id, weight);
        return Response.ok(ApiResponse.success("Order marked ready for delivery.", null)).build();
    }

    @PUT
    @Path("/orders/{id}/handover-warehouse")
    @RolesAllowed("VENDOR_REP")
    public Response handoverToWarehouse(@PathParam("id") Long id) {
        vendorPortalService.handoverToWarehouse(getVendorUser().getVendorId(), id);
        return Response.ok(ApiResponse.success("Order handed over to warehouse.", null)).build();
    }

    @PUT
    @Path("/orders/{id}/production")
    @RolesAllowed("VENDOR_REP")
    public Response inProduction(@PathParam("id") Long id) {
        PurchaseOrder po = warehouseService.updatePurchaseOrderStatus(id, PurchaseOrder.Status.IN_PRODUCTION);
        return Response.ok(ApiResponse.success("Order marked as In Production.", po)).build();
    }
    // --- New Shipping Order & Profile Endpoints ---

    @GET
    @Path("/profile")
    @RolesAllowed("VENDOR_REP")
    public Response getProfile() {
        User user = getVendorUser();
        if (user.getVendorId() == null) {
            return Response.status(Response.Status.FORBIDDEN).entity(ApiResponse.error("User not linked to a vendor.")).build();
        }
        Vendor v = vendorPortalService.findVendor(user.getVendorId());
        return Response.ok(ApiResponse.success(v)).build();
    }

    @PUT
    @Path("/profile")
    @RolesAllowed("VENDOR_REP")
    public Response updateProfile(Vendor updatedData) {
        User user = getVendorUser();
        Vendor updated = vendorPortalService.updateProfile(user.getVendorId(), updatedData);
        if (updated == null) return Response.status(404).entity(ApiResponse.error("Vendor not found")).build();
        return Response.ok(ApiResponse.success("Profile updated successfully.", updated)).build();
    }

    @GET
    @Path("/shipping-orders")
    @RolesAllowed("VENDOR_REP")
    public Response getShippingOrders() {
        User user = getVendorUser();
        List<ShippingOrder> orders = vendorPortalService.getShippingOrders(user.getVendorId());
        return Response.ok(ApiResponse.success(orders)).build();
    }

    @GET
    @Path("/returns")
    @RolesAllowed("VENDOR_REP")
    public Response getReturnedItems() {
        User user = getVendorUser();
        List<ReturnedItem> returns = vendorPortalService.getReturnedItems(user.getVendorId());
        return Response.ok(ApiResponse.success(returns)).build();
    }
}







