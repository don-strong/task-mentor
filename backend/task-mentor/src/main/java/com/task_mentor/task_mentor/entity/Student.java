package com.task_mentor.task_mentor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Student Entity - Stores student-specific profile information
 * Corresponds to the 'students' table in PostgreSQL
 * 
 * @author James No
 */
@Entity
@Table(name = "students")
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;
    
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "major", length = 100)
    private String major;
    
    @Column(name = "graduation_year")
    private Integer graduationYear;
    
    @Column(name = "career_interests", columnDefinition = "TEXT")
    private String careerInterests;
    
    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public Student() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Student(Long userId, String name) {
        this.userId = userId;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getMajor() {
        return major;
    }
    
    public void setMajor(String major) {
        this.major = major;
    }
    
    public Integer getGraduationYear() {
        return graduationYear;
    }
    
    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }
    
    public String getCareerInterests() {
        return careerInterests;
    }
    
    public void setCareerInterests(String careerInterests) {
        this.careerInterests = careerInterests;
    }
    
    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }
    
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", major='" + major + '\'' +
                ", graduationYear=" + graduationYear +
                ", createdAt=" + createdAt +
                '}';
    }
}
