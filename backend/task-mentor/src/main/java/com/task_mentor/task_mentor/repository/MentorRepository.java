package com.task_mentor.task_mentor.repository;

import com.task_mentor.task_mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MentorRepository - Spring Data JPA repository for Mentor entity
 * Provides CRUD operations and custom query methods for mentors table
 * 
 * @author James No
 */
@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {
    
    /**
     * Find a mentor by user ID
     * Used to get mentor profile when user logs in
     */
    Optional<Mentor> findByUserId(Long userId);
    
    /**
     * Find all mentors by company name
     * Useful for filtering mentors by their company
     */
    List<Mentor> findByCompany(String company);
    
    /**
     * Find mentors by years of experience (greater than or equal to)
     * Useful for filtering experienced mentors
     */
    List<Mentor> findByYearsExperienceGreaterThanEqual(Integer years);
    
    /**
     * Search mentors by name (case-insensitive, partial match)
     * Used for mentor search functionality
     */
    List<Mentor> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find mentors by industry (searches within industries TEXT field)
     * Used for filtering mentors by industry
     */
    @Query("SELECT m FROM Mentor m WHERE LOWER(m.industries) LIKE LOWER(CONCAT('%', :industry, '%'))")
    List<Mentor> findByIndustry(@Param("industry") String industry);
    
    /**
     * Find mentors by expertise area (searches within expertise_areas TEXT field)
     * Used for filtering mentors by specific skills
     */
    @Query("SELECT m FROM Mentor m WHERE LOWER(m.expertiseAreas) LIKE LOWER(CONCAT('%', :expertise, '%'))")
    List<Mentor> findByExpertise(@Param("expertise") String expertise);
    
    /**
     * Check if mentor profile exists for a given user ID
     * Used during mentor profile creation to prevent duplicates
     */
    boolean existsByUserId(Long userId);
}
