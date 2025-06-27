package com.example.demo.config;

import com.example.demo.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        EmailPasswordAuthenticationFilter emailAuthFilter = new EmailPasswordAuthenticationFilter();
        emailAuthFilter.setAuthenticationManager(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)));

        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/generate-hash", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(emailAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    class EmailPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
        @Override
        public org.springframework.security.core.Authentication attemptAuthentication(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) {
            if (request.getContentType() != null && request.getContentType().contains("application/json")) {
                try {
                    Map<String, String> creds = new ObjectMapper().readValue(
                        request.getInputStream(),
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {}
                    );
                    String email = creds.get("email");
                    String password = creds.get("password");
                    org.springframework.security.authentication.UsernamePasswordAuthenticationToken authRequest =
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(email, password);
                    setDetails(request, authRequest);
                    return this.getAuthenticationManager().authenticate(authRequest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return super.attemptAuthentication(request, response);
        }
    }
} 