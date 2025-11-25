package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.entity.User;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SearchService
 * Tests all search functionality with mocked dependencies
 *
 * @author Claude
 */
@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private SearchService searchService;

    private Mentor mentor1;
    private Mentor mentor2;
    private Student student1;
    private Student student2;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        // Setup mentor 1
        User user1 = new User();
        user1.setUserId(1L);
        user1.setAccountType("mentor");

        mentor1 = new Mentor();
        mentor1.setMentorId(1L);
        mentor1.setUser(user1);
        mentor1.setName("John Smith");
        mentor1.setCompany("Tech Corp");
        mentor1.setIndustries("Technology, Finance");
        mentor1.setExpertiseAreas("Java, Spring Boot");
        mentor1.setYearsExperience(10);
        mentor1.setCreatedAt(LocalDateTime.now());

        // Setup mentor 2
        User user2 = new User();
        user2.setUserId(2L);
        user2.setAccountType("mentor");

        mentor2 = new Mentor();
        mentor2.setMentorId(2L);
        mentor2.setUser(user2);
        mentor2.setName("Jane Doe");
        mentor2.setCompany("Startup Inc");
        mentor2.setIndustries("Healthcare, AI");
        mentor2.setExpertiseAreas("Python, Machine Learning");
        mentor2.setYearsExperience(5);
        mentor2.setCreatedAt(LocalDateTime.now());

        // Setup student 1
        User studentUser1 = new User();
        studentUser1.setUserId(3L);
        studentUser1.setAccountType("student");

        student1 = new Student();
        student1.setStudentId(1L);
        student1.setUser(studentUser1);
        student1.setName("Alice Student");
        student1.setMajor("Computer Science");
        student1.setGraduationYear(2025);
        student1.setCareerInterests("Software Engineering, AI");
        student1.setCreatedAt(LocalDateTime.now());

        // Setup student 2
        User studentUser2 = new User();
        studentUser2.setUserId(4L);
        studentUser2.setAccountType("student");

        student2 = new Student();
        student2.setStudentId(2L);
        student2.setUser(studentUser2);
        student2.setName("Bob Johnson");
        student2.setMajor("Information Systems");
        student2.setGraduationYear(2026);
        student2.setCareerInterests("Data Science, Cloud Computing");
        student2.setCreatedAt(LocalDateTime.now());

        // Setup task 1
        task1 = new Task();
        task1.setTaskId(1L);
        task1.setMentor(mentor1);
        task1.setTitle("Resume Review Session");
        task1.setDescription("Comprehensive resume review");
        task1.setDurationMinutes(45);
        task1.setCategory("Resume Review");
        task1.setCreatedAt(LocalDateTime.now());

        // Setup task 2
        task2 = new Task();
        task2.setTaskId(2L);
        task2.setMentor(mentor2);
        task2.setTitle("Mock Interview Practice");
        task2.setDescription("Technical interview preparation");
        task2.setDurationMinutes(60);
        task2.setCategory("Interview Prep");
        task2.setCreatedAt(LocalDateTime.now());
    }

    // ===== SEARCH MENTORS TESTS =====

    @Test
    @DisplayName("Search mentors - No filters returns all")
    void testSearchMentors_NoFilters() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentors(null, null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mentorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Search mentors - Filter by name")
    void testSearchMentors_ByName() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentors("John", null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors - Filter by name case insensitive")
    void testSearchMentors_ByNameCaseInsensitive() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentors("john", null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors - Filter by company")
    void testSearchMentors_ByCompany() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentors(null, "Tech", null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tech Corp", result.get(0).getCompany());
    }

    @Test
    @DisplayName("Search mentors - Filter by industry")
    void testSearchMentors_ByIndustry() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentors(null, null, "Healthcare", null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jane Doe", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors - Filter by expertise")
    void testSearchMentors_ByExpertise() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentors(null, null, null, "Java", null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors - Filter by minimum years experience")
    void testSearchMentors_ByMinYearsExperience() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentors(null, null, null, null, 8);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors - Multiple filters")
    void testSearchMentors_MultipleFilters() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentors(
                "John", "Tech", "Technology", null, null
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors - No results")
    void testSearchMentors_NoResults() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentors("Nonexistent", null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Search mentors - Filters out null values")
    void testSearchMentors_FiltersNullValues() {
        // Given
        mentor2.setCompany(null);
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentors(null, "Tech", null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }

    // ===== SEARCH TASKS TESTS =====

    @Test
    @DisplayName("Search tasks - No filters returns all")
    void testSearchTasks_NoFilters() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        List<Task> result = searchService.searchTasks(null, null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Search tasks - Filter by mentor ID")
    void testSearchTasks_ByMentorId() {
        // Given
        when(taskRepository.findByMentorId(1L)).thenReturn(Arrays.asList(task1));

        // When
        List<Task> result = searchService.searchTasks(null, null, 1L, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Resume Review Session", result.get(0).getTitle());
        verify(taskRepository, times(1)).findByMentorId(1L);
        verify(taskRepository, never()).findAll();
    }

    @Test
    @DisplayName("Search tasks - Filter by title")
    void testSearchTasks_ByTitle() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        List<Task> result = searchService.searchTasks("Resume", null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Resume Review Session", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Search tasks - Filter by category")
    void testSearchTasks_ByCategory() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        List<Task> result = searchService.searchTasks(null, "Interview", null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Interview Prep", result.get(0).getCategory());
    }

    @Test
    @DisplayName("Search tasks - Filter by minimum duration")
    void testSearchTasks_ByMinDuration() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        List<Task> result = searchService.searchTasks(null, null, null, 50, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(60, result.get(0).getDurationMinutes());
    }

    @Test
    @DisplayName("Search tasks - Filter by maximum duration")
    void testSearchTasks_ByMaxDuration() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        List<Task> result = searchService.searchTasks(null, null, null, null, 50);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(45, result.get(0).getDurationMinutes());
    }

    @Test
    @DisplayName("Search tasks - Filter by duration range")
    void testSearchTasks_ByDurationRange() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        List<Task> result = searchService.searchTasks(null, null, null, 40, 50);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(45, result.get(0).getDurationMinutes());
    }

    @Test
    @DisplayName("Search tasks - Multiple filters")
    void testSearchTasks_MultipleFilters() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        List<Task> result = searchService.searchTasks(
                "Resume", "Review", null, null, 50
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Resume Review Session", result.get(0).getTitle());
    }

    // ===== SEARCH STUDENTS TESTS =====

    @Test
    @DisplayName("Search students - No filters returns all")
    void testSearchStudents_NoFilters() {
        // Given
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // When
        List<Student> result = searchService.searchStudents(null, null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Search students - Filter by name")
    void testSearchStudents_ByName() {
        // Given
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // When
        List<Student> result = searchService.searchStudents("Alice", null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Alice Student", result.get(0).getName());
    }

    @Test
    @DisplayName("Search students - Filter by major")
    void testSearchStudents_ByMajor() {
        // Given
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // When
        List<Student> result = searchService.searchStudents(null, "Computer", null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Computer Science", result.get(0).getMajor());
    }

    @Test
    @DisplayName("Search students - Filter by graduation year")
    void testSearchStudents_ByGraduationYear() {
        // Given
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // When
        List<Student> result = searchService.searchStudents(null, null, 2025, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2025, result.get(0).getGraduationYear());
    }

    @Test
    @DisplayName("Search students - Filter by minimum graduation year")
    void testSearchStudents_ByMinGraduationYear() {
        // Given
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // When
        List<Student> result = searchService.searchStudents(null, null, null, 2026, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2026, result.get(0).getGraduationYear());
    }

    @Test
    @DisplayName("Search students - Filter by career interests")
    void testSearchStudents_ByCareerInterests() {
        // Given
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // When
        List<Student> result = searchService.searchStudents(null, null, null, null, "AI");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Alice Student", result.get(0).getName());
    }

    @Test
    @DisplayName("Search students - Multiple filters")
    void testSearchStudents_MultipleFilters() {
        // Given
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // When
        List<Student> result = searchService.searchStudents(
                "Alice", "Computer", 2025, null, "AI"
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Alice Student", result.get(0).getName());
    }

    // ===== GET ALL CATEGORIES TESTS =====

    @Test
    @DisplayName("Get all categories - Success")
    void testGetAllCategories_Success() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        List<String> result = searchService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Resume Review"));
        assertTrue(result.contains("Interview Prep"));
    }

    @Test
    @DisplayName("Get all categories - Removes duplicates")
    void testGetAllCategories_RemovesDuplicates() {
        // Given
        Task task3 = new Task();
        task3.setCategory("Resume Review");
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2, task3));

        // When
        List<String> result = searchService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Get all categories - Filters null and empty")
    void testGetAllCategories_FiltersNullAndEmpty() {
        // Given
        Task task3 = new Task();
        task3.setCategory(null);
        Task task4 = new Task();
        task4.setCategory("");
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2, task3, task4));

        // When
        List<String> result = searchService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Get all categories - Returns sorted list")
    void testGetAllCategories_Sorted() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task2, task1));

        // When
        List<String> result = searchService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals("Interview Prep", result.get(0));
        assertEquals("Resume Review", result.get(1));
    }

    // ===== GET ALL COMPANIES TESTS =====

    @Test
    @DisplayName("Get all companies - Success")
    void testGetAllCompanies_Success() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<String> result = searchService.getAllCompanies();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Tech Corp"));
        assertTrue(result.contains("Startup Inc"));
    }

    @Test
    @DisplayName("Get all companies - Removes duplicates")
    void testGetAllCompanies_RemovesDuplicates() {
        // Given
        Mentor mentor3 = new Mentor();
        mentor3.setCompany("Tech Corp");
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2, mentor3));

        // When
        List<String> result = searchService.getAllCompanies();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Get all companies - Filters null values")
    void testGetAllCompanies_FiltersNull() {
        // Given
        Mentor mentor3 = new Mentor();
        mentor3.setCompany(null);
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2, mentor3));

        // When
        List<String> result = searchService.getAllCompanies();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ===== GET ALL MAJORS TESTS =====

    @Test
    @DisplayName("Get all majors - Success")
    void testGetAllMajors_Success() {
        // Given
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // When
        List<String> result = searchService.getAllMajors();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Computer Science"));
        assertTrue(result.contains("Information Systems"));
    }

    @Test
    @DisplayName("Get all majors - Removes duplicates")
    void testGetAllMajors_RemovesDuplicates() {
        // Given
        Student student3 = new Student();
        student3.setMajor("Computer Science");
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2, student3));

        // When
        List<String> result = searchService.getAllMajors();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ===== SEARCH MENTORS WITH TASKS TESTS =====

    @Test
    @DisplayName("Search mentors with tasks - No task filters")
    void testSearchMentorsWithTasks_NoTaskFilters() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentorsWithTasks(null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Search mentors with tasks - Filter by mentor name")
    void testSearchMentorsWithTasks_ByMentorName() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));

        // When
        List<Mentor> result = searchService.searchMentorsWithTasks("John", null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors with tasks - Filter by task category")
    void testSearchMentorsWithTasks_ByTaskCategory() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));
        when(taskRepository.findByMentorId(1L)).thenReturn(Arrays.asList(task1));
        when(taskRepository.findByMentorId(2L)).thenReturn(Arrays.asList(task2));

        // When
        List<Mentor> result = searchService.searchMentorsWithTasks(
                null, null, "Resume Review", null
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors with tasks - Filter by max duration")
    void testSearchMentorsWithTasks_ByMaxDuration() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));
        when(taskRepository.findByMentorId(1L)).thenReturn(Arrays.asList(task1));
        when(taskRepository.findByMentorId(2L)).thenReturn(Arrays.asList(task2));

        // When
        List<Mentor> result = searchService.searchMentorsWithTasks(null, null, null, 50);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors with tasks - Combined filters")
    void testSearchMentorsWithTasks_CombinedFilters() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));
        when(taskRepository.findByMentorId(1L)).thenReturn(Arrays.asList(task1));
        when(taskRepository.findByMentorId(2L)).thenReturn(Arrays.asList(task2));

        // When
        List<Mentor> result = searchService.searchMentorsWithTasks(
                "John", "Java", "Resume Review", 50
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors with tasks - No matching mentors")
    void testSearchMentorsWithTasks_NoMatches() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));
        when(taskRepository.findByMentorId(1L)).thenReturn(Arrays.asList(task1));
        when(taskRepository.findByMentorId(2L)).thenReturn(Arrays.asList(task2));

        // When
        List<Mentor> result = searchService.searchMentorsWithTasks(
                null, null, "Nonexistent Category", null
        );

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Search mentors with tasks - Mentor with no tasks excluded")
    void testSearchMentorsWithTasks_MentorWithNoTasks() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mentor1, mentor2));
        when(taskRepository.findByMentorId(1L)).thenReturn(Collections.emptyList());
        when(taskRepository.findByMentorId(2L)).thenReturn(Arrays.asList(task2));

        // When
        List<Mentor> result = searchService.searchMentorsWithTasks(
                null, null, "Interview Prep", null
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jane Doe", result.get(0).getName());
    }
}