package com.globaltrade.logistics.web.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ApiCacheControlFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Prevent browser caching for all REST API endpoints
        responseContext.getHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
        responseContext.getHeaders().add("Pragma", "no-cache");
        responseContext.getHeaders().add("Expires", "0");
    }
}