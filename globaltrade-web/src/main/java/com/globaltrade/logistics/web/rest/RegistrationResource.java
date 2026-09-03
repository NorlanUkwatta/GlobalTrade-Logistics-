package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.User;
import com.globaltrade.logistics.service.local.UserService;
import com.globaltrade.logistics.web.dto.ApiResponse;
import com.globaltrade.logistics.web.dto.CustomerSignupRequest;
import com.globaltrade.logistics.web.dto.VendorSignupRequest;
import com.globaltrade.logistics.web.dto.UserDTO;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class RegistrationResource {

    private static final Logger LOG = LogManager.getLogger(RegistrationResource.class);

    @EJB
    private UserService userService;

    @POST
    @Path("/customer")
    @PermitAll
    public Response registerCustomer(CustomerSignupRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("All fields are required."))
                .build();
        }

        User registered = userService.registerCustomer(
            request.username(),
            request.email(),
            request.fullName(),
            request.password(),
            request.companyName(),
            request.countryCode()
        );

        LOG.info("[REGISTRATION] New customer registered: {}", registered.getUsername());

        return Response.status(Response.Status.CREATED)
            .entity(ApiResponse.success("Registration successful! You can now log in.", UserDTO.from(registered)))
            .build();
    }

    @POST
    @Path("/vendor")
    @PermitAll
    public Response registerVendor(VendorSignupRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("All fields are required."))
                .build();
        }

        User registered = userService.registerVendor(
            request.username(),
            request.email(),
            request.fullName(),
            request.password(),
            request.companyName(), request.phone(), request.registrationNumber(), request.headquartersAddress(), request.commodityCategoryId(), request.standardLeadTimeDays(), request.pickupAddressLine1(), request.pickupAddressLine2(), request.pickupCity(), request.pickupState(), request.pickupPostalCode(), request.pickupCountry()
        );

        LOG.info("[REGISTRATION] New vendor registered: {}", registered.getUsername());

        return Response.status(Response.Status.CREATED)
            .entity(ApiResponse.success("Vendor registration successful! You can now log in.", UserDTO.from(registered)))
            .build();
    }
}

