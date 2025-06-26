package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
public class OnboardingController {

    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final EmployeeOnboardingProcessRepository onboardingRepo;

    public OnboardingController(EmployeeRepository employeeRepository, 
                               TaskRepository taskRepository, 
                               EmployeeOnboardingProcessRepository onboardingRepo) {
        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
        this.onboardingRepo = onboardingRepo;
    }

    /* ---------- ADMIN ONLY ---------- */
    @PostMapping("/start-onboarding")
    @PreAuthorize("hasRole('ADMIN')")
    public String startOnboarding(Authentication auth) {
        try {
            System.out.println("[DEBUG] Authenticated user: " + auth.getName());
            System.out.println("[DEBUG] Authorities: " + auth.getAuthorities());

            System.out.println("[DEBUG] Fetching employees from database...");
            List<Employee> employees = employeeRepository.findAll();
            System.out.println("[DEBUG] Found " + employees.size() + " employees");

            if (employees.isEmpty()) {
                System.out.println("[DEBUG] No employees found, returning early");
                return "No employees found.";
            }

            System.out.println("[DEBUG] Creating new onboarding process...");
            Random rnd = new Random();
            EmployeeOnboardingProcess proc = onboardingRepo.save(new EmployeeOnboardingProcess());
            System.out.println("[DEBUG] Created process with ID: " + proc.getId());

            System.out.println("[DEBUG] Creating tasks...");
            List<Task> tasks = List.of("HR paperwork", "IT setup",
                                       "Manager orientation", "Employee self-onboarding",
                                       "Admin approval")
                                   .stream()
                                   .map(desc -> {
                                       Task t = new Task();
                                       t.setDescription(desc);
                                       t.setAssignee(employees.get(rnd.nextInt(employees.size())));
                                       t.setProcess(proc);
                                       System.out.println("[DEBUG] Created task: " + desc + " assigned to employee ID: " + t.getAssignee().getId());
                                       return t;
                                   }).toList();

            System.out.println("[DEBUG] Saving " + tasks.size() + " tasks to database...");
            taskRepository.saveAll(tasks);
            System.out.println("[DEBUG] Tasks saved successfully");

            return "Onboarding process started with " + tasks.size() + " tasks.";
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in startOnboarding: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to start onboarding process: " + e.getMessage(), e);
        }
    }

    /* ---------- TEST ENDPOINT ---------- */
    @GetMapping("/test-db")
    public String testDatabase() {
        try {
            System.out.println("[DEBUG] Testing database connection...");
            
            long employeeCount = employeeRepository.count();
            System.out.println("[DEBUG] Employee count: " + employeeCount);
            
            long taskCount = taskRepository.count();
            System.out.println("[DEBUG] Task count: " + taskCount);
            
            long processCount = onboardingRepo.count();
            System.out.println("[DEBUG] Process count: " + processCount);
            
            return String.format("Database test successful. Employees: %d, Tasks: %d, Processes: %d", 
                               employeeCount, taskCount, processCount);
        } catch (Exception e) {
            System.err.println("[ERROR] Database test failed: " + e.getMessage());
            e.printStackTrace();
            return "Database test failed: " + e.getMessage();
        }
    }
} 