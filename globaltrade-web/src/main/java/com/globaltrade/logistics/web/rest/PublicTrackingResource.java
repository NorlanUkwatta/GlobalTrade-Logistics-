package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.Shipment;
import com.globaltrade.logistics.service.local.ShipmentService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/public/tracking")
@Produces(MediaType.APPLICATION_JSON)
public class PublicTrackingResource {

    @EJB
    private ShipmentService shipmentService;

    @GET
    @Path("/{token}")
    public Response getShipmentByToken(@PathParam("token") String token) {
        Shipment shipment = shipmentService.findByPublicToken(token);
        
        if (shipment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error("Invalid tracking link."))
                .build();
        }
        
        return Response.ok(ApiResponse.success(shipment)).build();
    }
}