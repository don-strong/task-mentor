package com.task_mentor.task_mentor.service;


import com.task_mentor.task_mentor.entity.User;
import com.task_mentor.task_mentor.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔍 Loading user by email: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ User not found: " + email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        System.out.println("✅ User found: " + user.getEmail());
        System.out.println("   Account Type: " + user.getAccountType());
        System.out.println("   Password starts with: " + user.getPassword().substring(0, 10));

        String role = "ROLE_" + user.getAccountType().toUpperCase();
        System.out.println("   Assigned role: " + role);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .disabled(false)
                .build();

        System.out.println("✅ UserDetails created successfully");
        return userDetails;
    }}