package com.globaltrade.logistics.web.rest;

import com.globaltrade.logistics.entity.User;
import com.globaltrade.logistics.entity.UserRole;
import com.globaltrade.logistics.exception.InsufficientPermissionException;
import com.globaltrade.logistics.service.local.UserService;
import com.globaltrade.logistics.web.dto.*;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class UserResource {

    private static final Logger LOG = LogManager.getLogger(UserResource.class);

    @EJB
    private UserService userService;

    @Context
    private SecurityContext securityContext;

    @GET
    @RolesAllowed("ADMIN")
    public Response getAllUsers() {
        return Response.ok(ApiResponse.success(userService.listAll())).build();
    }

    @POST
    @RolesAllowed("ADMIN")
    public Response createUser(CreateUserRequest request) {
        if (request == null || request.username() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("Request body is required."))
                .build();
        }

        UserRole role;
        try {
            role = UserRole.valueOf(request.role());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("Invalid or missing role."))
                .build();
        }

        String callerName = securityContext.getUserPrincipal().getName();
        User created = userService.createUser(
            request.username(), request.email(), request.fullName(),
            request.password(), role,
            request.vendorId(), request.customerId(),
            callerName
        );

        LOG.info("[USER-MGMT] User created: id={}, username={}, role={}, by={}",
            created.getId(), created.getUsername(), created.getRole(), callerName);

        return Response.status(Response.Status.CREATED)
            .entity(ApiResponse.success("User created successfully.", UserDTO.from(created)))
            .build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({"ADMIN", "LOGISTICS_COORD", "WAREHOUSE_MGR", "VENDOR_REP", "CUSTOMS_AGENT", "CUSTOMER"})
    public Response getMyProfile() {
        String username = securityContext.getUserPrincipal().getName();
        User user = userService.findByUsernameForAuth(username);
        return Response.ok(ApiResponse.success(UserDTO.from(user))).build();
    }

    @PUT
    @Path("/me/password")
    @RolesAllowed({"ADMIN", "LOGISTICS_COORD", "WAREHOUSE_MGR", "VENDOR_REP", "CUSTOMS_AGENT", "CUSTOMER"})
    public Response changePassword(ChangePasswordRequest request) {
        if (request == null || request.currentPassword() == null || request.newPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("Current and new passwords are required."))
                .build();
        }

        String username = securityContext.getUserPrincipal().getName();
        User user = userService.findByUsernameForAuth(username);
        userService.changePassword(user.getId(), request.currentPassword(), request.newPassword(), username);

        return Response.ok(ApiResponse.success("Password changed successfully.", null)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "LOGISTICS_COORD"})
    public Response getUserById(@PathParam("id") Long id) {
        User user = userService.findById(id);
        return Response.ok(ApiResponse.success(UserDTO.from(user))).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response updateUser(@PathParam("id") Long id, UpdateUserRequest request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("Request body is required."))
                .build();
        }

        String callerName = securityContext.getUserPrincipal().getName();
        User updated = userService.updateUser(id, request.email(), request.fullName(),
            request.vendorId(), request.customerId(), callerName);

        return Response.ok(ApiResponse.success("User updated successfully.", UserDTO.from(updated))).build();
    }

    @PUT
    @Path("/{id}/suspend")
    @RolesAllowed("ADMIN")
    public Response suspendUser(@PathParam("id") Long id, SuspendUserRequest request) {
        if (request == null || request.reason() == null || request.reason().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("A suspension reason is required."))
                .build();
        }

        String callerName = securityContext.getUserPrincipal().getName();
        User targetUser = userService.findById(id);
        if (callerName.equals(targetUser.getUsername())) {
            throw InsufficientPermissionException.forOperation(callerName, "self-suspend");
        }

        User suspended = userService.suspendUser(id, request.reason(), callerName);
        LOG.warn("[USER-MGMT] Account SUSPENDED: id={}, username={}, by={}, reason='{}'",
            id, suspended.getUsername(), callerName, request.reason());

        return Response.ok(ApiResponse.success(
            "Account [" + suspended.getUsername() + "] has been suspended.",
            UserDTO.from(suspended)
        )).build();
    }

    @PUT
    @Path("/{id}/activate")
    @RolesAllowed("ADMIN")
    public Response activateUser(@PathParam("id") Long id) {
        String callerName = securityContext.getUserPrincipal().getName();
        User activated = userService.activateUser(id, callerName);
        LOG.info("[USER-MGMT] Account RE-ACTIVATED: id={}, username={}, by={}",
            id, activated.getUsername(), callerName);

        return Response.ok(ApiResponse.success(
            "Account [" + activated.getUsername() + "] has been re-activated.",
            UserDTO.from(activated)
        )).build();
    }

    @PUT
    @Path("/{id}/reset-password")
    @RolesAllowed("ADMIN")
    public Response resetPassword(@PathParam("id") Long id) {
        String adminName = securityContext.getUserPrincipal().getName();
        String tempPass = userService.resetPassword(id, adminName);
        return Response.ok(ApiResponse.success("Password reset successfully. Temp password: " + tempPass, tempPass)).build();
    }

    @DELETE
    @Path("/{id}")
    @DenyAll
    public Response deleteUser(@PathParam("id") Long id) {
        return Response.status(Response.Status.METHOD_NOT_ALLOWED)
            .entity(ApiResponse.error("User deletion is permanently disallowed. Use PUT /api/users/" + id + "/suspend to revoke access."))
            .build();
    }
}