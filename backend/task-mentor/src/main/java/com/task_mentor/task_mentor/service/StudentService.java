package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * StudentService - Business logic for Student profile management
 * Handles CRUD operations, validation, and business rules for student profiles
 * 
 * @author James No
 */
@Service
@Transactional
public class StudentService {
    
    private final StudentRepository studentRepository;
    
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    /**
     * Create a new student profile
     * Validates that student doesn't already exist for this user
     */
    public Student createStudent(Student student) {
        // Validation: Check if user is provided
        if (student.getUser() == null || student.getUser().getUserId() == null) {
            throw new IllegalArgumentException("User is required for student profile");
        }
        
        // Validation: Check if student profile already exists for this user
        if (studentRepository.existsByUserId(student.getUser().getUserId())) {
            throw new IllegalArgumentException("Student profile already exists for user ID: " + student.getUser().getUserId());
        }
        
        // Validation: Required fields
        validateStudent(student);
        
        return studentRepository.save(student);
    }
    
    /**
     * Get student by student ID
     */
    public Optional<Student> getStudentById(Long studentId) {
        return studentRepository.findById(studentId);
    }
    
    /**
     * Get student profile by user ID
     * Used when a logged-in user wants to view/edit their student profile
     */
    public Optional<Student> getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId);
    }
    
    /**
     * Get all students
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    /**
     * Update student profile
     */
    public Student updateStudent(Long studentId, Student updatedStudent) {
        Student existing = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));
        
        // Validation: Required fields
        validateStudent(updatedStudent);
        
        // Update fields
        existing.setName(updatedStudent.getName());
        existing.setBio(updatedStudent.getBio());
        existing.setMajor(updatedStudent.getMajor());
        existing.setGraduationYear(updatedStudent.getGraduationYear());
        existing.setCareerInterests(updatedStudent.getCareerInterests());
        existing.setProfilePhotoUrl(updatedStudent.getProfilePhotoUrl());
        
        return studentRepository.save(existing);
    }
    
    /**
     * Delete student profile
     */
    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }
        studentRepository.deleteById(studentId);
    }
    
    /**
     * Search students by major
     */
    public List<Student> getStudentsByMajor(String major) {
        return studentRepository.findByMajor(major);
    }
    
    /**
     * Search students by graduation year
     */
    public List<Student> getStudentsByGraduationYear(Integer year) {
        return studentRepository.findByGraduationYear(year);
    }
    
    /**
     * Search students by name (partial match, case-insensitive)
     */
    public List<Student> searchStudentsByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Search students by career interest
     */
    public List<Student> getStudentsByCareerInterest(String interest) {
        return studentRepository.findByCareerInterest(interest);
    }
    
    /**
     * Get current students (graduating in current year or later)
     */
    public List<Student> getCurrentStudents(Integer currentYear) {
        return studentRepository.findByGraduationYearGreaterThanEqual(currentYear);
    }
    
    /**
     * Validate student data
     * Business rules for student profiles
     */
    private void validateStudent(Student student) {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student name is required");
        }
        
        if (student.getUser() == null || student.getUser().getUserId() == null) {
            throw new IllegalArgumentException("User is required");
        }
        
        // Optional: Validate graduation year is reasonable
        if (student.getGraduationYear() != null) {
            int currentYear = java.time.Year.now().getValue();
            if (student.getGraduationYear() < currentYear - 10 || student.getGraduationYear() > currentYear + 10) {
                throw new IllegalArgumentException("Graduation year must be within 10 years of current year");
            }
        }
    }
}
