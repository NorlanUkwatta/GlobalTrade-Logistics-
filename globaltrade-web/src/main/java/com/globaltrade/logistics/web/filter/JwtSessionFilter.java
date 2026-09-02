package com.globaltrade.logistics.web.filter;

import com.globaltrade.logistics.web.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@WebFilter(filterName = "JwtSessionFilter", urlPatterns = "/*")
public class JwtSessionFilter implements Filter {

    private static final Logger LOG = LogManager.getLogger(JwtSessionFilter.class);

    private static final String AUTH_COOKIE = "auth_token";
    private static final String LOGIN_PAGE  = "/login.jsp";

    private static final Set<String> PUBLIC_PATHS = Set.of(
        "/",
        "/login.jsp",
        "/signup.jsp",
        "/vendor-signup.jsp",
        "/tracking.jsp",
        "/index.html", "/index.jsp"
    );

    private static final Set<String> BYPASS_PREFIXES = Set.of(
        "/api/",
        "/resources/",
        "/javax.faces."
    );

    private static final Set<String> STATIC_EXTENSIONS = Set.of(
        ".css", ".js", ".png", ".jpg", ".jpeg", ".gif",
        ".ico", ".svg", ".woff", ".woff2", ".ttf", ".map"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpReq  = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) resp;

        String contextPath = httpReq.getContextPath();
        String requestUri  = httpReq.getRequestURI();
        String path = requestUri.substring(contextPath.length());
        if (path.isEmpty()) path = "/";

        if (shouldBypass(path)) {
            chain.doFilter(req, resp);
            return;
        }

        httpResp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResp.setHeader("Pragma", "no-cache");
        httpResp.setDateHeader("Expires", 0);

        Claims claims = getClaims(httpReq);
        boolean isAuthenticated = (claims != null);

        if (PUBLIC_PATHS.contains(path)) {
            if (isAuthenticated) {
                httpResp.sendRedirect(contextPath + getDefaultPage(claims));
            } else {
                chain.doFilter(req, resp);
            }
            return;
        }

        if (isAuthenticated) {
            if (isAuthorized(claims, path)) {
                chain.doFilter(req, resp);
            } else {
                LOG.warn("[FILTER] User unauthorized for path: {}", path);
                httpResp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            }
        } else {
            LOG.debug("[FILTER] Unauthenticated access to [{}] - redirecting to login", path);
            httpResp.sendRedirect(contextPath + LOGIN_PAGE);
        }
    }

    private Claims getClaims(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (AUTH_COOKIE.equals(cookie.getName())) {
                String token = cookie.getValue();
                if (token != null && !token.isBlank()) {
                    try {
                        JwtUtil jwtUtil = jakarta.enterprise.inject.spi.CDI.current().select(JwtUtil.class).get();
                        return jwtUtil.validateTokenSilently(token);
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private String getDefaultPage(Claims claims) {
        Object rolesObj = claims.get("roles");
        java.util.Set<String> roles = new java.util.HashSet<>();
        if (rolesObj instanceof java.util.List) {
            for (Object r : (java.util.List<?>) rolesObj) {
                roles.add(String.valueOf(r));
            }
        } else if (rolesObj instanceof String) {
            roles.add(String.valueOf(rolesObj));
        }
        
        if (roles.contains("VENDOR_REP")) return "/vendor-portal.jsp";
        if (roles.contains("CUSTOMER")) return "/customer-portal.jsp";
        if (roles.contains("CUSTOMS_AGENT")) return "/customs.jsp";
        if (roles.contains("LOGISTICS_COORD")) return "/shipments.jsp";
        if (roles.contains("WAREHOUSE_MGR")) return "/warehouse.jsp";
        if (roles.contains("OPS")) return "/ops-portal.jsp";
        return "/dashboard.jsp"; // Default for ADMIN and others
    }

    private boolean isAuthorized(Claims claims, String path) {
        Object rolesObj = claims.get("roles");
        java.util.Set<String> roles = new java.util.HashSet<>();
        if (rolesObj instanceof java.util.List) {
            for (Object r : (java.util.List<?>) rolesObj) {
                roles.add(String.valueOf(r));
            }
        } else if (rolesObj instanceof String) {
            roles.add(String.valueOf(rolesObj));
        }
        
        LOG.info("[FILTER] Checking auth for path {}, user roles: {}", path, roles);

        if (path.equals("/dashboard.jsp")) return roles.contains("ADMIN") || roles.contains("LOGISTICS_COORD");
        if (path.equals("/shipments.jsp")) return roles.contains("ADMIN") || roles.contains("LOGISTICS_COORD");
        if (path.equals("/warehouse.jsp")) return roles.contains("ADMIN") || roles.contains("WAREHOUSE_MGR") || roles.contains("LOGISTICS_COORD");
        if (path.equals("/vendors.jsp")) return roles.contains("ADMIN");
        if (path.equals("/customs.jsp")) return roles.contains("ADMIN") || roles.contains("CUSTOMS_AGENT");
        if (path.startsWith("/users/")) return roles.contains("ADMIN");
        if (path.equals("/audit.jsp")) return roles.contains("ADMIN");
        if (path.equals("/vendor-portal.jsp")) return roles.contains("VENDOR_REP");
        if (path.equals("/customer-portal.jsp")) return roles.contains("CUSTOMER");
        if (path.equals("/ops-portal.jsp")) return roles.contains("OPS");
        
        return true; 
    }

    private boolean shouldBypass(String path) {
        for (String prefix : BYPASS_PREFIXES) {
            if (path.startsWith(prefix)) return true;
        }
        for (String ext : STATIC_EXTENSIONS) {
            if (path.toLowerCase().endsWith(ext)) return true;
        }
        return false;
    }

    @Override
    public void init(FilterConfig config) {}

    @Override
    public void destroy() {}
}