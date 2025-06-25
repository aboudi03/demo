package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleTestController {

    // Endpoint for admin only
    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminEndpoint() {
        return "Hello, Admin!";
    }

    // Endpoint for non-admin users (HR, IT, Manager, etc.)
    @GetMapping("/user-only")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_IT', 'ROLE_MANAGER')")
    public String userEndpoint() {
        return "Hello, non-admin user!";
    }
} 