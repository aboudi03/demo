package com.example.demo.controller;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /* ---------- VIEW MY TASKS ---------- */
    @GetMapping("/my-tasks")
    public List<String> myTasks(Authentication auth) {
        System.out.println("[DEBUG] TaskController - Authenticated user: " + auth.getName());
        System.out.println("[DEBUG] TaskController - Authorities: " + auth.getAuthorities());

        // Return all tasks instead of just user's tasks
        return taskRepository.findAll()
                             .stream()
                             .filter(task -> task.getDescription() != null)
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
                                String assigneeUsername = "Unknown";
                                String assigneeName = "Unknown";
                                
                                if (task.getAssignee() != null && task.getAssignee().getUser() != null) {
                                    assigneeUsername = task.getAssignee().getUser().getUsername();
                                    assigneeName = task.getAssignee().getUser().getFirstName();
                                }
                                
                                return new TaskInfo(
                                    task.getId(),
                                    task.getDescription(),
                                    assigneeUsername,
                                    assigneeName,
                                    task.getProcess() != null ? task.getProcess().getId() : null
                                );
                            })
                            .collect(Collectors.toList());
    }

    /* ---------- DTOs ---------- */
    public record TaskInfo(Long id, String description, String assigneeUsername, String assigneeName, Long processId) {}

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
                                            return String.format("Task: %s | Employee ID: %d | User: %s | Department: %s",
                                                task.getDescription(),
                                                task.getAssignee().getId(),
                                                task.getAssignee().getUser().getUsername(),
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