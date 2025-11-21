package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.entity.Booking;
import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.repository.BookingRepository;
import com.task_mentor.task_mentor.repository.MentorRepository;
import com.task_mentor.task_mentor.repository.StudentRepository;
import com.task_mentor.task_mentor.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private TaskRepository taskRepository;


    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_DECLINED = "declined";
    public static final String STATUS_CANCELLED = "cancelled";


    public Booking createBooking(Long studentId, Long mentorId, Long taskId, LocalDateTime proposedDatetime) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found with ID: " + mentorId));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));


        if (!task.getMentor().getMentorId().equals(mentorId)) {
            throw new IllegalArgumentException("Task does not belong to the specified mentor");
        }


        validateProposedDatetime(proposedDatetime);


        if (hasConflictingBooking(mentorId, proposedDatetime, task.getDurationMinutes())) {
            throw new IllegalStateException(
                    "Mentor already has a confirmed booking at this time. Please choose another time slot.");
        }


        Booking booking = new Booking(student, mentor, task, proposedDatetime);
        return bookingRepository.save(booking);
    }


    public Booking acceptBooking(Long bookingId, Long mentorId) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getMentor().getMentorId().equals(mentorId)) {
            throw new IllegalStateException("Only the assigned mentor can accept this booking");
        }


        if (!STATUS_PENDING.equals(booking.getStatus())) {
            throw new IllegalStateException("Only pending bookings can be accepted");
        }


        if (hasConflictingBooking(mentorId, booking.getProposedDatetime(),
                booking.getTask().getDurationMinutes(), bookingId)) {
            throw new IllegalStateException(
                    "Cannot accept: Another booking was confirmed for this time slot");
        }

        booking.setStatus(STATUS_ACCEPTED);
        return bookingRepository.save(booking);
    }


    public Booking declineBooking(Long bookingId, Long mentorId) {
        Booking booking = getBookingById(bookingId);


        if (!booking.getMentor().getMentorId().equals(mentorId)) {
            throw new IllegalStateException("Only the assigned mentor can decline this booking");
        }


        if (!STATUS_PENDING.equals(booking.getStatus())) {
            throw new IllegalStateException("Only pending bookings can be declined");
        }

        booking.setStatus(STATUS_DECLINED);
        return bookingRepository.save(booking);
    }


    public Booking cancelBooking(Long bookingId, Long userId, String userType) {
        Booking booking = getBookingById(bookingId);


        if ("student".equalsIgnoreCase(userType)) {
            if (!booking.getStudent().getStudentId().equals(userId)) {
                throw new IllegalStateException("You can only cancel your own bookings");
            }
        } else if ("mentor".equalsIgnoreCase(userType)) {
            if (!booking.getMentor().getMentorId().equals(userId)) {
                throw new IllegalStateException("You can only cancel your own bookings");
            }
        } else {
            throw new IllegalArgumentException("Invalid user type: " + userType);
        }


        if (STATUS_CANCELLED.equals(booking.getStatus()) || STATUS_DECLINED.equals(booking.getStatus())) {
            throw new IllegalStateException("Booking is already " + booking.getStatus());
        }

        booking.setStatus(STATUS_CANCELLED);
        return bookingRepository.save(booking);
    }


    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
    }


    public List<Booking> getBookingsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));
        return bookingRepository.findByStudent(student);
    }


    public List<Booking> getBookingsByMentor(Long mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found with ID: " + mentorId));
        return bookingRepository.findByMentor(mentor);
    }


    public List<Booking> getBookingsByMentorAndStatus(Long mentorId, String status) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found with ID: " + mentorId));

        validateStatus(status);
        return bookingRepository.findByMentorAndStatus(mentor, status);
    }


    public List<Booking> getBookingsByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        return bookingRepository.findByTask(task);
    }


    public List<Booking> getBookingsByStatus(String status) {
        validateStatus(status);
        return bookingRepository.findByStatus(status);
    }


    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }


    public void deleteBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);


        if (STATUS_ACCEPTED.equals(booking.getStatus())) {
            throw new IllegalStateException("Cannot delete accepted bookings. Cancel them instead.");
        }

        bookingRepository.deleteById(bookingId);
    }


    private boolean hasConflictingBooking(Long mentorId, LocalDateTime proposedDatetime,
                                          Integer durationMinutes) {
        return hasConflictingBooking(mentorId, proposedDatetime, durationMinutes, null);
    }


    private boolean hasConflictingBooking(Long mentorId, LocalDateTime proposedDatetime,
                                          Integer durationMinutes, Long excludeBookingId) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));


        List<Booking> acceptedBookings = bookingRepository.findByMentorAndStatus(mentor, STATUS_ACCEPTED);

        LocalDateTime proposedEnd = proposedDatetime.plusMinutes(durationMinutes);

        for (Booking existingBooking : acceptedBookings) {

            if (excludeBookingId != null && existingBooking.getBookingId().equals(excludeBookingId)) {
                continue;
            }

            LocalDateTime existingStart = existingBooking.getProposedDatetime();
            LocalDateTime existingEnd = existingStart.plusMinutes(
                    existingBooking.getTask().getDurationMinutes());


            if (timesOverlap(proposedDatetime, proposedEnd, existingStart, existingEnd)) {
                return true;
            }
        }

        return false;
    }


    private boolean timesOverlap(LocalDateTime start1, LocalDateTime end1,
                                 LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }


    private void validateProposedDatetime(LocalDateTime proposedDatetime) {
        if (proposedDatetime == null) {
            throw new IllegalArgumentException("Proposed datetime is required");
        }

        LocalDateTime now = LocalDateTime.now();
        if (proposedDatetime.isBefore(now)) {
            throw new IllegalArgumentException("Proposed datetime cannot be in the past");
        }


        LocalDateTime maxFuture = now.plusMonths(6);
        if (proposedDatetime.isAfter(maxFuture)) {
            throw new IllegalArgumentException("Proposed datetime cannot be more than 6 months in the future");
        }
    }


    private void validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }

        List<String> validStatuses = List.of(STATUS_PENDING, STATUS_ACCEPTED, STATUS_DECLINED, STATUS_CANCELLED);
        if (!validStatuses.contains(status.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid status. Must be one of: " + String.join(", ", validStatuses));
        }
    }
}
