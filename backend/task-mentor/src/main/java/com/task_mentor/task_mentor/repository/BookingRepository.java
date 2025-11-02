package com.task_mentor.task_mentor.repository;

import com.task_mentor.task_mentor.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BookingRepository - Spring Data JPA repository for Booking entity
 * Provides CRUD operations and custom query methods for bookings table
 * 
 * @author James No
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Find all bookings for a specific student
     * Used to display student's booking history and upcoming sessions
     */
    List<Booking> findByStudentId(Long studentId);
    
    /**
     * Find all bookings for a specific mentor
     * Used to display mentor's booking requests and scheduled sessions
     */
    List<Booking> findByMentorId(Long mentorId);
    
    /**
     * Find bookings by status
     * Used to filter pending/accepted/declined bookings
     */
    List<Booking> findByStatus(String status);
    
    /**
     * Find bookings for a specific student by status
     * Used for student dashboard to show pending/upcoming/past bookings
     */
    List<Booking> findByStudentIdAndStatus(Long studentId, String status);
    
    /**
     * Find bookings for a specific mentor by status
     * Used for mentor dashboard to show pending requests and confirmed sessions
     */
    List<Booking> findByMentorIdAndStatus(Long mentorId, String status);
    
    /**
     * Find bookings for a specific task
     * Used to see all booking history for a particular task offering
     */
    List<Booking> findByTaskId(Long taskId);
    
    /**
     * Find bookings by proposed datetime range
     * Used for calendar views and preventing double-bookings
     */
    List<Booking> findByProposedDatetimeBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find mentor's bookings within a datetime range
     * Used to check mentor availability and prevent conflicts
     */
    @Query("SELECT b FROM Booking b WHERE b.mentorId = :mentorId " +
           "AND b.proposedDatetime BETWEEN :start AND :end " +
           "AND b.status IN ('pending', 'accepted')")
    List<Booking> findMentorBookingsInRange(
        @Param("mentorId") Long mentorId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    /**
     * Count bookings by status for a specific mentor
     * Used for mentor dashboard statistics
     */
    long countByMentorIdAndStatus(Long mentorId, String status);
    
    /**
     * Count bookings by status for a specific student
     * Used for student dashboard statistics
     */
    long countByStudentIdAndStatus(Long studentId, String status);
    
    /**
     * Find upcoming bookings for a student (accepted bookings in the future)
     * Used for student dashboard
     */
    @Query("SELECT b FROM Booking b WHERE b.studentId = :studentId " +
           "AND b.status = 'accepted' AND b.proposedDatetime > :now " +
           "ORDER BY b.proposedDatetime ASC")
    List<Booking> findUpcomingBookingsForStudent(
        @Param("studentId") Long studentId,
        @Param("now") LocalDateTime now
    );
    
    /**
     * Find upcoming bookings for a mentor (accepted bookings in the future)
     * Used for mentor dashboard
     */
    @Query("SELECT b FROM Booking b WHERE b.mentorId = :mentorId " +
           "AND b.status = 'accepted' AND b.proposedDatetime > :now " +
           "ORDER BY b.proposedDatetime ASC")
    List<Booking> findUpcomingBookingsForMentor(
        @Param("mentorId") Long mentorId,
        @Param("now") LocalDateTime now
    );
}
