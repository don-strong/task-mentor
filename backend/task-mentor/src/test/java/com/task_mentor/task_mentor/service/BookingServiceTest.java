package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.entity.Booking;
import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.repository.BookingRepository;
import com.task_mentor.task_mentor.repository.MentorRepository;
import com.task_mentor.task_mentor.repository.StudentRepository;
import com.task_mentor.task_mentor.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingService
 * Tests business logic for booking management
 *
 * @author Claude
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private BookingService bookingService;

    private Student testStudent;
    private Mentor testMentor;
    private Task testTask;
    private Booking testBooking;
    private LocalDateTime futureDate;

    @BeforeEach
    void setUp() {
        futureDate = LocalDateTime.now().plusDays(7);

        testStudent = new Student();
        testStudent.setStudentId(1L);
        testStudent.setName("John Student");

        testMentor = new Mentor();
        testMentor.setMentorId(2L);
        testMentor.setName("Dr. Mentor");

        testTask = new Task();
        testTask.setTaskId(3L);
        testTask.setMentor(testMentor);
        testTask.setTitle("Resume Review");
        testTask.setDurationMinutes(45);

        testBooking = new Booking();
        testBooking.setBookingId(100L);
        testBooking.setStudent(testStudent);
        testBooking.setMentor(testMentor);
        testBooking.setTask(testTask);
        testBooking.setProposedDatetime(futureDate);
        testBooking.setStatus("pending");
    }

    // ===== CREATE BOOKING TESTS =====

    @Test
    @DisplayName("Create booking - Success")
    void testCreateBooking_Success() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(testMentor));
        when(taskRepository.findById(3L)).thenReturn(Optional.of(testTask));
        when(bookingRepository.findByMentorAndStatus(testMentor, "accepted"))
                .thenReturn(Arrays.asList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Booking result = bookingService.createBooking(1L, 2L, 3L, futureDate);

        // Then
        assertNotNull(result);
        assertEquals("pending", result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Create booking - Student not found")
    void testCreateBooking_StudentNotFound() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(999L, 2L, 3L, futureDate)
        );
        assertTrue(exception.getMessage().contains("Student not found"));
    }

    @Test
    @DisplayName("Create booking - Mentor not found")
    void testCreateBooking_MentorNotFound() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(mentorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(1L, 999L, 3L, futureDate)
        );
        assertTrue(exception.getMessage().contains("Mentor not found"));
    }

    @Test
    @DisplayName("Create booking - Task not found")
    void testCreateBooking_TaskNotFound() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(testMentor));
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(1L, 2L, 999L, futureDate)
        );
        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    @DisplayName("Create booking - Task doesn't belong to mentor")
    void testCreateBooking_TaskNotBelongToMentor() {
        // Given
        Mentor wrongMentor = new Mentor();
        wrongMentor.setMentorId(999L);
        testTask.setMentor(wrongMentor);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(testMentor));
        when(taskRepository.findById(3L)).thenReturn(Optional.of(testTask));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(1L, 2L, 3L, futureDate)
        );
        assertEquals("Task does not belong to the specified mentor", exception.getMessage());
    }

    @Test
    @DisplayName("Create booking - Date in past")
    void testCreateBooking_DateInPast() {
        // Given
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(testMentor));
        when(taskRepository.findById(3L)).thenReturn(Optional.of(testTask));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(1L, 2L, 3L, pastDate)
        );
        assertEquals("Proposed datetime cannot be in the past", exception.getMessage());
    }

    @Test
    @DisplayName("Create booking - Date too far in future")
    void testCreateBooking_DateTooFarFuture() {
        // Given
        LocalDateTime farFuture = LocalDateTime.now().plusMonths(7);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(testMentor));
        when(taskRepository.findById(3L)).thenReturn(Optional.of(testTask));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(1L, 2L, 3L, farFuture)
        );
        assertTrue(exception.getMessage().contains("cannot be more than 6 months"));
    }

    @Test
    @DisplayName("Create booking - Conflicting booking exists")
    void testCreateBooking_ConflictingBooking() {
        // Given
        Booking existingBooking = new Booking();
        existingBooking.setProposedDatetime(futureDate);
        existingBooking.setTask(testTask);
        existingBooking.setStatus("accepted");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(testMentor));
        when(taskRepository.findById(3L)).thenReturn(Optional.of(testTask));
        when(bookingRepository.findByMentorAndStatus(testMentor, "accepted"))
                .thenReturn(Arrays.asList(existingBooking));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookingService.createBooking(1L, 2L, 3L, futureDate)
        );
        assertTrue(exception.getMessage().contains("already has a confirmed booking"));
    }

    // ===== ACCEPT BOOKING TESTS =====

    @Test
    @DisplayName("Accept booking - Success")
    void testAcceptBooking_Success() {
        // Given
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
        // ← FIXED: Added this mock for the conflict check
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(testMentor));
        when(bookingRepository.findByMentorAndStatus(testMentor, "accepted"))
                .thenReturn(Arrays.asList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Booking result = bookingService.acceptBooking(100L, 2L);

        // Then
        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Accept booking - Booking not found")
    void testAcceptBooking_NotFound() {
        // Given
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.acceptBooking(999L, 2L)
        );
        assertTrue(exception.getMessage().contains("Booking not found"));
    }

    @Test
    @DisplayName("Accept booking - Wrong mentor")
    void testAcceptBooking_WrongMentor() {
        // Given
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookingService.acceptBooking(100L, 999L)
        );
        assertEquals("Only the assigned mentor can accept this booking", exception.getMessage());
    }

    @Test
    @DisplayName("Accept booking - Already accepted")
    void testAcceptBooking_AlreadyAccepted() {
        // Given
        testBooking.setStatus("accepted");
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookingService.acceptBooking(100L, 2L)
        );
        assertEquals("Only pending bookings can be accepted", exception.getMessage());
    }

    @Test
    @DisplayName("Accept booking - Conflicting booking exists")
    void testAcceptBooking_ConflictingBooking() {
        // Given
        Booking existingBooking = new Booking();
        existingBooking.setProposedDatetime(futureDate);
        existingBooking.setTask(testTask);
        existingBooking.setStatus("accepted");
        existingBooking.setBookingId(999L); // Different booking ID

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(testMentor));
        when(bookingRepository.findByMentorAndStatus(testMentor, "accepted"))
                .thenReturn(Arrays.asList(existingBooking));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookingService.acceptBooking(100L, 2L)
        );
        assertTrue(exception.getMessage().contains("Another booking was confirmed"));
    }

    // ===== DECLINE BOOKING TESTS =====

    @Test
    @DisplayName("Decline booking - Success")
    void testDeclineBooking_Success() {
        // Given
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Booking result = bookingService.declineBooking(100L, 2L);

        // Then
        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Decline booking - Wrong mentor")
    void testDeclineBooking_WrongMentor() {
        // Given
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookingService.declineBooking(100L, 999L)
        );
        assertEquals("Only the assigned mentor can decline this booking", exception.getMessage());
    }

    // ===== CANCEL BOOKING TESTS =====

    @Test
    @DisplayName("Cancel booking as student - Success")
    void testCancelBooking_AsStudent_Success() {
        // Given
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Booking result = bookingService.cancelBooking(100L, 1L, "student");

        // Then
        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Cancel booking as mentor - Success")
    void testCancelBooking_AsMentor_Success() {
        // Given
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Booking result = bookingService.cancelBooking(100L, 2L, "mentor");

        // Then
        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Cancel booking - Wrong student")
    void testCancelBooking_WrongStudent() {
        // Given
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookingService.cancelBooking(100L, 999L, "student")
        );
        assertEquals("You can only cancel your own bookings", exception.getMessage());
    }

    @Test
    @DisplayName("Cancel booking - Invalid user type")
    void testCancelBooking_InvalidUserType() {
        // Given
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.cancelBooking(100L, 1L, "admin")
        );
        assertTrue(exception.getMessage().contains("Invalid user type"));
    }

    @Test
    @DisplayName("Cancel booking - Already cancelled")
    void testCancelBooking_AlreadyCancelled() {
        // Given
        testBooking.setStatus("cancelled");
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookingService.cancelBooking(100L, 1L, "student")
        );
        assertTrue(exception.getMessage().contains("already cancelled"));
    }

    // ===== GET BOOKING TESTS =====

    @Test
    @DisplayName("Get booking by ID - Success")
    void testGetBookingById_Success() {
        // Given
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        // When
        Booking result = bookingService.getBookingById(100L);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getBookingId());
    }

    @Test
    @DisplayName("Get booking by ID - Not found")
    void testGetBookingById_NotFound() {
        // Given
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getBookingById(999L)
        );
        assertTrue(exception.getMessage().contains("Booking not found"));
    }

    @Test
    @DisplayName("Get bookings by student - Success")
    void testGetBookingsByStudent_Success() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(bookingRepository.findByStudent(testStudent))
                .thenReturn(Arrays.asList(testBooking));

        // When
        List<Booking> results = bookingService.getBookingsByStudent(1L);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Get bookings by mentor - Success")
    void testGetBookingsByMentor_Success() {
        // Given
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(testMentor));
        when(bookingRepository.findByMentor(testMentor))
                .thenReturn(Arrays.asList(testBooking));

        // When
        List<Booking> results = bookingService.getBookingsByMentor(2L);

        // Then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Get bookings by task - Success")
    void testGetBookingsByTask_Success() {
        // Given
        when(taskRepository.findById(3L)).thenReturn(Optional.of(testTask));
        when(bookingRepository.findByTask(testTask))
                .thenReturn(Arrays.asList(testBooking));

        // When
        List<Booking> results = bookingService.getBookingsByTask(3L);

        // Then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Get bookings by status - Success")
    void testGetBookingsByStatus_Success() {
        // Given
        when(bookingRepository.findByStatus("pending"))
                .thenReturn(Arrays.asList(testBooking));

        // When
        List<Booking> results = bookingService.getBookingsByStatus("pending");

        // Then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Get bookings by status - Invalid status")
    void testGetBookingsByStatus_InvalidStatus() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getBookingsByStatus("invalid")
        );
        assertTrue(exception.getMessage().contains("Invalid status"));
    }

    @Test
    @DisplayName("Get bookings by mentor and status - Success")
    void testGetBookingsByMentorAndStatus_Success() {
        // Given
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(testMentor));
        when(bookingRepository.findByMentorAndStatus(testMentor, "pending"))
                .thenReturn(Arrays.asList(testBooking));

        // When
        List<Booking> results = bookingService.getBookingsByMentorAndStatus(2L, "pending");

        // Then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Get all bookings - Success")
    void testGetAllBookings_Success() {
        // Given
        Booking booking2 = new Booking();
        booking2.setBookingId(200L);

        when(bookingRepository.findAll()).thenReturn(Arrays.asList(testBooking, booking2));

        // When
        List<Booking> results = bookingService.getAllBookings();

        // Then
        assertEquals(2, results.size());
    }

    // ===== DELETE BOOKING TESTS =====

    @Test
    @DisplayName("Delete booking - Success (pending)")
    void testDeleteBooking_Success() {
        // Given
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
        doNothing().when(bookingRepository).deleteById(100L);

        // When
        bookingService.deleteBooking(100L);

        // Then
        verify(bookingRepository).deleteById(100L);
    }

    @Test
    @DisplayName("Delete booking - Cannot delete accepted")
    void testDeleteBooking_CannotDeleteAccepted() {
        // Given
        testBooking.setStatus("accepted");
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookingService.deleteBooking(100L)
        );
        assertTrue(exception.getMessage().contains("Cannot delete accepted bookings"));
    }

    @Test
    @DisplayName("Delete booking - Not found")
    void testDeleteBooking_NotFound() {
        // Given
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.deleteBooking(999L)
        );
        assertTrue(exception.getMessage().contains("Booking not found"));
    }
}