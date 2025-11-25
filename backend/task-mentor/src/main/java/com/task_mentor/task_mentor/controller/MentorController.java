package com.task_mentor.task_mentor.controller;

import com.task_mentor.task_mentor.dto.MentorCreateRequest;
import com.task_mentor.task_mentor.dto.MentorUpdateRequest;
import com.task_mentor.task_mentor.dto.MentorSearchDTO;
import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.service.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/mentors")
@CrossOrigin(origins = "*")
public class MentorController {

    @Autowired
    private MentorService mentorService;

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping
    public ResponseEntity<?> createMentor(@RequestBody MentorCreateRequest request) {
        try {
            Mentor mentor = mentorService.createMentor(
                    request.getUserId(),
                    request.getName(),
                    request.getBio(),
                    request.getRoleTitle(),
                    request.getCompany(),
                    request.getYearsExperience(),
                    request.getIndustries(),
                    request.getExpertiseAreas(),
                    request.getProfilePhotoUrl()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(mentor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getMentorById(@PathVariable Long id) {
        try {
            Mentor mentor = mentorService.getMentorById(id);
            return ResponseEntity.ok(mentor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<List<Mentor>> getAllMentors() {
        List<Mentor> mentors = mentorService.getAllMentors();
        return ResponseEntity.ok(mentors);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMentor(
            @PathVariable Long id,
            @RequestBody MentorUpdateRequest request) {
        try {
            Mentor mentor = mentorService.updateMentorProfile(
                    id,
                    request.getName(),
                    request.getBio(),
                    request.getRoleTitle(),
                    request.getCompany(),
                    request.getYearsExperience(),
                    request.getIndustries(),
                    request.getExpertiseAreas(),
                    request.getProfilePhotoUrl()
            );
            return ResponseEntity.ok(mentor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMentor(@PathVariable Long id) {
        try {
            mentorService.deleteMentorProfile(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }


    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}