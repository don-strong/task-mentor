package com.task_mentor.task_mentor.controller;

import com.task_mentor.task_mentor.dto.AuthResponse;
import com.task_mentor.task_mentor.dto.LoginRequest;
import com.task_mentor.task_mentor.dto.RegisterRequest;
import com.task_mentor.task_mentor.entity.User;
import com.task_mentor.task_mentor.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Email already registered"));
            }

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setAccountType(request.getAccountType().toLowerCase());
            user.setCreatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);

            AuthResponse response = AuthResponse.fromUser(savedUser, "Registration successful");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Registration failed: " + e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(Authentication authentication) {
        System.out.println("🔐 Login attempt received");
        System.out.println("Authentication: " + authentication);
        System.out.println("Is Authenticated: " + (authentication != null && authentication.isAuthenticated()));

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ Authentication failed - null or not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid credentials"));
        }

        try {
            String email = authentication.getName();
            System.out.println("✅ Authenticated user email: " + email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("✅ User found in database: " + user.getEmail());

            AuthResponse response = AuthResponse.fromUser(user, "Login successful");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ Error during login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Login failed: " + e.getMessage()));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Not authenticated"));
        }

        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AuthResponse response = AuthResponse.fromUser(user, null);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("User not found: " + e.getMessage()));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }


    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}