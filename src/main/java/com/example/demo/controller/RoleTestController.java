package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleTestController {

    // Endpoint for admin only
    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminEndpoint() {
        return "Hello, Admin!";
    }

    // Endpoint for non-admin users (HR, IT, Manager, etc.)
    @GetMapping("/user-only")
    @PreAuthorize("hasAnyAuthority('HR', 'IT', 'MANAGER')")
    public String userEndpoint() {
        return "Hello, non-admin user!";
    }
} 