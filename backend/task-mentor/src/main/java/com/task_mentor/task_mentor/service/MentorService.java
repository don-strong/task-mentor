package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.repository.MentorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * MentorService - Business logic for Mentor profile management
 * Handles CRUD operations, validation, and business rules for mentor profiles
 * 
 * @author James No
 */
@Service
@Transactional
public class MentorService {
    
    private final MentorRepository mentorRepository;
    
    public MentorService(MentorRepository mentorRepository) {
        this.mentorRepository = mentorRepository;
    }
    
    /**
     * Create a new mentor profile
     * Validates that mentor doesn't already exist for this user
     */
    public Mentor createMentor(Mentor mentor) {
        // Validation: Check if mentor profile already exists for this user
        if (mentorRepository.existsByUserId(mentor.getUserId())) {
            throw new IllegalArgumentException("Mentor profile already exists for user ID: " + mentor.getUserId());
        }
        
        // Validation: Required fields
        validateMentor(mentor);
        
        return mentorRepository.save(mentor);
    }
    
    /**
     * Get mentor by mentor ID
     */
    public Optional<Mentor> getMentorById(Long mentorId) {
        return mentorRepository.findById(mentorId);
    }
    
    /**
     * Get mentor profile by user ID
     * Used when a logged-in user wants to view/edit their mentor profile
     */
    public Optional<Mentor> getMentorByUserId(Long userId) {
        return mentorRepository.findByUserId(userId);
    }
    
    /**
     * Get all mentors
     * Used for mentor discovery/search features
     */
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }
    
    /**
     * Update mentor profile
     */
    public Mentor updateMentor(Long mentorId, Mentor updatedMentor) {
        Mentor existing = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found with ID: " + mentorId));
        
        // Validation: Required fields
        validateMentor(updatedMentor);
        
        // Update fields
        existing.setName(updatedMentor.getName());
        existing.setBio(updatedMentor.getBio());
        existing.setRoleTitle(updatedMentor.getRoleTitle());
        existing.setCompany(updatedMentor.getCompany());
        existing.setYearsExperience(updatedMentor.getYearsExperience());
        existing.setIndustries(updatedMentor.getIndustries());
        existing.setExpertiseAreas(updatedMentor.getExpertiseAreas());
        existing.setProfilePhotoUrl(updatedMentor.getProfilePhotoUrl());
        
        return mentorRepository.save(existing);
    }
    
    /**
     * Delete mentor profile
     */
    public void deleteMentor(Long mentorId) {
        if (!mentorRepository.existsById(mentorId)) {
            throw new IllegalArgumentException("Mentor not found with ID: " + mentorId);
        }
        mentorRepository.deleteById(mentorId);
    }
    
    /**
     * Search mentors by company
     */
    public List<Mentor> getMentorsByCompany(String company) {
        return mentorRepository.findByCompany(company);
    }
    
    /**
     * Search mentors by minimum years of experience
     */
    public List<Mentor> getMentorsByExperience(Integer minYears) {
        return mentorRepository.findByYearsExperienceGreaterThanEqual(minYears);
    }
    
    /**
     * Search mentors by name (partial match, case-insensitive)
     */
    public List<Mentor> searchMentorsByName(String name) {
        return mentorRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Search mentors by industry
     */
    public List<Mentor> getMentorsByIndustry(String industry) {
        return mentorRepository.findByIndustry(industry);
    }
    
    /**
     * Search mentors by expertise area
     */
    public List<Mentor> getMentorsByExpertise(String expertise) {
        return mentorRepository.findByExpertise(expertise);
    }
    
    /**
     * Validate mentor data
     * Business rules for mentor profiles
     */
    private void validateMentor(Mentor mentor) {
        if (mentor.getName() == null || mentor.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Mentor name is required");
        }
        
        if (mentor.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        
        // Optional: Validate years of experience is reasonable
        if (mentor.getYearsExperience() != null) {
            if (mentor.getYearsExperience() < 0 || mentor.getYearsExperience() > 70) {
                throw new IllegalArgumentException("Years of experience must be between 0 and 70");
            }
        }
    }
}
