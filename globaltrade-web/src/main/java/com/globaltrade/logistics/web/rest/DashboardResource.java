package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.service.local.DashboardService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    @Inject
    private DashboardService dashboardService;

    @GET
    @Path("/stats")
    @RolesAllowed("ADMIN")
    public Response getAdminStats() {
        return Response.ok(ApiResponse.success(dashboardService.getAdminStats())).build();
    }
}