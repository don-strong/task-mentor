package com.task_mentor.task_mentor.repository;

import com.task_mentor.task_mentor.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TaskRepository - Spring Data JPA repository for Task entity
 * Provides CRUD operations and custom query methods for tasks table
 *
 * @author James No
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks offered by a specific mentor
     * Used to display a mentor's task menu
     */
    @Query("SELECT t FROM Task t WHERE t.mentor.mentorId = :mentorId")
    List<Task> findByMentorId(@Param("mentorId") Long mentorId);

    /**
     * Find tasks by category
     * Used for filtering/browsing tasks by type (e.g., "Resume Review", "Interview Prep")
     */
    List<Task> findByCategory(String category);

    /**
     * Find tasks by duration range (less than or equal to specified minutes)
     * Useful for students looking for quick sessions
     */
    List<Task> findByDurationMinutesLessThanEqual(Integer maxDuration);

    /**
     * Find tasks by duration range (greater than or equal to specified minutes)
     * Useful for students looking for longer, in-depth sessions
     */
    List<Task> findByDurationMinutesGreaterThanEqual(Integer minDuration);

    /**
     * Search tasks by title (case-insensitive, partial match)
     * Used for task search functionality
     */
    List<Task> findByTitleContainingIgnoreCase(String title);

    /**
     * Find tasks by mentor ID and category
     * Useful for organizing a mentor's tasks by category
     */
    @Query("SELECT t FROM Task t WHERE t.mentor.mentorId = :mentorId AND t.category = :category")
    List<Task> findByMentorIdAndCategory(@Param("mentorId") Long mentorId, @Param("category") String category);

    /**
     * Count number of tasks offered by a specific mentor
     * Used for mentor profile statistics
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.mentor.mentorId = :mentorId")
    long countByMentorId(@Param("mentorId") Long mentorId);
}