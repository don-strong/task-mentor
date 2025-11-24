package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.entity.Booking;
import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.repository.BookingRepository;
import com.task_mentor.task_mentor.repository.MentorRepository;
import com.task_mentor.task_mentor.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService
 * Tests business logic in isolation using mocked dependencies
 *
 * @author Claude
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private TaskService taskService;

    private Mentor testMentor;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testMentor = new Mentor();
        testMentor.setMentorId(1L);
        testMentor.setName("Dr. Smith");
        testMentor.setCompany("Tech Corp");
        testMentor.setYearsExperience(10);

        testTask = new Task();
        testTask.setTaskId(100L);
        testTask.setMentor(testMentor);
        testTask.setTitle("Resume Review Session");
        testTask.setDescription("Professional resume review with detailed feedback and suggestions");
        testTask.setDurationMinutes(45);
        testTask.setCategory("Resume Review");
        testTask.setCreatedAt(LocalDateTime.now());
    }

    // ===== CREATE TASK TESTS =====

    @Test
    @DisplayName("Create task - Success")
    void testCreateTask_Success() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.createTask(
                1L,
                "Resume Review Session",
                "Professional resume review with detailed feedback and suggestions",
                45,
                "Resume Review"
        );

        // Then
        assertNotNull(result);
        assertEquals("Resume Review Session", result.getTitle());
        assertEquals(45, result.getDurationMinutes());
        verify(mentorRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Create task - Mentor not found")
    void testCreateTask_MentorNotFound() {
        // Given
        when(mentorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(999L, "Title", "Description", 45, "Category")
        );
        assertTrue(exception.getMessage().contains("Mentor not found"));
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create task - Invalid title (too short)")
    void testCreateTask_InvalidTitleTooShort() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(1L, "Hi", "Valid description here", 45, "Resume Review")
        );
        assertEquals("Task title must be at least 5 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("Create task - Invalid description (too short)")
    void testCreateTask_InvalidDescriptionTooShort() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(1L, "Valid Title", "Short", 45, "Resume Review")
        );
        assertEquals("Task description must be at least 20 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("Create task - Invalid duration (too short)")
    void testCreateTask_InvalidDurationTooShort() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(1L, "Valid Title", "Valid description with enough characters", 10, "Resume Review")
        );
        assertEquals("Task duration must be at least 15 minutes", exception.getMessage());
    }

    @Test
    @DisplayName("Create task - Invalid duration (not 15-minute increment)")
    void testCreateTask_InvalidDurationIncrement() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(1L, "Valid Title", "Valid description with enough characters", 47, "Resume Review")
        );
        assertEquals("Task duration must be in 15-minute increments", exception.getMessage());
    }

    @Test
    @DisplayName("Create task - Invalid category")
    void testCreateTask_InvalidCategory() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(1L, "Valid Title", "Valid description with enough characters", 45, "Invalid Category")
        );
        assertTrue(exception.getMessage().contains("Invalid category"));
    }

    // ===== UPDATE TASK TESTS =====

    @Test
    @DisplayName("Update task - Success")
    void testUpdateTask_Success() {
        // Given
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task updates = new Task();
        updates.setTitle("Updated Resume Review");
        updates.setDurationMinutes(60);

        // When
        Task result = taskService.updateTask(100L, updates);

        // Then
        assertNotNull(result);
        verify(taskRepository).findById(100L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Update task - Not found")
    void testUpdateTask_NotFound() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        Task updates = new Task();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.updateTask(999L, updates)
        );
        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    @DisplayName("Update task with mentor permission - Success")
    void testUpdateTaskWithMentorId_Success() {
        // Given
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.updateTask(100L, 1L, "Updated Title", null, null, null);

        // Then
        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Update task - Wrong mentor")
    void testUpdateTask_WrongMentor() {
        // Given
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> taskService.updateTask(100L, 999L, "Updated Title", null, null, null)
        );
        assertEquals("Mentor does not have permission to update this task", exception.getMessage());
    }

    // ===== DELETE TASK TESTS =====

    @Test
    @DisplayName("Delete task - Success")
    void testDeleteTask_Success() {
        // Given
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(bookingRepository.findByTask(testTask)).thenReturn(Arrays.asList());
        doNothing().when(taskRepository).deleteById(100L);

        // When
        taskService.deleteTask(100L, 1L);

        // Then
        verify(taskRepository).findById(100L);
        verify(bookingRepository).findByTask(testTask);
        verify(taskRepository).deleteById(100L);
    }

    @Test
    @DisplayName("Delete task - Not found")
    void testDeleteTask_NotFound() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.deleteTask(999L, 1L)
        );
        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    @DisplayName("Delete task - Wrong mentor")
    void testDeleteTask_WrongMentor() {
        // Given
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> taskService.deleteTask(100L, 999L)
        );
        assertEquals("Mentor does not have permission to delete this task", exception.getMessage());
    }

    @Test
    @DisplayName("Delete task - Has confirmed bookings")
    void testDeleteTask_HasConfirmedBookings() {
        // Given
        Booking booking = new Booking();
        booking.setStatus("accepted");
        booking.setProposedDatetime(LocalDateTime.now().plusDays(1));

        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(bookingRepository.findByTask(testTask)).thenReturn(Arrays.asList(booking));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> taskService.deleteTask(100L, 1L)
        );
        assertTrue(exception.getMessage().contains("Cannot delete task with"));
        verify(taskRepository, never()).deleteById(anyLong());
    }

    // ===== GET TASK TESTS =====

    @Test
    @DisplayName("Get task by ID - Success")
    void testGetTaskById_Success() {
        // Given
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));

        // When
        Task result = taskService.getTaskById(100L);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getTaskId());
        assertEquals("Resume Review Session", result.getTitle());
    }

    @Test
    @DisplayName("Get task by ID - Not found")
    void testGetTaskById_NotFound() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.getTaskById(999L)
        );
        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    @DisplayName("Get all tasks - Success")
    void testGetAllTasks_Success() {
        // Given
        Task task2 = new Task();
        task2.setTaskId(200L);
        task2.setTitle("Interview Prep");

        when(taskRepository.findAll()).thenReturn(Arrays.asList(testTask, task2));

        // When
        List<Task> results = taskService.getAllTasks();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Get tasks by mentor - Success")
    void testGetTasksByMentor_Success() {
        // Given
        when(mentorRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByMentorId(1L)).thenReturn(Arrays.asList(testTask));

        // When
        List<Task> results = taskService.getTasksByMentor(1L);

        // Then
        assertEquals(1, results.size());
        assertEquals("Resume Review Session", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Get tasks by mentor - Mentor not found")
    void testGetTasksByMentor_NotFound() {
        // Given
        when(mentorRepository.existsById(999L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.getTasksByMentor(999L)
        );
        assertTrue(exception.getMessage().contains("Mentor not found"));
    }

    @Test
    @DisplayName("Get tasks by category - Success")
    void testGetTasksByCategory_Success() {
        // Given
        when(taskRepository.findByCategory("Resume Review"))
                .thenReturn(Arrays.asList(testTask));

        // When
        List<Task> results = taskService.getTasksByCategory("Resume Review");

        // Then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Search tasks by title - Success")
    void testSearchTasksByTitle_Success() {
        // Given
        when(taskRepository.findByTitleContainingIgnoreCase("Resume"))
                .thenReturn(Arrays.asList(testTask));

        // When
        List<Task> results = taskService.searchTasksByTitle("Resume");

        // Then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Search tasks by title - Empty string throws exception")
    void testSearchTasksByTitle_EmptyString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.searchTasksByTitle("")
        );
        assertEquals("Search title cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Get tasks by max duration - Success")
    void testGetTasksByMaxDuration_Success() {
        // Given
        when(taskRepository.findByDurationMinutesLessThanEqual(60))
                .thenReturn(Arrays.asList(testTask));

        // When
        List<Task> results = taskService.getTasksByMaxDuration(60);

        // Then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Get tasks by min duration - Success")
    void testGetTasksByMinDuration_Success() {
        // Given
        when(taskRepository.findByDurationMinutesGreaterThanEqual(30))
                .thenReturn(Arrays.asList(testTask));

        // When
        List<Task> results = taskService.getTasksByMinDuration(30);

        // Then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Count tasks by mentor - Success")
    void testCountTasksByMentor_Success() {
        // Given
        when(mentorRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.countByMentorId(1L)).thenReturn(5L);

        // When
        long count = taskService.countTasksByMentor(1L);

        // Then
        assertEquals(5L, count);
    }

    @Test
    @DisplayName("Does task exist - Returns true")
    void testDoesTaskExist_True() {
        // Given
        when(taskRepository.existsById(100L)).thenReturn(true);

        // When
        boolean exists = taskService.doesTaskExist(100L);

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Does task exist - Returns false")
    void testDoesTaskExist_False() {
        // Given
        when(taskRepository.existsById(999L)).thenReturn(false);

        // When
        boolean exists = taskService.doesTaskExist(999L);

        // Then
        assertFalse(exists);
    }

    // ===== IMAGE HANDLING TESTS =====

    @Test
    @DisplayName("Set task image - Success")
    void testSetTaskImage_Success() {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(fileStorageService.storeFile(mockFile)).thenReturn("image123.jpg");
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        taskService.setTaskImage(100L, mockFile);

        // Then
        verify(fileStorageService).storeFile(mockFile);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Delete task image - Success")
    void testDeleteTaskImage_Success() {
        // Given
        testTask.setImageFileName("old-image.jpg");
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        doNothing().when(fileStorageService).deleteFile("old-image.jpg");
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        taskService.deleteTaskImage(100L);

        // Then
        verify(fileStorageService).deleteFile("old-image.jpg");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Delete task image - No image to delete")
    void testDeleteTaskImage_NoImage() {
        // Given
        testTask.setImageFileName(null);
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));

        // When
        taskService.deleteTaskImage(100L);

        // Then
        verify(fileStorageService, never()).deleteFile(any());
        verify(taskRepository, never()).save(any());
    }

    // ===== VALIDATION TESTS =====

    @Test
    @DisplayName("Valid categories - All valid categories accepted")
    void testValidCategories() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        String[] validCategories = {
                "Resume Review",
                "Interview Prep",
                "Career Advice",
                "Technical Mentoring",
                "Networking",
                "Project Review",
                "Programming",
                "Other"
        };

        // When & Then
        for (String category : validCategories) {
            assertDoesNotThrow(() ->
                    taskService.createTask(1L, "Valid Title", "Valid description with twenty chars", 30, category)
            );
        }
    }

    @Test
    @DisplayName("Valid durations - All 15-minute increments accepted")
    void testValidDurations() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        int[] validDurations = {15, 30, 45, 60, 75, 90, 105, 120, 135, 150, 165, 180};

        // When & Then
        for (int duration : validDurations) {
            assertDoesNotThrow(() ->
                    taskService.createTask(1L, "Valid Title", "Valid description with twenty chars", duration, "Resume Review")
            );
        }
    }
}
