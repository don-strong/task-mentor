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


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Student() {

    }

    public Student(Long studentId, User user, String name, String bio, String major, Integer graduationYear,
                   String careerInterests, String profilePhotoUrl, LocalDateTime createdAt) {
        this.studentId = studentId;
        this.user = user;
        this.name = name;
        this.bio = bio;
        this.major = major;
        this.graduationYear = graduationYear;
        this.careerInterests = careerInterests;
        this.profilePhotoUrl = profilePhotoUrl;
        this.createdAt = createdAt;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                ", userId=" + user +
                ", name='" + name + '\'' +
                ", major='" + major + '\'' +
                ", graduationYear=" + graduationYear +
                ", createdAt=" + createdAt +
                '}';
    }
}