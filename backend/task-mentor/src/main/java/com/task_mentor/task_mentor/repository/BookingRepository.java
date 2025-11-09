package com.task_mentor.task_mentor.repository;


import com.task_mentor.task_mentor.entity.Booking;
import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.Task;
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

    // Find all bookings for a given student
    List<Booking> findByStudent(Student student);

    // Find all bookings for a given mentor
    List<Booking> findByMentor(Mentor mentor);

    // Find bookings by task
    List<Booking> findByTask(Task task);

    // Find bookings by status
    List<Booking> findByStatus(String status);

    // Custom JPQL example: find bookings for a mentor by status
    @Query("SELECT b FROM Booking b WHERE b.mentor = :mentor AND b.status = :status")
    List<Booking> findByMentorAndStatus(@Param("mentor") Mentor mentor, @Param("status") String status);
}