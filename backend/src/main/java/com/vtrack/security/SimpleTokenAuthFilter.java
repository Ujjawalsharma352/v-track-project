package com.vtrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter that validates X-Auth-Token or Authorization Bearer for protected APIs.
 * Token value must match the one issued on login (configured in application.properties).
 */
@Component
public class SimpleTokenAuthFilter extends OncePerRequestFilter {

    private static final String HEADER_AUTH_TOKEN = "X-Auth-Token";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    @Value("${vtrack.auth.token}")
    private String validToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HEADER_AUTH_TOKEN);
        if (token == null && request.getHeader(HEADER_AUTHORIZATION) != null) {
            String auth = request.getHeader(HEADER_AUTHORIZATION);
            if (auth.startsWith("Bearer ")) {
                token = auth.substring(7);
            }
        }
        if (token != null && validToken.equals(token)) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    "admin", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
