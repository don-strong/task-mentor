package com.task_mentor.task_mentor.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task_mentor.task_mentor.dto.*;
import com.task_mentor.task_mentor.entity.*;
import com.task_mentor.task_mentor.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TaskMentorIntegrationTest - Comprehensive integration tests for the Task Mentor application
 * Tests all major features: User authentication, Student/Mentor profiles, Tasks, and Bookings
 *
 * @author Integration Test Suite
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TaskMentorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private User studentUser;
    private User mentorUser;
    private Student student;
    private Mentor mentor;
    private Task task;

    @BeforeEach
    public void setUp() {
        // Clean up database
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

        // Create test mentor user
        mentorUser = new User();
        mentorUser.setEmail("mentor@test.com");
        mentorUser.setPassword(passwordEncoder.encode("password123"));
        mentorUser.setAccountType("mentor");
        mentorUser.setCreatedAt(LocalDateTime.now());
        mentorUser = userRepository.save(mentorUser);

        // Create test student profile
        student = new Student();
        student.setUser(studentUser);
        student.setName("John Student");
        student.setBio("Computer Science student");
        student.setMajor("Computer Science");
        student.setGraduationYear(2025);
        student.setCareerInterests("Software Development");
        student.setProfilePhotoUrl("https://example.com/student.jpg");
        student = studentRepository.save(student);

        // Create test mentor profile
        mentor = new Mentor();
        mentor.setUser(mentorUser);
        mentor.setName("Dr. Jane Mentor");
        mentor.setBio("Senior Software Engineer with 10 years experience");
        mentor.setRoleTitle("Senior Software Engineer");
        mentor.setCompany("Tech Corp");
        mentor.setYearsExperience(10);
        mentor.setIndustries("Technology");
        mentor.setExpertiseAreas("Java, Python, Cloud");
        mentor.setProfilePhotoUrl("https://example.com/mentor.jpg");
        mentor.setCreatedAt(LocalDateTime.now());
        mentor = mentorRepository.save(mentor);

        // Create test task
        task = new Task();
        task.setMentor(mentor);
        task.setTitle("Java Programming Task");
        task.setDescription("Learn basic Java concepts");
        task.setDurationMinutes(60);
        task.setCategory("Programming");
        task.setCreatedAt(LocalDateTime.now());
        task = taskRepository.save(task);
    }

    // ===== STUDENT PROFILE TESTS =====

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testCreateStudentProfile() throws Exception {
        // Clean up existing student for this test
        studentRepository.deleteAll();

        CreateStudentRequest request = new CreateStudentRequest();
        request.setName("New Student");
        request.setBio("Aspiring developer");
        request.setMajor("Computer Science");
        request.setGraduationYear(2026);
        request.setCareerInterests("Web Development");
        request.setProfilePhotoUrl("https://example.com/new-student.jpg");

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Student"))
                .andExpect(jsonPath("$.major").value("Computer Science"))
                .andExpect(jsonPath("$.graduationYear").value(2026));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testGetMyStudentProfile() throws Exception {
        mockMvc.perform(get("/api/students/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Student"))
                .andExpect(jsonPath("$.major").value("Computer Science"))
                .andExpect(jsonPath("$.graduationYear").value(2025));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testUpdateStudentProfile() throws Exception {
        StudentRequest request = new StudentRequest();
        request.setName("John Updated Student");
        request.setBio("Updated bio");
        request.setMajor("Software Engineering");
        request.setGraduationYear(2026);
        request.setCareerInterests("Full Stack Development");
        request.setProfilePhotoUrl("https://example.com/updated.jpg");

        mockMvc.perform(put("/api/students/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated Student"))
                .andExpect(jsonPath("$.major").value("Software Engineering"))
                .andExpect(jsonPath("$.graduationYear").value(2026));
    }

    @Test
    public void testGetStudentById() throws Exception {
        mockMvc.perform(get("/api/students/" + student.getStudentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Student"))
                .andExpect(jsonPath("$.major").value("Computer Science"));
    }

    @Test
    public void testGetAllStudents() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name").value("John Student"));
    }

    // ===== MENTOR PROFILE TESTS =====

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testCreateMentorProfile() throws Exception {
        // Clean up existing mentor for this test
        mentorRepository.deleteAll();

        MentorCreateRequest request = new MentorCreateRequest();
        request.setUserId(mentorUser.getUserId());
        request.setName("New Mentor");
        request.setBio("Experienced developer");
        request.setRoleTitle("Tech Lead");
        request.setCompany("Startup Inc");
        request.setYearsExperience(5);
        request.setIndustries("Technology, Finance");
        request.setExpertiseAreas("JavaScript, React, Node.js");
        request.setProfilePhotoUrl("https://example.com/new-mentor.jpg");

        mockMvc.perform(post("/api/mentors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Mentor"))
                .andExpect(jsonPath("$.company").value("Startup Inc"));
    }

    @Test
    public void testGetMentorProfile() throws Exception {
        mockMvc.perform(get("/api/mentors/" + mentor.getMentorId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Jane Mentor"))
                .andExpect(jsonPath("$.company").value("Tech Corp"))
                .andExpect(jsonPath("$.yearsExperience").value(10));
    }

    @Test
    public void testGetAllMentors() throws Exception {
        mockMvc.perform(get("/api/mentors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name").value("Dr. Jane Mentor"));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testUpdateMentorProfile() throws Exception {
        MentorUpdateRequest request = new MentorUpdateRequest();
        request.setName("Dr. Jane Mentor Updated");
        request.setBio("Updated bio with more details");
        request.setRoleTitle("Principal Engineer");
        request.setCompany("Tech Corp");
        request.setYearsExperience(12);
        request.setIndustries("Technology, Finance, Healthcare");
        request.setExpertiseAreas("Java, Python, Cloud Architecture");
        request.setProfilePhotoUrl(null);

        mockMvc.perform(put("/api/mentors/" + mentor.getMentorId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Jane Mentor Updated"))
                .andExpect(jsonPath("$.yearsExperience").value(12));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testDeleteMentorProfile() throws Exception {
        mockMvc.perform(delete("/api/mentors/" + mentor.getMentorId())
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes())))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/mentors/" + mentor.getMentorId()))
                .andExpect(status().isNotFound());
    }

    // ===== TASK TESTS =====

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testCreateTask() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("New Programming Task");
        request.setDescription("Learn advanced algorithms and data structures with practical examples");
        request.setMentorId(mentor.getMentorId());
        request.setDurationMinutes(90);
        request.setCategory("Programming");

        MockMultipartFile taskPart = new MockMultipartFile(
                "task",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        mockMvc.perform(multipart("/api/tasks")
                        .file(taskPart)
                        .file(imagePart)
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Programming Task"))
                .andExpect(jsonPath("$.description").value("Learn advanced algorithms and data structures with practical examples"))
                .andExpect(jsonPath("$.durationMinutes").value(90));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testCreateTaskWithoutImage() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Task Without Image");
        request.setDescription("This is a simple task without any image attachment for testing purposes");
        request.setMentorId(mentor.getMentorId());
        request.setDurationMinutes(30);
        request.setCategory("Discussion");

        MockMultipartFile taskPart = new MockMultipartFile(
                "task",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/tasks")
                        .file(taskPart)
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Task Without Image"));
    }

    @Test
    public void testGetTaskById() throws Exception {
        mockMvc.perform(get("/api/tasks/" + task.getTaskId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Programming Task"))
                .andExpect(jsonPath("$.description").value("Learn basic Java concepts"));
    }

    @Test
    public void testGetAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title").value("Java Programming Task"));
    }

    @Test
    public void testGetTasksByCategory() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .param("category", "Programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].category").value("Programming"));
    }

    @Test
    public void testGetTasksByMentor() throws Exception {
        mockMvc.perform(get("/api/tasks/mentor/" + mentor.getMentorId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].mentorId").value(mentor.getMentorId()));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testUpdateTask() throws Exception {
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated Java Task");
        request.setDescription("Updated description with more comprehensive details about the task");
        request.setDurationMinutes(120);
        request.setCategory("Advanced Programming");

        MockMultipartFile taskPart = new MockMultipartFile(
                "task",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "updated-image.jpg",
                "image/jpeg",
                "updated image content".getBytes()
        );

        mockMvc.perform(multipart("/api/tasks/" + task.getTaskId())
                        .file(taskPart)
                        .file(imagePart)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Java Task"))
                .andExpect(jsonPath("$.description").value("Updated description with more comprehensive details about the task"))
                .andExpect(jsonPath("$.durationMinutes").value(120));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testUpdateTaskWithoutImage() throws Exception {
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated Task Title Only");
        request.setDescription("Keep everything else the same but update the title for this task");

        MockMultipartFile taskPart = new MockMultipartFile(
                "task",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/tasks/" + task.getTaskId())
                        .file(taskPart)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task Title Only"));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + task.getTaskId())
                        .param("mentorId", mentor.getMentorId().toString())
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes())))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/tasks/" + task.getTaskId()))
                .andExpect(status().isNotFound());
    }

    // ===== BOOKING TESTS =====

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testCreateBooking() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setStudentId(student.getStudentId());
        request.setMentorId(mentor.getMentorId());
        request.setTaskId(task.getTaskId());
        request.setProposedDatetime(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.studentId").value(student.getStudentId()))
                .andExpect(jsonPath("$.mentorId").value(mentor.getMentorId()));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testCreateBookingWithPastDate() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setStudentId(student.getStudentId());
        request.setMentorId(mentor.getMentorId());
        request.setTaskId(task.getTaskId());
        request.setProposedDatetime(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testAcceptBooking() throws Exception {
        // Create a booking first
        Booking booking = new Booking(student, mentor, task, LocalDateTime.now().plusDays(1));
        booking = bookingRepository.save(booking);

        mockMvc.perform(put("/api/bookings/" + booking.getBookingId() + "/accept")
                        .param("mentorId", mentor.getMentorId().toString())
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accepted"));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testDeclineBooking() throws Exception {
        // Create a booking first
        Booking booking = new Booking(student, mentor, task, LocalDateTime.now().plusDays(2));
        booking = bookingRepository.save(booking);

        mockMvc.perform(put("/api/bookings/" + booking.getBookingId() + "/decline")
                        .param("mentorId", mentor.getMentorId().toString())
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("declined"));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testCancelBooking() throws Exception {
        // Create a booking first
        Booking booking = new Booking(student, mentor, task, LocalDateTime.now().plusDays(3));
        booking = bookingRepository.save(booking);

        mockMvc.perform(put("/api/bookings/" + booking.getBookingId() + "/cancel")
                        .param("userId", student.getStudentId().toString())
                        .param("userType", "student")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testGetBookingById() throws Exception {
        // Create a booking first
        Booking booking = new Booking(student, mentor, task, LocalDateTime.now().plusDays(4));
        booking = bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/" + booking.getBookingId())
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(booking.getBookingId()))
                .andExpect(jsonPath("$.status").value("pending"));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testGetBookingsByStudent() throws Exception {
        // Create a booking first
        Booking booking = new Booking(student, mentor, task, LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/student/" + student.getStudentId())
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testGetBookingsByMentor() throws Exception {
        // Create a booking first
        Booking booking = new Booking(student, mentor, task, LocalDateTime.now().plusDays(6));
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/mentor/" + mentor.getMentorId())
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testGetBookingsByMentorAndStatus() throws Exception {
        // Create bookings with different statuses
        Booking pending = new Booking(student, mentor, task, LocalDateTime.now().plusDays(7));
        bookingRepository.save(pending);

        mockMvc.perform(get("/api/bookings/mentor/" + mentor.getMentorId())
                        .param("status", "pending")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].status").value("pending"));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testGetBookingsByTask() throws Exception {
        // Create a booking first
        Booking booking = new Booking(student, mentor, task, LocalDateTime.now().plusDays(8));
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/task/" + task.getTaskId())
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testDeleteBooking() throws Exception {
        // Create a booking first (not accepted)
        Booking booking = new Booking(student, mentor, task, LocalDateTime.now().plusDays(9));
        booking = bookingRepository.save(booking);

        mockMvc.perform(delete("/api/bookings/" + booking.getBookingId())
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/bookings/" + booking.getBookingId())
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isNotFound());
    }

    // ===== SEARCH TESTS =====

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testSearchMentors() throws Exception {
        mockMvc.perform(get("/api/search/mentors")
                        .param("name", "Jane")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mentors", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.mentors[0].name", containsString("Jane")))
                .andExpect(jsonPath("$.count", greaterThanOrEqualTo(1)));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testSearchMentorsByCompany() throws Exception {
        mockMvc.perform(get("/api/search/mentors")
                        .param("company", "Tech Corp")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mentors", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.mentors[0].company").value("Tech Corp"));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testSearchTasks() throws Exception {
        mockMvc.perform(get("/api/search/tasks")
                        .param("category", "Programming")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.tasks[0].category").value("Programming"))
                .andExpect(jsonPath("$.count", greaterThanOrEqualTo(1)));
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testSearchStudents() throws Exception {
        mockMvc.perform(get("/api/search/students")
                        .param("major", "Computer Science")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.count", greaterThanOrEqualTo(1)));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/search/categories")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.count", greaterThanOrEqualTo(1)));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testGetAllCompanies() throws Exception {
        mockMvc.perform(get("/api/search/companies")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companies", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.count", greaterThanOrEqualTo(1)));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testGetAllMajors() throws Exception {
        mockMvc.perform(get("/api/search/majors")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.majors", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.count", greaterThanOrEqualTo(1)));
    }

    // ===== EDGE CASE AND ERROR TESTS =====

    @Test
    public void testGetNonExistentStudent() throws Exception {
        mockMvc.perform(get("/api/students/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetNonExistentMentor() throws Exception {
        mockMvc.perform(get("/api/mentors/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetNonExistentTask() throws Exception {
        mockMvc.perform(get("/api/tasks/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testGetNonExistentBooking() throws Exception {
        mockMvc.perform(get("/api/bookings/99999")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("student@test.com:password123".getBytes())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    public void testCreateStudentWithInvalidData() throws Exception {
        studentRepository.deleteAll();

        CreateStudentRequest request = new CreateStudentRequest();
        request.setName("A"); // Too short
        request.setMajor("CS");
        request.setGraduationYear(2025);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "mentor@test.com", roles = "MENTOR")
    public void testCreateTaskWithInvalidMentor() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Invalid Task");
        request.setDescription("This should fail because the mentor ID does not exist in the database");
        request.setMentorId(99999L); // Non-existent mentor
        request.setDurationMinutes(60);
        request.setCategory("Programming");

        MockMultipartFile taskPart = new MockMultipartFile(
                "task",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/tasks")
                        .file(taskPart)
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("mentor@test.com:password123".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}