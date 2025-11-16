package com.task_mentor.task_mentor.repository;

import com.task_mentor.task_mentor.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * StudentRepository - Spring Data JPA repository for Student entity
 * Provides CRUD operations and custom query methods for students table
 *
 * @author James No
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find a student by user ID
     * Used to get student profile when user logs in
     */
    @Query("SELECT s FROM Student s WHERE s.user.userId = :userId")
    Optional<Student> findByUserId(@Param("userId") Long userId);

    /**
     * Find all students by major
     * Useful for analytics or matching students with relevant mentors
     */
    List<Student> findByMajor(String major);

    /**
     * Find students by graduation year
     * Useful for filtering students by class year
     */
    List<Student> findByGraduationYear(Integer graduationYear);

    /**
     * Find students graduating in or after a specific year
     * Useful for identifying current/upcoming students
     */
    List<Student> findByGraduationYearGreaterThanEqual(Integer year);

    /**
     * Search students by name (case-insensitive, partial match)
     * Used for student search functionality
     */
    List<Student> findByNameContainingIgnoreCase(String name);

    /**
     * Find students by career interest (searches within career_interests TEXT field)
     * Used for matching students with relevant mentors
     */
    @Query("SELECT s FROM Student s WHERE LOWER(s.careerInterests) LIKE LOWER(CONCAT('%', :interest, '%'))")
    List<Student> findByCareerInterest(@Param("interest") String interest);

    /**
     * Check if student profile exists for a given user ID
     * Used during student profile creation to prevent duplicates
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Student s WHERE s.user.userId = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}