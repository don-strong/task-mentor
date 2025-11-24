package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.dto.StudentStatistics;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.User;
import com.task_mentor.task_mentor.repository.StudentRepository;
import com.task_mentor.task_mentor.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService
 * Tests business logic in isolation using mocked dependencies
 *
 * @author Claude
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StudentService studentService;

    private User testUser;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("student@test.com");
        testUser.setPassword("hashedPassword");
        testUser.setAccountType("student");
        testUser.setCreatedAt(LocalDateTime.now());

        // Create test student
        testStudent = new Student();
        testStudent.setStudentId(10L);
        testStudent.setUser(testUser);
        testStudent.setName("John Doe");
        testStudent.setBio("Computer Science student");
        testStudent.setMajor("Computer Science");
        testStudent.setGraduationYear(2025);
        testStudent.setCareerInterests("Software Engineering");
        testStudent.setProfilePhotoUrl("https://example.com/photo.jpg");
        testStudent.setCreatedAt(LocalDateTime.now());
    }

    // ===== CREATE STUDENT TESTS =====

    @Test
    @DisplayName("Create student - Success")
    void testCreateStudent_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentRepository.existsByUserId(1L)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // When
        Student result = studentService.createStudent(
                1L,
                "John Doe",
                "Computer Science student",
                "Computer Science",
                2025,
                "Software Engineering",
                "https://example.com/photo.jpg"
        );

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("Computer Science", result.getMajor());
        verify(userRepository).findById(1L);
        verify(studentRepository).existsByUserId(1L);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Create student - User not found")
    void testCreateStudent_UserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.createStudent(999L, "John", "Bio", "CS", 2025, "SE", null)
        );
        assertEquals("User not found", exception.getMessage());
        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create student - User not a student")
    void testCreateStudent_UserNotStudent() {
        // Given
        testUser.setAccountType("mentor");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.createStudent(1L, "John", "Bio", "CS", 2025, "SE", null)
        );
        assertEquals("User not a Student", exception.getMessage());
    }

    @Test
    @DisplayName("Create student - Already exists")
    void testCreateStudent_AlreadyExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentRepository.existsByUserId(1L)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.createStudent(1L, "John", "Bio", "CS", 2025, "SE", null)
        );
        assertEquals("User is already a Student", exception.getMessage());
    }

    @Test
    @DisplayName("Create student - Invalid name (too short)")
    void testCreateStudent_InvalidName() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentRepository.existsByUserId(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.createStudent(1L, "J", "Bio", "CS", 2025, "SE", null)
        );
        assertEquals("Student name must be at least 2 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("Create student - Invalid graduation year (too far past)")
    void testCreateStudent_InvalidGraduationYearPast() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentRepository.existsByUserId(1L)).thenReturn(false);

        // When & Then
        int currentYear = LocalDateTime.now().getYear();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.createStudent(1L, "John", "Bio", "CS", currentYear - 15, "SE", null)
        );
        assertTrue(exception.getMessage().contains("Graduation year cannot be more than 10 years in the past"));
    }

    @Test
    @DisplayName("Create student - Default profile photo when null")
    void testCreateStudent_DefaultProfilePhoto() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentRepository.existsByUserId(1L)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student student = invocation.getArgument(0);
            student.setStudentId(10L);
            return student;
        });

        // When
        Student result = studentService.createStudent(1L, "John", "Bio", "CS", 2025, "SE", null);

        // Then
        assertNotNull(result.getProfilePhotoUrl());
        assertTrue(result.getProfilePhotoUrl().contains("dicebear"));
    }

    // ===== UPDATE STUDENT TESTS =====

    @Test
    @DisplayName("Update student - Success")
    void testUpdateStudent_Success() {
        // Given
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // When
        Student result = studentService.updateStudentProfile(
                1L,
                "Jane Doe",
                "Updated bio",
                "Data Science",
                2026,
                "Data Engineering",
                "https://example.com/new-photo.jpg"
        );

        // Then
        assertNotNull(result);
        verify(studentRepository).findByUserId(1L);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Update student - Not found")
    void testUpdateStudent_NotFound() {
        // Given
        when(studentRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.updateStudentProfile(999L, "Jane", null, null, null, null, null)
        );
        assertEquals("Student not found", exception.getMessage());
    }

    @Test
    @DisplayName("Update student - Partial update (only name)")
    void testUpdateStudent_PartialUpdate() {
        // Given
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // When
        studentService.updateStudentProfile(1L, "Jane Doe", null, null, null, null, null);

        // Then
        verify(studentRepository).save(any(Student.class));
    }

    // ===== GET STUDENT TESTS =====

    @Test
    @DisplayName("Get student by ID - Success")
    void testGetStudentById_Success() {
        // Given
        when(studentRepository.findById(10L)).thenReturn(Optional.of(testStudent));

        // When
        Student result = studentService.getStudentById(10L);

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getStudentId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    @DisplayName("Get student by ID - Not found")
    void testGetStudentById_NotFound() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.getStudentById(999L)
        );
        assertEquals("Student with that id not found", exception.getMessage());
    }

    @Test
    @DisplayName("Get student by user ID - Success")
    void testGetStudentByUserId_Success() {
        // Given
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(testStudent));

        // When
        Student result = studentService.getStudentByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    @DisplayName("Get all students - Success")
    void testGetAllStudents_Success() {
        // Given
        Student student2 = new Student();
        student2.setStudentId(20L);
        student2.setName("Jane Smith");

        when(studentRepository.findAll()).thenReturn(Arrays.asList(testStudent, student2));

        // When
        List<Student> results = studentService.getAllStudents();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("John Doe", results.get(0).getName());
        assertEquals("Jane Smith", results.get(1).getName());
    }

    // ===== DELETE STUDENT TESTS =====

    @Test
    @DisplayName("Delete student - Success")
    void testDeleteStudent_Success() {
        // Given
        when(studentRepository.existsById(10L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(10L);

        // When
        studentService.deleteStudentProfile(10L);

        // Then
        verify(studentRepository).existsById(10L);
        verify(studentRepository).deleteById(10L);
    }

    @Test
    @DisplayName("Delete student - Not found")
    void testDeleteStudent_NotFound() {
        // Given
        when(studentRepository.existsById(999L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.deleteStudentProfile(999L)
        );
        assertEquals("Student with that id not found", exception.getMessage());
        verify(studentRepository, never()).deleteById(anyLong());
    }

    // ===== SEARCH TESTS =====

    @Test
    @DisplayName("Search by name - Success")
    void testSearchByName_Success() {
        // Given
        when(studentRepository.findByNameContainingIgnoreCase("John"))
                .thenReturn(Arrays.asList(testStudent));

        // When
        List<Student> results = studentService.searchStudentByName("John");

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("John Doe", results.get(0).getName());
    }

    @Test
    @DisplayName("Search by name - Empty string throws exception")
    void testSearchByName_EmptyString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.searchStudentByName("")
        );
        assertEquals("Student name cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Search by major - Success")
    void testSearchByMajor_Success() {
        // Given
        when(studentRepository.findByMajor("Computer Science"))
                .thenReturn(Arrays.asList(testStudent));

        // When
        List<Student> results = studentService.searchStudentByMajor("Computer Science");

        // Then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Search by graduation year - Success")
    void testSearchByGraduationYear_Success() {
        // Given
        when(studentRepository.findByGraduationYear(2025))
                .thenReturn(Arrays.asList(testStudent));

        // When
        List<Student> results = studentService.searchStudentByGraduationYear(2025);

        // Then
        assertEquals(1, results.size());
    }

    // ===== STATISTICS TESTS =====

    @Test
    @DisplayName("Get student statistics - Success")
    void testGetStudentStatistics_Success() {
        // Given
        when(studentRepository.findById(10L)).thenReturn(Optional.of(testStudent));

        // When
        StudentStatistics stats = studentService.getStudentStatistics(10L);

        // Then
        assertNotNull(stats);
        assertEquals(10L, stats.getStudentId());
        assertEquals("John Doe", stats.getName());
        assertEquals("Computer Science", stats.getMajor());
        assertEquals(2025, stats.getGraduationYear());
    }

    @Test
    @DisplayName("Get all student statistics - Success")
    void testGetAllStudentStatistics_Success() {
        // Given
        Student student2 = new Student();
        student2.setStudentId(20L);
        student2.setName("Jane Smith");
        student2.setMajor("Data Science");
        student2.setGraduationYear(2026);

        when(studentRepository.findAll()).thenReturn(Arrays.asList(testStudent, student2));

        // When
        List<StudentStatistics> results = studentService.getAllStudentStatistics();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("John Doe", results.get(0).getName());
        assertEquals("Jane Smith", results.get(1).getName());
    }

    @Test
    @DisplayName("Student profile exists - Returns true")
    void testStudentProfileExists_True() {
        // Given
        when(studentRepository.existsById(10L)).thenReturn(true);

        // When
        boolean exists = studentService.StudentProfileExists(10L);

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Student profile exists - Returns false")
    void testStudentProfileExists_False() {
        // Given
        when(studentRepository.existsById(999L)).thenReturn(false);

        // When
        boolean exists = studentService.StudentProfileExists(999L);

        // Then
        assertFalse(exists);
    }
}