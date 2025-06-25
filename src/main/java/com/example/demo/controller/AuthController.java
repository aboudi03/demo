package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.entity.User;
import com.example.demo.entity.Task;
import com.example.demo.entity.EmployeeOnboardingProcess;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.EmployeeOnboardingProcessRepository;
import com.example.demo.security.JwtUtil;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmployeeOnboardingProcessRepository onboardingRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    // Login endpoint
    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return new LoginResponse(token);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    // Show all onboarding processes (protected)
    @GetMapping("/onboarding")
    public List<Long> showOnboarding() {
        return onboardingRepo.findAll().stream().map(p -> p.getId()).collect(Collectors.toList());
    }

    // Admin starts onboarding process
    @PostMapping("/start-onboarding")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String startOnboarding() {
        // Find users by role
        User hr = userRepository.findByRole("HR").stream().findFirst().orElse(null);
        User it = userRepository.findByRole("IT").stream().findFirst().orElse(null);
        User manager = userRepository.findByRole("MANAGER").stream().findFirst().orElse(null);
        User employee = userRepository.findByRole("EMPLOYEE").stream().findFirst().orElse(null);
        User admin = userRepository.findByRole("ADMIN").stream().findFirst().orElse(null);

        EmployeeOnboardingProcess process = new EmployeeOnboardingProcess();
        onboardingRepo.save(process);

        // Create 5 tasks and assign to users
        Task t1 = new Task(); t1.setDescription("HR paperwork"); t1.setAssignee(hr); t1.setProcess(process);
        Task t2 = new Task(); t2.setDescription("IT setup"); t2.setAssignee(it); t2.setProcess(process);
        Task t3 = new Task(); t3.setDescription("Manager orientation"); t3.setAssignee(manager); t3.setProcess(process);
        Task t4 = new Task(); t4.setDescription("Employee self-onboarding"); t4.setAssignee(employee); t4.setProcess(process);
        Task t5 = new Task(); t5.setDescription("Admin approval"); t5.setAssignee(admin); t5.setProcess(process);

        onboardingRepo.save(process); // Save process first to get ID
        // Save tasks
        taskRepository.save(t1);
        taskRepository.save(t2);
        taskRepository.save(t3);
        taskRepository.save(t4);
        taskRepository.save(t5);

        return "Onboarding process started with 5 tasks.";
    }

    // Authenticated user sees only their tasks
    @GetMapping("/my-tasks")
    public List<String> myTasks(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return List.of();
        return user.getTasks().stream().map(Task::getDescription).collect(Collectors.toList());
    }

    // DTOs
    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    public static class LoginResponse {
        private String token;
        public LoginResponse(String token) { this.token = token; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
} 