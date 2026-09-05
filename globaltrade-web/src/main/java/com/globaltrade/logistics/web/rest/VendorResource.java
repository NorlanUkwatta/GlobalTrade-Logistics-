package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.Vendor;
import com.globaltrade.logistics.service.local.VendorService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/vendors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN"})
public class VendorResource {

    @Inject
    private VendorService vendorService;

    @GET
    public Response getAllVendors() {
        return Response.ok(ApiResponse.success(vendorService.findAll())).build();
    }

    @POST
    public Response createVendor(Vendor vendor) {
        Vendor created = vendorService.createVendor(
            vendor.getCompanyName(),
            vendor.getContactName(),
            vendor.getEmail(),
            vendor.getPhone(),
            vendor.getRegistrationNumber(),
            vendor.getHeadquartersAddress(),
            vendor.getCommodityCategory() != null ? vendor.getCommodityCategory().getId() : null,
            vendor.getStandardLeadTimeDays(),
            vendor.getPickupAddressLine1(),
            vendor.getPickupAddressLine2(),
            vendor.getPickupCity(),
            vendor.getPickupState(),
            vendor.getPickupPostalCode(),
            vendor.getPickupCountry()
        );
        return Response.status(Response.Status.CREATED).entity(ApiResponse.success(created)).build();
    }
}
