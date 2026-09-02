package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.service.local.AuditService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/audit")
@Produces(MediaType.APPLICATION_JSON)
public class AuditResource {

    @Inject
    private AuditService auditService;

    @GET
    @RolesAllowed("ADMIN")
    public Response getAuditLogs(@QueryParam("limit") @DefaultValue("100") int limit) {
        return Response.ok(ApiResponse.success(auditService.findAll(limit))).build();
    }
}