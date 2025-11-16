package com.task_mentor.task_mentor.service;


import com.task_mentor.task_mentor.dto.AuthResponse;
import com.task_mentor.task_mentor.dto.LoginRequest;
import com.task_mentor.task_mentor.dto.RegisterRequest;
import com.task_mentor.task_mentor.entity.User;
import com.task_mentor.task_mentor.repository.UserRepository;
import com.task_mentor.task_mentor.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountType(request.getRole().toLowerCase());

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getAccountType());

        return new AuthResponse(token, user.getEmail(), user.getAccountType(), user.getUserId());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getAccountType());

        return new AuthResponse(token, user.getEmail(), user.getAccountType(), user.getUserId());
    }
}