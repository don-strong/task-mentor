package com.task_mentor.task_mentor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    // Password Encoder Bean: encrypts passwords using BCrypt, automatically salts and hashes passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security Filter Chain Bean: configures HTTP security settings
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configure which endpoints need authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/test/public" ).permitAll() // Public endpoints (login, register)
                        .anyRequest().authenticated() // Everything else requires authentication
                )
                // Enable HTTP Basic Authentication
                .httpBasic(Customizer.withDefaults())
                // ! Disable CSRF for REST APIs (required for Postman testing ! re-Enable after testing !)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
