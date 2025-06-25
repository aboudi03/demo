package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor          // <<–– replaces @Autowired boilerplate
public class AuthController {

    private final AuthenticationManager             authenticationManager;
    private final JwtUtil                           jwtUtil;
    private final UserRepository                    userRepository;
    private final EmployeeRepository                employeeRepository;
    private final TaskRepository                    taskRepository;
    private final EmployeeOnboardingProcessRepository onboardingRepo;

    /* ---------- LOGIN ---------- */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", token);
            response.put("user", Map.of(
                    "id", user.getId(),
                    "role", user.getRole(),
                    "first_name", user.getFirstName()
            ));
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    /* ---------- DTOs ---------- */
    public record LoginRequest(String username, String password) {}
    
    /* ---------- TEMPORARY DEBUG ENDPOINT ---------- */
    @GetMapping("/generate-hash")
    public Map<String, String> generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin");
        return Map.of(
            "password", "admin",
            "hash", hash,
            "verification", String.valueOf(encoder.matches("admin", hash))
        );
    }

    /* ---------- AUTH TEST ENDPOINT ---------- */
    @GetMapping("/test-auth")
    public Map<String, Object> testAuth(Authentication auth) {
        System.out.println("[DEBUG] Test auth endpoint called");
        System.out.println("[DEBUG] Authenticated user: " + (auth != null ? auth.getName() : "null"));
        System.out.println("[DEBUG] Authorities: " + (auth != null ? auth.getAuthorities() : "null"));
        
        return Map.of(
            "authenticated", auth != null,
            "username", auth != null ? auth.getName() : "null",
            "authorities", auth != null ? auth.getAuthorities().toString() : "null"
        );
    }
}
