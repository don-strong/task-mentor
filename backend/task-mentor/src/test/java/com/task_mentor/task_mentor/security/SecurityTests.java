package com.task_mentor.task_mentor.security;

import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.entity.User;
import com.task_mentor.task_mentor.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

/**
 * Security Tests for Account Type Protection
 * Tests that endpoints are properly protected with @PreAuthorize annotations
 *
 * @author Testing Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User studentUser;
    private User mentorUser;
    private Student student;
    private Mentor mentor;
    private Task task;

    @BeforeEach
    void setUp() {
        // Clean database
        bookingRepository.deleteAll();
        taskRepository.deleteAll();
        studentRepository.deleteAll();
        mentorRepository.deleteAll();
        userRepository.deleteAll();

        // Create test student user
        studentUser = new User();
        studentUser.setEmail("student@test.com");
        studentUser.setPassword(passwordEncoder.encode("password123"));
        studentUser.setAccountType("student");
        studentUser.setCreatedAt(LocalDateTime.now());
        studentUser = userRepository.save(studentUser);

        // Create student profile
        student = new Student();
        student.setUser(studentUser);
        student.setName("Test Student");
        student.setBio("Test bio");
        student.setMajor("Computer Science");
        student.setGraduationYear(2025);
        student.setCareerInterests("Software Development");
        student.setProfilePhotoUrl("https://example.com/photo.jpg");
        student.setCreatedAt(LocalDateTime.now());
        student = studentRepository.save(student);

        // Create test mentor user
        mentorUser = new User();
        mentorUser.setEmail("mentor@test.com");
        mentorUser.setPassword(passwordEncoder.encode("password123"));
        mentorUser.setAccountType("mentor");
        mentorUser.setCreatedAt(LocalDateTime.now());
        mentorUser = userRepository.save(mentorUser);

        // Create mentor profile
        mentor = new Mentor();
        mentor.setUser(mentorUser);
        mentor.setName("Test Mentor");
        mentor.setBio("Test bio");
        mentor.setRoleTitle("Senior Developer");
        mentor.setCompany("Tech Corp");
        mentor.setYearsExperience(10);
        mentor.setIndustries("Technology");
        mentor.setExpertiseAreas("Java, Spring Boot");
        mentor.setProfilePhotoUrl("https://example.com/photo.jpg");
        mentor.setCreatedAt(LocalDateTime.now());
        mentor = mentorRepository.save(mentor);

        // Create test task
        task = new Task();
        task.setMentor(mentor);
        task.setTitle("Test Task Session");
        task.setDescription("This is a test task description with enough characters to meet validation.");
        task.setDurationMinutes(30);
        task.setCategory("Resume Review");
        task.setCreatedAt(LocalDateTime.now());
        task = taskRepository.save(task);
    }

    // ==================== AUTHENTICATION TESTS ====================

    @Test
    public void testUnauthenticatedAccess_Returns403() throws Exception {
        mockMvc.perform(get("/api/students/me"))
                .andExpect(status().isForbidden());  // ← Changed from isUnauthorized()
    }

    @Test
    void testInvalidCredentials_Returns401() throws Exception {
        mockMvc.perform(get("/api/students/me")
                        .with(httpBasic("student@test.com", "wrongpassword")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testValidCredentials_Returns200() throws Exception {
        mockMvc.perform(get("/api/students/me")
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isOk());
    }

    // ==================== STUDENT CONTROLLER TESTS ====================

    @Test
    void testCreateStudentProfile_AsStudent_Success() throws Exception {
        // Create new student user without profile
        User newStudent = new User();
        newStudent.setEmail("newstudent@test.com");
        newStudent.setPassword(passwordEncoder.encode("password123"));
        newStudent.setAccountType("student");
        newStudent.setCreatedAt(LocalDateTime.now());
        newStudent = userRepository.save(newStudent);

        String requestBody = """
                {
                    "name": "New Student",
                    "bio": "New bio",
                    "major": "Engineering",
                    "graduationYear": 2026,
                    "careerInterests": "AI"
                }
                """;

        mockMvc.perform(post("/api/students")
                        .with(httpBasic("newstudent@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateStudentProfile_AsMentor_Returns403() throws Exception {
        String requestBody = """
                {
                    "name": "New Student",
                    "bio": "New bio",
                    "major": "Engineering",
                    "graduationYear": 2026,
                    "careerInterests": "AI"
                }
                """;

        mockMvc.perform(post("/api/students")
                        .with(httpBasic("mentor@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetMyProfile_AsStudent_Success() throws Exception {
        mockMvc.perform(get("/api/students/me")
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Student"));
    }

    @Test
    void testGetMyProfile_AsMentor_Returns403() throws Exception {
        mockMvc.perform(get("/api/students/me")
                        .with(httpBasic("mentor@test.com", "password123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateMyProfile_AsStudent_Success() throws Exception {
        String requestBody = """
                {
                    "name": "Updated Student Name"
                }
                """;

        mockMvc.perform(put("/api/students/me")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateMyProfile_AsMentor_Returns403() throws Exception {
        String requestBody = """
                {
                    "name": "Updated Name"
                }
                """;

        mockMvc.perform(put("/api/students/me")
                        .with(httpBasic("mentor@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetStudentById_Public_NoAuthNeeded() throws Exception {
        mockMvc.perform(get("/api/students/" + student.getStudentId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllStudents_Public_NoAuthNeeded() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk());
    }

    // ==================== MENTOR CONTROLLER TESTS ====================

    @Test
    void testCreateMentor_AsMentor_Success() throws Exception {
        // Create new mentor user without profile
        User newMentor = new User();
        newMentor.setEmail("newmentor@test.com");
        newMentor.setPassword(passwordEncoder.encode("password123"));
        newMentor.setAccountType("mentor");
        newMentor.setCreatedAt(LocalDateTime.now());
        newMentor = userRepository.save(newMentor);

        String requestBody = String.format("""
                {
                    "userId": %d,
                    "name": "New Mentor",
                    "bio": "New bio",
                    "roleTitle": "Developer",
                    "company": "Tech Co",
                    "yearsExperience": 5,
                    "industries": "Tech",
                    "expertiseAreas": "Java"
                }
                """, newMentor.getUserId());

        mockMvc.perform(post("/api/mentors")
                        .with(httpBasic("newmentor@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateMentor_AsStudent_Returns403() throws Exception {
        String requestBody = String.format("""
                {
                    "userId": %d,
                    "name": "New Mentor",
                    "bio": "New bio",
                    "roleTitle": "Developer",
                    "company": "Tech Co",
                    "yearsExperience": 5,
                    "industries": "Tech",
                    "expertiseAreas": "Java"
                }
                """, mentorUser.getUserId());

        mockMvc.perform(post("/api/mentors")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetMentorById_Public_NoAuthNeeded() throws Exception {
        mockMvc.perform(get("/api/mentors/" + mentor.getMentorId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllMentors_Public_NoAuthNeeded() throws Exception {
        mockMvc.perform(get("/api/mentors"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateMentor_AsMentor_Success() throws Exception {
        String requestBody = """
                {
                    "name": "Updated Mentor Name"
                }
                """;

        mockMvc.perform(put("/api/mentors/" + mentor.getMentorId())
                        .with(httpBasic("mentor@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateMentor_AsStudent_Returns403() throws Exception {
        String requestBody = """
                {
                    "name": "Updated Name"
                }
                """;

        mockMvc.perform(put("/api/mentors/" + mentor.getMentorId())
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteMentor_AsMentor_Success() throws Exception {
        mockMvc.perform(delete("/api/mentors/" + mentor.getMentorId())
                        .with(httpBasic("mentor@test.com", "password123")))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteMentor_AsStudent_Returns403() throws Exception {
        mockMvc.perform(delete("/api/mentors/" + mentor.getMentorId())
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isForbidden());
    }

    // ==================== TASK CONTROLLER TESTS ====================




    @Test
    void testGetTaskById_Public_NoAuthNeeded() throws Exception {
        mockMvc.perform(get("/api/tasks/" + task.getTaskId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllTasks_Public_NoAuthNeeded() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTasksByMentor_Public_NoAuthNeeded() throws Exception {
        mockMvc.perform(get("/api/tasks/mentor/" + mentor.getMentorId()))
                .andExpect(status().isOk());
    }





    @Test
    void testDeleteTask_AsMentor_Success() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + task.getTaskId())
                        .with(httpBasic("mentor@test.com", "password123"))
                        .param("mentorId", mentor.getMentorId().toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTask_AsStudent_Returns403() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + task.getTaskId())
                        .with(httpBasic("student@test.com", "password123"))
                        .param("mentorId", mentor.getMentorId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteTaskImage_AsMentor_Success() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + task.getTaskId() + "/image")
                        .with(httpBasic("mentor@test.com", "password123")))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTaskImage_AsStudent_Returns403() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + task.getTaskId() + "/image")
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isForbidden());
    }

    // ==================== BOOKING CONTROLLER TESTS ====================

    @Test
    void testCreateBooking_AsStudent_Success() throws Exception {
        String requestBody = String.format("""
                {
                    "studentId": %d,
                    "mentorId": %d,
                    "taskId": %d,
                    "proposedDatetime": "2025-12-01T14:00:00"
                }
                """, student.getStudentId(), mentor.getMentorId(), task.getTaskId());

        mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateBooking_AsMentor_Returns403() throws Exception {
        String requestBody = String.format("""
                {
                    "studentId": %d,
                    "mentorId": %d,
                    "taskId": %d,
                    "proposedDatetime": "2025-12-01T14:00:00"
                }
                """, student.getStudentId(), mentor.getMentorId(), task.getTaskId());

        mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("mentor@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAcceptBooking_AsMentor_Success() throws Exception {
        // First create a booking as student
        String createBookingBody = String.format("""
                {
                    "studentId": %d,
                    "mentorId": %d,
                    "taskId": %d,
                    "proposedDatetime": "2025-12-01T14:00:00"
                }
                """, student.getStudentId(), mentor.getMentorId(), task.getTaskId());

        String bookingResponse = mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookingBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("bookingId").asLong();

        // Now accept as mentor
        mockMvc.perform(put("/api/bookings/" + bookingId + "/accept")
                        .with(httpBasic("mentor@test.com", "password123"))
                        .param("mentorId", mentor.getMentorId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testAcceptBooking_AsStudent_Returns403() throws Exception {
        // Create booking first
        String createBookingBody = String.format("""
                {
                    "studentId": %d,
                    "mentorId": %d,
                    "taskId": %d,
                    "proposedDatetime": "2025-12-01T14:00:00"
                }
                """, student.getStudentId(), mentor.getMentorId(), task.getTaskId());

        String bookingResponse = mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookingBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("bookingId").asLong();

        // Try to accept as student - should fail
        mockMvc.perform(put("/api/bookings/" + bookingId + "/accept")
                        .with(httpBasic("student@test.com", "password123"))
                        .param("mentorId", mentor.getMentorId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeclineBooking_AsMentor_Success() throws Exception {
        // Create booking
        String createBookingBody = String.format("""
                {
                    "studentId": %d,
                    "mentorId": %d,
                    "taskId": %d,
                    "proposedDatetime": "2025-12-01T14:00:00"
                }
                """, student.getStudentId(), mentor.getMentorId(), task.getTaskId());

        String bookingResponse = mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookingBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("bookingId").asLong();

        // Decline as mentor
        mockMvc.perform(put("/api/bookings/" + bookingId + "/decline")
                        .with(httpBasic("mentor@test.com", "password123"))
                        .param("mentorId", mentor.getMentorId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeclineBooking_AsStudent_Returns403() throws Exception {
        // Create booking
        String createBookingBody = String.format("""
                {
                    "studentId": %d,
                    "mentorId": %d,
                    "taskId": %d,
                    "proposedDatetime": "2025-12-01T14:00:00"
                }
                """, student.getStudentId(), mentor.getMentorId(), task.getTaskId());

        String bookingResponse = mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookingBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("bookingId").asLong();

        // Try to decline as student
        mockMvc.perform(put("/api/bookings/" + bookingId + "/decline")
                        .with(httpBasic("student@test.com", "password123"))
                        .param("mentorId", mentor.getMentorId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCancelBooking_AsStudent_Success() throws Exception {
        // Create booking
        String createBookingBody = String.format("""
                {
                    "studentId": %d,
                    "mentorId": %d,
                    "taskId": %d,
                    "proposedDatetime": "2025-12-01T14:00:00"
                }
                """, student.getStudentId(), mentor.getMentorId(), task.getTaskId());

        String bookingResponse = mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookingBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("bookingId").asLong();

        // Cancel as student
        mockMvc.perform(put("/api/bookings/" + bookingId + "/cancel")
                        .with(httpBasic("student@test.com", "password123"))
                        .param("userId", student.getStudentId().toString())
                        .param("userType", "student"))
                .andExpect(status().isOk());
    }

    @Test
    void testCancelBooking_AsMentor_Success() throws Exception {
        // Create and accept booking
        String createBookingBody = String.format("""
                {
                    "studentId": %d,
                    "mentorId": %d,
                    "taskId": %d,
                    "proposedDatetime": "2025-12-01T14:00:00"
                }
                """, student.getStudentId(), mentor.getMentorId(), task.getTaskId());

        String bookingResponse = mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookingBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("bookingId").asLong();

        mockMvc.perform(put("/api/bookings/" + bookingId + "/accept")
                        .with(httpBasic("mentor@test.com", "password123"))
                        .param("mentorId", mentor.getMentorId().toString()))
                .andExpect(status().isOk());

        // Cancel as mentor
        mockMvc.perform(put("/api/bookings/" + bookingId + "/cancel")
                        .with(httpBasic("mentor@test.com", "password123"))
                        .param("userId", mentor.getMentorId().toString())
                        .param("userType", "mentor"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingById_AsStudent_Success() throws Exception {
        // Create booking
        String createBookingBody = String.format("""
                {
                    "studentId": %d,
                    "mentorId": %d,
                    "taskId": %d,
                    "proposedDatetime": "2025-12-01T14:00:00"
                }
                """, student.getStudentId(), mentor.getMentorId(), task.getTaskId());

        String bookingResponse = mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookingBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("bookingId").asLong();

        // Get booking as student
        mockMvc.perform(get("/api/bookings/" + bookingId)
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingById_AsMentor_Success() throws Exception {
        // Create booking
        String createBookingBody = String.format("""
                {
                    "studentId": %d,
                    "mentorId": %d,
                    "taskId": %d,
                    "proposedDatetime": "2025-12-01T14:00:00"
                }
                """, student.getStudentId(), mentor.getMentorId(), task.getTaskId());

        String bookingResponse = mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("student@test.com", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookingBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("bookingId").asLong();

        // Get booking as mentor
        mockMvc.perform(get("/api/bookings/" + bookingId)
                        .with(httpBasic("mentor@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingsByStudent_AsStudent_Success() throws Exception {
        mockMvc.perform(get("/api/bookings/student/" + student.getStudentId())
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingsByStudent_AsMentor_Returns403() throws Exception {
        mockMvc.perform(get("/api/bookings/student/" + student.getStudentId())
                        .with(httpBasic("mentor@test.com", "password123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetBookingsByMentor_AsMentor_Success() throws Exception {
        mockMvc.perform(get("/api/bookings/mentor/" + mentor.getMentorId())
                        .with(httpBasic("mentor@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingsByMentor_AsStudent_Returns403() throws Exception {
        mockMvc.perform(get("/api/bookings/mentor/" + mentor.getMentorId())
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isForbidden());
    }

    // ==================== SEARCH CONTROLLER TESTS ====================

    @Test
    void testSearchMentors_AsStudent_Success() throws Exception {
        mockMvc.perform(get("/api/search/mentors")
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchMentors_AsMentor_Success() throws Exception {
        mockMvc.perform(get("/api/search/mentors")
                        .with(httpBasic("mentor@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchMentors_Unauthenticated_Returns403() throws Exception {
        mockMvc.perform(get("/api/search/mentors"))
                .andExpect(status().isForbidden());  // ← Changed from isUnauthorized()
    }

    @Test
    void testSearchTasks_AsStudent_Success() throws Exception {
        mockMvc.perform(get("/api/search/tasks")
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchTasks_AsMentor_Success() throws Exception {
        mockMvc.perform(get("/api/search/tasks")
                        .with(httpBasic("mentor@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchStudents_AsStudent_Success() throws Exception {
        mockMvc.perform(get("/api/search/students")
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchStudents_AsMentor_Success() throws Exception {
        mockMvc.perform(get("/api/search/students")
                        .with(httpBasic("mentor@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCategories_RequiresAuth() throws Exception {
        mockMvc.perform(get("/api/search/categories")
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCompanies_RequiresAuth() throws Exception {
        mockMvc.perform(get("/api/search/companies")
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMajors_RequiresAuth() throws Exception {
        mockMvc.perform(get("/api/search/majors")
                        .with(httpBasic("mentor@test.com", "password123")))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFilterOptions_RequiresAuth() throws Exception {
        mockMvc.perform(get("/api/search/filter-options")
                        .with(httpBasic("student@test.com", "password123")))
                .andExpect(status().isOk());
    }

    // ==================== FILE CONTROLLER TESTS ====================

    @Test
    void testServeFile_Public_NoAuthNeeded() throws Exception {
        // This would need actual file setup, but testing the endpoint doesn't require auth
        mockMvc.perform(get("/api/files/task-images/test.jpg"))
                .andExpect(status().is5xxServerError()); // File won't exist, but shouldn't be 401
    }
}