package com.task_mentor.task_mentor.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // Public endpoint - no authentication required
    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a PUBLIC endpoint - anyone can access!";
    }

    // Protected endpoint - authentication required
    @GetMapping("/protected")
    public String protectedEndpoint(Authentication authentication) {
        return "Hello " + authentication.getName() + "! This is a PROTECTED endpoint.";
    }
}
