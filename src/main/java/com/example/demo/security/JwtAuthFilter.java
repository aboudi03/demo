package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        System.out.println("[DEBUG] JwtAuthFilter processing request: " + request.getRequestURI());
        System.out.println("[DEBUG] Authorization header: " + (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null"));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(token);
                System.out.println("[DEBUG] Extracted username from token: " + username);
            } catch (Exception e) {
                System.out.println("[DEBUG] Error extracting username from token: " + e.getMessage());
            }
        } else if (authHeader != null && authHeader.startsWith("Bearer")) {
            // Handle case where there's no space after "Bearer"
            token = authHeader.substring(6);
            try {
                username = jwtUtil.getUsernameFromToken(token);
                System.out.println("[DEBUG] Extracted username from token (no space): " + username);
            } catch (Exception e) {
                System.out.println("[DEBUG] Error extracting username from token: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    String role = jwtUtil.getRoleFromToken(token);
                    System.out.println("[DEBUG] Extracted role from token: " + role);
                    
                    var authorities = java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role));
                    System.out.println("[DEBUG] Created authorities: " + authorities);
                    
                    org.springframework.security.core.userdetails.User principal =
                        new org.springframework.security.core.userdetails.User(username, "", authorities);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[DEBUG] JwtAuthFilter set authentication: " + SecurityContextHolder.getContext().getAuthentication());
                } else {
                    System.out.println("[DEBUG] Token validation failed");
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error processing JWT token: " + e.getMessage());
            }
        } else {
            System.out.println("[DEBUG] No token found or authentication already exists");
        }
        
        filterChain.doFilter(request, response);
    }
} 