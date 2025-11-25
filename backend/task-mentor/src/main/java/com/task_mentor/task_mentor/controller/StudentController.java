package com.task_mentor.task_mentor.controller;

import com.task_mentor.task_mentor.dto.CreateStudentRequest;
import com.task_mentor.task_mentor.dto.StudentStatistics;
import com.task_mentor.task_mentor.dto.StudentRequest;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.User;
import com.task_mentor.task_mentor.repository.UserRepository;
import com.task_mentor.task_mentor.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Student Profile Management
 *
 * Endpoints:
 * - POST   /api/students     - Create student profile
 * - GET    /api/students/me  - Get my profile
 * - PUT    /api/students/me  - Update my profile
 * - GET    /api/students/{id} - Get student by ID
 * - GET    /api/students     - List all students
 *
 * @author Tyson Ringelstetter
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserRepository userRepository;

    /**
     * POST /api/students
     * Create a new student profile for the authenticated user
     *
     * Security: Only authenticated users can create their own profile
     *
     * @param request The student profile data
     * @param authentication Spring Security authentication object (auto-injected)
     * @return 201 CREATED with StudentStatistics, or error message
     */

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    public ResponseEntity<?> createStudentProfile(
            @RequestBody CreateStudentRequest request,
            Authentication authentication) {
        try {
            // Step 1: Get currently logged-in user's email
            String userEmail = authentication.getName();

            // Step 2: Find user entity in database
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Step 3: Call service to create the student
            // Service layer handles validation and business logic
            Student student = studentService.createStudent(
                    user.getUserId(),
                    request.getName(),
                    request.getBio(),
                    request.getMajor(),
                    request.getGraduationYear(),
                    request.getCareerInterests(),
                    request.getProfilePhotoUrl()
            );

            // Step 4: Convert entity to DTO ( safe for REST responses,
            // (Because StudentStatistics doesn't expose sensitive user data) )
            StudentStatistics response = studentService.getStudentStatistics(
                    student.getStudentId()
            );

            // Step 5: Return 201 CREATED with the profile data
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // Business logic errors (e.g. User is already a student)
            // Return a 400 BAD REQUEST for client errors
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // unexpected errors - return 500 INTERNAL SERVER ERROR for server errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating student profile:" + e.getMessage());
        }

    }

    /**
     * GET /api/students/me
     * Get the authenticated user's own student profile
     *
     * Security: Returns only YOUR profile, not someone else's
     *
     * @param authentication Auto-injected by Spring Security
     * @return 200 OK with StudentStatistics, or error 404 if profile doesn't exist
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            // Get Logged-in User's Email
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Get student profile by userID (not studentID!)
            Student student = studentService.getStudentByUserId(user.getUserId());

            // Convert to DTO for response
            StudentStatistics response = studentService.getStudentStatistics(student.getStudentId());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            //Profile does not exist yet for this user
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student profile not found. Please create your profile.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving your profile: " + e.getMessage());
        }
    }

    /**
     * GET /api/students/me
     * Get the authenticated user's own student profile
     *
     * Security: Returns only YOUR profile, not someone else's
     *
     * @param authentication Auto-injected by Spring Security
     * @return 200 OK with StudentStatistics, or 404 if the profile doesn't exist
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @RequestBody StudentRequest request,
            Authentication authentication) {

        try {
             // Get Logged-in User's Email
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update student profile by userID
            Student updatedStudent = studentService.updateStudentProfile(
                    user.getUserId(),
                    request.getName(),
                    request.getBio(),
                    request.getMajor(),
                    request.getGraduationYear(),
                    request.getCareerInterests(),
                    request.getProfilePhotoUrl()
            );

            // Convert to DTO for response
            StudentStatistics response = studentService.getStudentStatistics(updatedStudent.getStudentId());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Validation errors (ex: "Name must be atleast 2 characters")
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating your profile: " + e.getMessage());
        }
    }

    /**
     * GET /api/students/{id}
     * Get any student's profile by their student ID
     *
     * Security: PUBLIC endpoint - anyone can view student profiles
     * WHY: Mentors need to browse students to find mentees
     *
     * @param id The student ID
     * @return 200 OK with StudentStatistics, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            StudentStatistics student = studentService.getStudentStatistics(id);
            return ResponseEntity.ok(student);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student with ID " + id + " not found.");
        }
    }
    /**
     * GET /api/students
     * List all student profiles
     *
     * Security: PUBLIC endpoint - anyone can browse
     * WHY: Mentors browse students, students can network
     *
     * @return 200 OK with List of StudentStatistics
     */
    @GetMapping
    public ResponseEntity<List<StudentStatistics>> getAllStudents() {
        List<StudentStatistics> students = studentService.getAllStudentStatistics();
        return ResponseEntity.ok(students);
    }
}
