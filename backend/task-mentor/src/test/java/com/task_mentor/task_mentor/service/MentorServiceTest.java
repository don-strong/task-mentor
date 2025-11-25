package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.dto.MentorSearchDTO;
import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.User;
import com.task_mentor.task_mentor.repository.MentorRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MentorService
 * Tests all mentor-related business logic with mocked dependencies
 *
 * @author Claude
 */
@ExtendWith(MockitoExtension.class)
class MentorServiceTest {

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorService mentorService;

    private User mockUser;
    private Mentor mockMentor;

    @BeforeEach
    void setUp() {
        // Setup mock user
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setEmail("mentor@test.com");
        mockUser.setAccountType("mentor");
        mockUser.setCreatedAt(LocalDateTime.now());

        // Setup mock mentor
        mockMentor = new Mentor();
        mockMentor.setMentorId(1L);
        mockMentor.setUser(mockUser);
        mockMentor.setName("John Mentor");
        mockMentor.setBio("Experienced software engineer");
        mockMentor.setRoleTitle("Senior Engineer");
        mockMentor.setCompany("Tech Corp");
        mockMentor.setYearsExperience(10);
        mockMentor.setIndustries("Technology, Finance");
        mockMentor.setExpertiseAreas("Java, Spring Boot");
        mockMentor.setProfilePhotoUrl("https://example.com/photo.jpg");
        mockMentor.setCreatedAt(LocalDateTime.now());
    }

    // ===== CREATE MENTOR TESTS =====

    @Test
    @DisplayName("Create mentor - Success with all fields")
    void testCreateMentor_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mockMentor);

        // When
        Mentor result = mentorService.createMentor(
                1L,
                "John Mentor",
                "Experienced software engineer",
                "Senior Engineer",
                "Tech Corp",
                10,
                "Technology, Finance",
                "Java, Spring Boot",
                "https://example.com/photo.jpg"
        );

        // Then
        assertNotNull(result);
        assertEquals("John Mentor", result.getName());
        assertEquals("Tech Corp", result.getCompany());
        assertEquals(10, result.getYearsExperience());
        verify(mentorRepository, times(1)).save(any(Mentor.class));
    }

    @Test
    @DisplayName("Create mentor - Success with minimal fields")
    void testCreateMentor_MinimalFields() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mockMentor);

        // When
        Mentor result = mentorService.createMentor(
                1L,
                "John Mentor",
                null,
                null,
                null,
                5,
                null,
                null,
                null
        );

        // Then
        assertNotNull(result);
        assertEquals("John Mentor", result.getName());
        verify(mentorRepository, times(1)).save(any(Mentor.class));
    }

    @Test
    @DisplayName("Create mentor - Success with default profile photo")
    void testCreateMentor_DefaultProfilePhoto() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);

        Mentor savedMentor = new Mentor();
        savedMentor.setProfilePhotoUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=default");
        when(mentorRepository.save(any(Mentor.class))).thenReturn(savedMentor);

        // When
        Mentor result = mentorService.createMentor(
                1L, "John Mentor", null, null, null, 5, null, null, null
        );

        // Then
        assertNotNull(result);
        assertEquals("https://api.dicebear.com/7.x/avataaars/svg?seed=default",
                result.getProfilePhotoUrl());
    }

    @Test
    @DisplayName("Create mentor - Fails when user not found")
    void testCreateMentor_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, "John", null, null, null, 5, null, null, null
                )
        );

        assertEquals("User not found", exception.getMessage());
        verify(mentorRepository, never()).save(any(Mentor.class));
    }

    @Test
    @DisplayName("Create mentor - Fails when user is not mentor type")
    void testCreateMentor_UserNotMentorType() {
        // Given
        mockUser.setAccountType("student");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, "John", null, null, null, 5, null, null, null
                )
        );

        assertEquals("User is not a mentor", exception.getMessage());
        verify(mentorRepository, never()).save(any(Mentor.class));
    }

    @Test
    @DisplayName("Create mentor - Fails when mentor already exists")
    void testCreateMentor_MentorAlreadyExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, "John", null, null, null, 5, null, null, null
                )
        );

        assertEquals("User is already a mentor", exception.getMessage());
        verify(mentorRepository, never()).save(any(Mentor.class));
    }

    @Test
    @DisplayName("Create mentor - Fails with empty name")
    void testCreateMentor_EmptyName() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, "", null, null, null, 5, null, null, null
                )
        );

        assertEquals("Mentor name cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Create mentor - Fails with short name")
    void testCreateMentor_NameTooShort() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, "J", null, null, null, 5, null, null, null
                )
        );

        assertEquals("Mentor name cannot be less than 2 characters",
                exception.getMessage());
    }

    @Test
    @DisplayName("Create mentor - Fails with negative years experience")
    void testCreateMentor_NegativeYears() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, "John Mentor", null, null, null, -1, null, null, null
                )
        );

        assertEquals("Years experience cannot be less than 0",
                exception.getMessage());
    }

    @Test
    @DisplayName("Create mentor - Fails with excessive years experience")
    void testCreateMentor_ExcessiveYears() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, "John Mentor", null, null, null, 61, null, null, null
                )
        );

        assertEquals("Years experience seems unrealistic(no more than 60 :))",
                exception.getMessage());
    }

    @Test
    @DisplayName("Create mentor - Fails with invalid profile photo URL")
    void testCreateMentor_InvalidPhotoUrl() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, "John Mentor", null, null, null, 5, null, null,
                        "invalid-url"
                )
        );

        assertEquals("Profile photo url must be a valid HTTTP or HTTPS URL",
                exception.getMessage());
    }

    // ===== UPDATE MENTOR TESTS =====

    @Test
    @DisplayName("Update mentor - Success with all fields")
    void testUpdateMentor_Success() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mockMentor));
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mockMentor);

        // When
        Mentor result = mentorService.updateMentorProfile(
                1L,
                "Updated Name",
                "Updated bio",
                "Principal Engineer",
                "New Company",
                15,
                "Tech, Healthcare",
                "Java, Python, Cloud",
                "https://example.com/new.jpg"
        );

        // Then
        assertNotNull(result);
        verify(mentorRepository, times(1)).save(any(Mentor.class));
    }

    @Test
    @DisplayName("Update mentor - Success with partial update")
    void testUpdateMentor_PartialUpdate() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mockMentor));
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mockMentor);

        // When
        Mentor result = mentorService.updateMentorProfile(
                1L,
                "Updated Name",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Then
        assertNotNull(result);
        verify(mentorRepository, times(1)).save(any(Mentor.class));
    }

    @Test
    @DisplayName("Update mentor - Fails when mentor not found")
    void testUpdateMentor_NotFound() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.updateMentorProfile(
                        1L, "Updated", null, null, null, null, null, null, null
                )
        );

        assertEquals("Mentor not found", exception.getMessage());
        verify(mentorRepository, never()).save(any(Mentor.class));
    }

    @Test
    @DisplayName("Update mentor - Clears bio with empty string")
    void testUpdateMentor_ClearBio() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mockMentor));
        when(mentorRepository.save(any(Mentor.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Mentor result = mentorService.updateMentorProfile(
                1L, null, "", null, null, null, null, null, null
        );

        // Then
        assertNull(result.getBio());
        verify(mentorRepository, times(1)).save(any(Mentor.class));
    }

    // ===== GET MENTOR TESTS =====

    @Test
    @DisplayName("Get mentor by ID - Success")
    void testGetMentorById_Success() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mockMentor));

        // When
        Mentor result = mentorService.getMentorById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getMentorId());
        assertEquals("John Mentor", result.getName());
        verify(mentorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get mentor by ID - Not found")
    void testGetMentorById_NotFound() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.getMentorById(1L)
        );

        assertEquals("Mentor with that id  not found", exception.getMessage());
    }

    @Test
    @DisplayName("Get all mentors - Success")
    void testGetAllMentors_Success() {
        // Given
        Mentor mentor2 = new Mentor();
        mentor2.setMentorId(2L);
        mentor2.setName("Jane Mentor");

        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mockMentor, mentor2));

        // When
        List<Mentor> result = mentorService.getAllMentors();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mentorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get all mentors - Empty list")
    void testGetAllMentors_Empty() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Mentor> result = mentorService.getAllMentors();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ===== DELETE MENTOR TESTS =====

    @Test
    @DisplayName("Delete mentor - Success")
    void testDeleteMentor_Success() {
        // Given
        when(mentorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(mentorRepository).deleteById(1L);

        // When
        mentorService.deleteMentorProfile(1L);

        // Then
        verify(mentorRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Delete mentor - Not found")
    void testDeleteMentor_NotFound() {
        // Given
        when(mentorRepository.existsById(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.deleteMentorProfile(1L)
        );

        assertEquals("Mentor with that id not found", exception.getMessage());
        verify(mentorRepository, never()).deleteById(anyLong());
    }

    // ===== EXISTS TESTS =====

    @Test
    @DisplayName("Check mentor exists - True")
    void testDoesMentorExist_True() {
        // Given
        when(mentorRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = mentorService.doesMentorExist(1L);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Check mentor exists - False")
    void testDoesMentorExist_False() {
        // Given
        when(mentorRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = mentorService.doesMentorExist(1L);

        // Then
        assertFalse(result);
    }

    // ===== SEARCH TESTS =====

    @Test
    @DisplayName("Search mentors by name - Success")
    void testSearchMentorsByName_Success() {
        // Given
        when(mentorRepository.findByNameContainingIgnoreCase("John"))
                .thenReturn(Arrays.asList(mockMentor));

        // When
        List<Mentor> result = mentorService.SearchMentorsByName("John");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Mentor", result.get(0).getName());
    }

    @Test
    @DisplayName("Search mentors by name - Empty query fails")
    void testSearchMentorsByName_EmptyQuery() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.SearchMentorsByName("")
        );

        assertEquals("Mentor name cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Search mentors by company - Success")
    void testSearchMentorsByCompany_Success() {
        // Given
        when(mentorRepository.findByNameContainingIgnoreCase("Tech"))
                .thenReturn(Arrays.asList(mockMentor));

        // When
        List<Mentor> result = mentorService.SearchMentorsByCompany("Tech");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Search mentors by company - Empty query fails")
    void testSearchMentorsByCompany_EmptyQuery() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.SearchMentorsByCompany("")
        );

        assertEquals("Company name cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Search mentors by expertise - Success")
    void testSearchMentorsByExpertise_Success() {
        // Given
        when(mentorRepository.findByExpertise("Java"))
                .thenReturn(Arrays.asList(mockMentor));

        // When
        List<Mentor> result = mentorService.SearchMentorsByExpertise("Java");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Search mentors by expertise - Empty query fails")
    void testSearchMentorsByExpertise_EmptyQuery() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.SearchMentorsByExpertise("")
        );

        assertEquals("Expertise areas cannot be empty", exception.getMessage());
    }

    // ===== STATISTICS TESTS =====

    @Test
    @DisplayName("Get mentor statistics - Success")
    void testGetMentorStatistics_Success() {
        // Given
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mockMentor));

        // When
        MentorSearchDTO result = mentorService.getMentorStatistics(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getMentorId());
        assertEquals("John Mentor", result.getName());
        assertEquals("Tech Corp", result.getCompany());
        assertEquals(10, result.getYearsExperience());
    }

    @Test
    @DisplayName("Get all mentor statistics - Success")
    void testGetAllMentorStatistics_Success() {
        // Given
        Mentor mentor2 = new Mentor();
        mentor2.setMentorId(2L);
        mentor2.setName("Jane Mentor");
        mentor2.setYearsExperience(5);

        when(mentorRepository.findAll()).thenReturn(Arrays.asList(mockMentor, mentor2));

        // When
        List<MentorSearchDTO> result = mentorService.getAllMentorStatistics();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Mentor", result.get(0).getName());
        assertEquals("Jane Mentor", result.get(1).getName());
    }

    @Test
    @DisplayName("Get all mentor statistics - Empty list")
    void testGetAllMentorStatistics_Empty() {
        // Given
        when(mentorRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<MentorSearchDTO> result = mentorService.getAllMentorStatistics();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ===== VALIDATION TESTS =====

    @Test
    @DisplayName("Validation - Name too long fails")
    void testValidation_NameTooLong() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);

        String longName = "A".repeat(151);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, longName, null, null, null, 5, null, null, null
                )
        );

        assertEquals("Mentor name cannot be greater than 150 characters",
                exception.getMessage());
    }

    @Test
    @DisplayName("Validation - Null years experience fails")
    void testValidation_NullYears() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, "John", null, null, null, null, null, null, null
                )
        );

        assertEquals("Years experience cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Validation - Profile photo URL too long fails")
    void testValidation_PhotoUrlTooLong() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);

        String longUrl = "https://" + "a".repeat(494);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mentorService.createMentor(
                        1L, "John", null, null, null, 5, null, null, longUrl
                )
        );

        assertEquals("Profile photo url cannot be greater than 500 characters",
                exception.getMessage());
    }
}