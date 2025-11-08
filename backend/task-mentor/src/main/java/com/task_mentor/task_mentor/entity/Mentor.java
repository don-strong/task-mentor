package com.task_mentor.task_mentor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Mentor Entity - Stores mentor-specific profile information
 * Corresponds to the 'mentors' table in PostgreSQL
 * 
 * @author James No
 */
@Entity
@Table(name = "mentors")
public class Mentor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentor_id")
    private Long mentorId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;
    
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "role_title", length = 150)
    private String roleTitle;
    
    @Column(name = "company", length = 150)
    private String company;
    
    @Column(name = "years_experience")
    private Integer yearsExperience;
    
    @Column(name = "industries", columnDefinition = "TEXT")
    private String industries;
    
    @Column(name = "expertise_areas", columnDefinition = "TEXT")
    private String expertiseAreas;
    
    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public Mentor() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Mentor(Long userId, String name) {
        this.userId = userId;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getMentorId() {
        return mentorId;
    }
    
    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
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
    
    public String getRoleTitle() {
        return roleTitle;
    }
    
    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public Integer getYearsExperience() {
        return yearsExperience;
    }
    
    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }
    
    public String getIndustries() {
        return industries;
    }
    
    public void setIndustries(String industries) {
        this.industries = industries;
    }
    
    public String getExpertiseAreas() {
        return expertiseAreas;
    }
    
    public void setExpertiseAreas(String expertiseAreas) {
        this.expertiseAreas = expertiseAreas;
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
        return "Mentor{" +
                "mentorId=" + mentorId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", roleTitle='" + roleTitle + '\'' +
                ", company='" + company + '\'' +
                ", yearsExperience=" + yearsExperience +
                ", createdAt=" + createdAt +
                '}';
    }
}
