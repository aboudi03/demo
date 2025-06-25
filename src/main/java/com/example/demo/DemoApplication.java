package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// @Bean
	// public CommandLineRunner dataLoader(
	// 		com.example.demo.repository.UserRepository userRepository,
	// 		com.example.demo.repository.EmployeeRepository employeeRepository,
	// 		com.example.demo.repository.EmployeeOnboardingProcessRepository onboardingRepo,
	// 		com.example.demo.repository.TaskRepository taskRepository,
	// 		org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
	// 	return args -> {
	// 		// Create demo users with hashed passwords
	// 		com.example.demo.entity.User admin = userRepository.findByUsername("admin@mail.com").orElseGet(() -> {
	// 			com.example.demo.entity.User u = new com.example.demo.entity.User();
	// 			u.setUsername("admin@mail.com");
	// 			u.setPassword(passwordEncoder.encode("admin"));
	// 			u.setRole("ADMIN");
	// 			u.setFirstName("Admin");
	// 			return userRepository.save(u);
	// 		});
	// 		com.example.demo.entity.User hr = userRepository.findByUsername("hr@mail.com").orElseGet(() -> {
	// 			com.example.demo.entity.User u = new com.example.demo.entity.User();
	// 			u.setUsername("hr@mail.com");
	// 			u.setPassword(passwordEncoder.encode("hrpass"));
	// 			u.setRole("HR");
	// 			u.setFirstName("HR");
	// 			return userRepository.save(u);
	// 		});
	// 		com.example.demo.entity.User it = userRepository.findByUsername("it@mail.com").orElseGet(() -> {
	// 			com.example.demo.entity.User u = new com.example.demo.entity.User();
	// 			u.setUsername("it@mail.com");
	// 			u.setPassword(passwordEncoder.encode("itpass"));
	// 			u.setRole("IT");
	// 			u.setFirstName("IT");
	// 			return userRepository.save(u);
	// 		});
	// 		com.example.demo.entity.User manager = userRepository.findByUsername("manager@mail.com").orElseGet(() -> {
	// 			com.example.demo.entity.User u = new com.example.demo.entity.User();
	// 			u.setUsername("manager@mail.com");
	// 			u.setPassword(passwordEncoder.encode("managerpass"));
	// 			u.setRole("MANAGER");
	// 			u.setFirstName("Manager");
	// 			return userRepository.save(u);
	// 		});
	// 		// Create employees for each user
	// 		com.example.demo.entity.Employee adminEmp = employeeRepository.findAll().stream().filter(e -> e.getUser().getId().equals(admin.getId())).findFirst().orElseGet(() -> {
	// 			com.example.demo.entity.Employee e = new com.example.demo.entity.Employee();
	// 			e.setUser(admin);
	// 			e.setDepartmentName("Administration");
	// 			return employeeRepository.save(e);
	// 		});
	// 		com.example.demo.entity.Employee hrEmp = employeeRepository.findAll().stream().filter(e -> e.getUser().getId().equals(hr.getId())).findFirst().orElseGet(() -> {
	// 			com.example.demo.entity.Employee e = new com.example.demo.entity.Employee();
	// 			e.setUser(hr);
	// 			e.setDepartmentName("HR");
	// 			return employeeRepository.save(e);
	// 		});
	// 		com.example.demo.entity.Employee itEmp = employeeRepository.findAll().stream().filter(e -> e.getUser().getId().equals(it.getId())).findFirst().orElseGet(() -> {
	// 			com.example.demo.entity.Employee e = new com.example.demo.entity.Employee();
	// 			e.setUser(it);
	// 			e.setDepartmentName("IT");
	// 			return employeeRepository.save(e);
	// 		});
	// 		com.example.demo.entity.Employee managerEmp = employeeRepository.findAll().stream().filter(e -> e.getUser().getId().equals(manager.getId())).findFirst().orElseGet(() -> {
	// 			com.example.demo.entity.Employee e = new com.example.demo.entity.Employee();
	// 			e.setUser(manager);
	// 			e.setDepartmentName("Management");
	// 			return employeeRepository.save(e);
	// 		});
	// 		// Create onboarding process and tasks
	// 		if (onboardingRepo.count() == 0) {
	// 			com.example.demo.entity.EmployeeOnboardingProcess process = new com.example.demo.entity.EmployeeOnboardingProcess();
	// 			onboardingRepo.save(process);
	// 			com.example.demo.entity.Task t1 = new com.example.demo.entity.Task();
	// 			t1.setDescription("HR paperwork");
	// 			t1.setAssignee(hrEmp);
	// 			t1.setProcess(process);
	// 			com.example.demo.entity.Task t2 = new com.example.demo.entity.Task();
	// 			t2.setDescription("IT setup");
	// 			t2.setAssignee(itEmp);
	// 			t2.setProcess(process);
	// 			com.example.demo.entity.Task t3 = new com.example.demo.entity.Task();
	// 			t3.setDescription("Manager orientation");
	// 			t3.setAssignee(managerEmp);
	// 			t3.setProcess(process);
	// 			com.example.demo.entity.Task t4 = new com.example.demo.entity.Task();
	// 			t4.setDescription("Admin approval");
	// 			t4.setAssignee(adminEmp);
	// 			t4.setProcess(process);
	// 			taskRepository.save(t1);
	// 			taskRepository.save(t2);
	// 			taskRepository.save(t3);
	// 			taskRepository.save(t4);
	// 		}
	// 	};
	// }

	@SuppressWarnings("unused")
	@Bean
	public CommandLineRunner dbConnectionChecker(DataSource dataSource) {
		return args -> {
			try (Connection conn = dataSource.getConnection()) {
				if (!conn.isClosed()) {
					System.out.println("[DEBUG] Successfully connected to the database!");
				}
			} catch (Exception e) {
				System.err.println("[DEBUG] Failed to connect to the database: " + e.getMessage());
			}
		};
	}

}
