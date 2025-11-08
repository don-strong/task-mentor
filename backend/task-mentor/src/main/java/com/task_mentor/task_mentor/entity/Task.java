package com.task_mentor.task_mentor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Task Entity - Stores tasks/services offered by mentors
 * Corresponds to the 'tasks' table in PostgreSQL
 *
 * @author James No
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id",nullable = false)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentorId;


    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Task() {

    }

    public Task(Long taskId, Mentor mentorId, String title, String description, Integer durationMinutes, String category, LocalDateTime createdAt) {
        this.taskId = taskId;
        this.mentorId = mentorId;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.category = category;
        this.createdAt = createdAt;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Mentor getMentorId() {
        return mentorId;
    }

    public void setMentor(Mentor mentorId) {
        this.mentorId = mentorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        return "Task{" +
                "taskId=" + taskId +
                ", mentorId=" + mentorId +
                ", title='" + title + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}