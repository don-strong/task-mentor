package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.dto.StudentStatistics;
import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.User;
import com.task_mentor.task_mentor.repository.StudentRepository;
import com.task_mentor.task_mentor.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String DEFAULT_STUDENT_IMAGE = "https://api.dicebear.com/7.x/avataaars/svg?seed=default";

    public Student createStudent(Long userId, String name, String bio, String major, Integer graduationYear,
                                 String careerInterests, String profilePhotoUrl){
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("User not found"));

        if(!"student".equalsIgnoreCase(user.getAccountType())){
            throw new IllegalArgumentException("User not a Student");
        }

        if(studentRepository.existsByUserId(userId)){
            throw new IllegalArgumentException("User is already a Student");
        }

        validateStudentData(name,major,graduationYear);

        if(profilePhotoUrl != null && !profilePhotoUrl.trim().isEmpty()) {
            validateProfilePhotoUrl(profilePhotoUrl);
        }

        Student student = new Student();
        student.setUser(user);
        student.setName(name.trim());
        student.setBio(bio != null ? bio.trim() : null);
        student.setMajor(major != null ? major.trim() : null);
        student.setGraduationYear(graduationYear);
        student.setCareerInterests(careerInterests != null ? careerInterests.trim() : null);
        if(profilePhotoUrl != null && !profilePhotoUrl.trim().isEmpty()) {
            student.setProfilePhotoUrl(profilePhotoUrl);
        }else{
            student.setProfilePhotoUrl(DEFAULT_STUDENT_IMAGE);
        }

        return studentRepository.save(student);

    }

    public Student updateStudentProfile(Long userId, String name, String bio, String major, Integer graduationYear,
                                      String careerInterests, String profilePhotoUrl){
        // Fixed: changed from findById to findByUserId to match parameter name,
        // fixes error 400  BAD REQUEST bug when updating profiles
        Student student = studentRepository.findByUserId(userId).orElseThrow(()->
                new IllegalArgumentException("Student not found"));

        if(name != null && !name.trim().isEmpty()){
            validateName(name);
            student.setName(name.trim());
        }

        if(bio != null){
            student.setBio(bio.trim().isEmpty() ? null : bio.trim());
        }

        if(graduationYear != null){
            validateGraduationYear(graduationYear);
            student.setGraduationYear(graduationYear);
        }

        if(careerInterests != null){
            student.setCareerInterests(careerInterests.trim().isEmpty() ? null : careerInterests.trim());
        }

        if(profilePhotoUrl != null){
            if(!profilePhotoUrl.trim().isEmpty()){
                validateProfilePhotoUrl(profilePhotoUrl);
                student.setProfilePhotoUrl(profilePhotoUrl.trim());
            }else{
                student.setProfilePhotoUrl(DEFAULT_STUDENT_IMAGE);
            }
        }

        return studentRepository.save(student);

    }

    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(()->
                new IllegalArgumentException("Student with that id not found"));
    }

    public Student getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId).orElseThrow(()->
                new IllegalArgumentException("Student with that userId not found"));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void deleteStudentProfile(Long studentId){
        if(!studentRepository.existsById(studentId)){
            throw new IllegalArgumentException("Student with that id not found");
        }
        studentRepository.deleteById(studentId);
    }

    public boolean StudentProfileExists(Long studentId){
        return studentRepository.existsById(studentId);
    }

    public List<Student> searchStudentByName(String name){
        if (name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Student> searchStudentByMajor(String major){
        if (major == null || major.trim().isEmpty()){
            throw new IllegalArgumentException("Student major cannot be empty");
        }
        return studentRepository.findByMajor(major);
    }

    public List<Student> searchStudentByGraduationYear(Integer graduationYear){
        validateGraduationYear(graduationYear);
        return studentRepository.findByGraduationYear(graduationYear);
    }

    public List<Student> searchStudentByCareerInterests(String careerInterests){
        if (careerInterests == null || careerInterests.trim().isEmpty()){
            throw new IllegalArgumentException("Student career interests cannot be empty");
        }
        return studentRepository.findByCareerInterest(careerInterests);
    }

    public StudentStatistics getStudentStatistics(Long studentId) {
        Student student = getStudentById(studentId);

        StudentStatistics stats = new StudentStatistics();
        stats.setStudentId(student.getStudentId());
        stats.setName(student.getName());
        stats.setBio(student.getBio());
        stats.setMajor(student.getMajor());
        stats.setGraduationYear(student.getGraduationYear());
        stats.setCareerInterests(student.getCareerInterests());
        stats.setProfilePhotoUrl(student.getProfilePhotoUrl());

        return stats;
    }

    public List<StudentStatistics> getAllStudentStatistics() {
        List<Student> students = getAllStudents();

        return students.stream()
                .map(student -> {
                    StudentStatistics stats = new StudentStatistics();
                    stats.setStudentId(student.getStudentId());
                    stats.setName(student.getName());
                    stats.setBio(student.getBio());
                    stats.setMajor(student.getMajor());
                    stats.setGraduationYear(student.getGraduationYear());
                    stats.setCareerInterests(student.getCareerInterests());
                    stats.setProfilePhotoUrl(student.getProfilePhotoUrl());
                    return stats;
                })
                .toList();
    }


    private void validateStudentData(String name, String major, Integer graduationYear) {
        validateName(name);
        validateMajor(major);
        validateGraduationYear(graduationYear);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name is required");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Student name must be at least 2 characters long");
        }
        if (name.length() > 150) {
            throw new IllegalArgumentException("Student name must not exceed 150 characters");
        }
    }

    private void validateMajor(String major){
        if (major == null || major.trim().isEmpty()){
            throw new IllegalArgumentException("Student major is required");
        }
        if(major.length() > 100){
            throw new IllegalArgumentException("Student major must be less than 100 characters");
        }
    }

    private void validateGraduationYear(Integer year) {
        if (year == null) {
            throw new IllegalArgumentException("Graduation year is required");
        }
        int currentYear = LocalDateTime.now().getYear();
        if (year < currentYear - 10) {
            throw new IllegalArgumentException(
                    "Graduation year cannot be more than 10 years in the past (before " + (currentYear - 10) + ")");
        }
        if (year > currentYear + 10) {
            throw new IllegalArgumentException(
                    "Graduation year cannot be more than 10 years in the future (after " + (currentYear + 10) + ")");
        }
    }

    private void validateProfilePhotoUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return; // URL is optional
        }
        if (url.length() > 500) {
            throw new IllegalArgumentException("Profile photo URL must not exceed 500 characters");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("Profile photo URL must be a valid HTTP or HTTPS URL");
        }
    }



}
