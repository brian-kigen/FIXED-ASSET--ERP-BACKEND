package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private List<String> excludeUrls = List.of("/api/users/create-admin");

    public static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationFilter( JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Check if the request URI is in the list of excluded URLs
        String requestURI = request.getRequestURI();
        logger.info("JwtAuthenticationFilter - Request URI: {}", requestURI);

        if (excludeUrls.contains(requestURI)) {
            logger.info("JwtAuthenticationFilter - Skipping JWT validation for excluded URL: {}", requestURI);
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("JwtAuthenticationFilter - No JWT token found in request headers");
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace("Bearer ", "");
        try {
            if (jwtTokenUtil.validateToken(token)) {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtTokenUtil.getSecret())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info("JwtAuthenticationFilter - Authenticated user: {}", username);
            } else {
                logger.warn("JwtAuthenticationFilter - JWT token is invalid");
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            logger.error("JwtAuthenticationFilter - Error parsing JWT token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
