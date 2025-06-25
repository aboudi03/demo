package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@SuppressWarnings("unused")
	@Bean
	public CommandLineRunner dataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("admin@mail.com").isEmpty()) {
				User admin = new User();
				admin.setUsername("admin@mail.com");
				admin.setPassword(passwordEncoder.encode("admin"));
				admin.setRole("ADMIN");
				userRepository.save(admin);
			}
			if (userRepository.findByUsername("employee@mail.com").isEmpty()) {
				User employee = new User();
				employee.setUsername("employee@mail.com");
				employee.setPassword(passwordEncoder.encode("admin"));
				employee.setRole("EMPLOYEE");
				userRepository.save(employee);
			}
		};
	}

}
