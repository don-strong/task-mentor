package com.task_mentor.task_mentor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Mentor() {

    }

    public Mentor(Long mentorId, User user, List<Task> tasks, String name, String bio, String roleTitle, String company,
                  Integer yearsExperience, String industries, String expertiseAreas, String profilePhotoUrl,
                  LocalDateTime createdAt) {
        this.mentorId = mentorId;
        this.user = user;
        this.tasks = tasks;
        this.name = name;
        this.bio = bio;
        this.roleTitle = roleTitle;
        this.company = company;
        this.yearsExperience = yearsExperience;
        this.industries = industries;
        this.expertiseAreas = expertiseAreas;
        this.profilePhotoUrl = profilePhotoUrl;
        this.createdAt = createdAt;
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Mentor{" +
                "mentorId=" + mentorId +
                ", userId=" + user +
                ", name='" + name + '\'' +
                ", roleTitle='" + roleTitle + '\'' +
                ", company='" + company + '\'' +
                ", yearsExperience=" + yearsExperience +
                ", createdAt=" + createdAt +
                '}';
    }
}