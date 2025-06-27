package com.example.demo.controller;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TaskController {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    public TaskController(TaskRepository taskRepository, EmployeeRepository employeeRepository) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
    }

    /* ---------- VIEW MY TASKS ---------- */
    @GetMapping("/my-tasks")
    public List<String> myTasks(Authentication auth) {
        System.out.println("[DEBUG] TaskController - Authenticated user: " + auth.getName());
        System.out.println("[DEBUG] TaskController - Authorities: " + auth.getAuthorities());

        // Get the employee for the authenticated user
        var employeeOpt = employeeRepository.findByUserEmail(auth.getName());
        if (employeeOpt.isEmpty()) {
            return List.of();
        }
        var employee = employeeOpt.get();
        var userRole = employee.getUser().getRole();

        // Only return tasks relevant to the user's role
        return taskRepository.findAll().stream()
            .filter(task -> task.getDescription() != null)
            .filter(task -> {
                String desc = task.getDescription().toLowerCase();
                if (userRole.equalsIgnoreCase("HR")) return desc.contains("hr");
                if (userRole.equalsIgnoreCase("IT")) return desc.contains("it");
                if (userRole.equalsIgnoreCase("MANAGER")) return desc.contains("manager");
                if (userRole.equalsIgnoreCase("EMPLOYEE")) return desc.contains("employee");
                if (userRole.equalsIgnoreCase("ADMIN")) return desc.contains("admin");
                return false;
            })
            .map(Task::getDescription)
            .collect(Collectors.toList());
    }

    /* ---------- VIEW ALL TASKS (ADMIN ONLY) ---------- */
    @GetMapping("/all-tasks")
    public List<TaskInfo> allTasks(Authentication auth) {
        System.out.println("[DEBUG] TaskController - All tasks requested by: " + auth.getName());
        
        return taskRepository.findAll()
                            .stream()
                            .map(task -> {
                                String assigneeEmail = "Unknown";
                                String assigneeName = "Unknown";
                                
                                if (task.getAssignee() != null && task.getAssignee().getUser() != null) {
                                    assigneeEmail = task.getAssignee().getUser().getEmail();
                                    assigneeName = task.getAssignee().getUser().getFirstName();
                                }
                                
                                return new TaskInfo(
                                    task.getId(),
                                    task.getDescription(),
                                    assigneeEmail,
                                    assigneeName,
                                    task.getProcess() != null ? task.getProcess().getId() : null
                                );
                            })
                            .collect(Collectors.toList());
    }

    /* ---------- DTOs ---------- */
    public record TaskInfo(Long id, String description, String assigneeEmail, String assigneeName, Long processId) {}

    /* ---------- DEBUG ENDPOINT ---------- */
    @GetMapping("/debug-employees")
    public String debugEmployees() {
        StringBuilder result = new StringBuilder();
        result.append("=== EMPLOYEE DEBUG INFO ===\n");
        
        // Get all employees with their user info
        var employees = taskRepository.findAll()
                                    .stream()
                                    .map(task -> {
                                        if (task.getAssignee() != null && task.getAssignee().getUser() != null) {
                                            return String.format("Task: %s | Employee ID: %d | Email: %s | Department: %s",
                                                task.getDescription(),
                                                task.getAssignee().getId(),
                                                task.getAssignee().getUser().getEmail(),
                                                task.getAssignee().getDepartmentName());
                                        }
                                        return String.format("Task: %s | No assignee", task.getDescription());
                                    })
                                    .collect(Collectors.toList());
        
        result.append("Task Assignments:\n");
        employees.forEach(emp -> result.append(emp).append("\n"));
        
        return result.toString();
    }
} 