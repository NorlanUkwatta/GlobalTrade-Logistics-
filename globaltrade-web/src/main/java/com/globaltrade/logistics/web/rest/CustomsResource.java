package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.CustomsDeclaration;
import com.globaltrade.logistics.service.local.CustomsService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/customs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomsResource {

    @Inject
    private CustomsService customsService;

    @GET
    @RolesAllowed({"ADMIN", "CUSTOMS_AGENT"})
    public Response getAllDeclarations() {
        List<CustomsDeclaration> list = customsService.findAll();
        return Response.ok(ApiResponse.success(list)).build();
    }

    @POST
    @Path("/{shipmentId}")
    @RolesAllowed({"ADMIN", "VENDOR_REP", "LOGISTICS_COORD"})
    public Response submitDeclaration(@PathParam("shipmentId") Long shipmentId, @QueryParam("dutyAmount") Double dutyAmount) {
        if (dutyAmount == null) dutyAmount = 0.0;
        CustomsDeclaration decl = customsService.submitDeclaration(shipmentId, dutyAmount);
        return Response.ok(ApiResponse.success(decl)).build();
    }
    
    @PUT
    @Path("/{declarationId}/status")
    @RolesAllowed({"ADMIN", "CUSTOMS_AGENT"})
    public Response updateStatus(@PathParam("declarationId") Long declarationId, @QueryParam("status") CustomsDeclaration.Status status, @QueryParam("remarks") String remarks) {
        CustomsDeclaration decl = customsService.updateStatus(declarationId, status, remarks);
        return Response.ok(ApiResponse.success("Status updated.", decl)).build();
    }
}