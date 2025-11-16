package com.task_mentor.task_mentor.dto;

import com.task_mentor.task_mentor.entity.Task;
import java.time.LocalDateTime;

/**
 * TaskResponse - DTO for returning task data to frontend
 * Includes all task information including image metadata
 * 
 * @author James No
 */
public class TaskResponse {
    
    private Long taskId;
    private Long mentorId;
    private String title;
    private String description;
    private Integer durationMinutes;
    private String category;
    private LocalDateTime createdAt;
    private String imageUrl;
    private String imageFileName;
    private Long imageFileSize;
    
    // Constructors
    public TaskResponse() {}
    
    public TaskResponse(Long taskId, Long mentorId, String title, String description, Integer durationMinutes,
                       String category, LocalDateTime createdAt, String imageUrl, String imageFileName, Long imageFileSize) {
        this.taskId = taskId;
        this.mentorId = mentorId;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.category = category;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
        this.imageFileName = imageFileName;
        this.imageFileSize = imageFileSize;
    }
    
    /**
     * Factory method to create TaskResponse from Task entity
     */
    public static TaskResponse fromEntity(Task task) {
        TaskResponse response = new TaskResponse();
        response.setTaskId(task.getTaskId());
        response.setMentorId(task.getMentor() != null ? task.getMentor().getMentorId() : null);
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setDurationMinutes(task.getDurationMinutes());
        response.setCategory(task.getCategory());
        response.setCreatedAt(task.getCreatedAt());
        response.setImageUrl(task.getImageUrl());
        response.setImageFileName(task.getImageFileName());
        response.setImageFileSize(task.getImageFileSize());
        return response;
    }
    
    // Getters and Setters
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public Long getMentorId() {
        return mentorId;
    }
    
    public void setMentorId(Long mentorId) {
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
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getImageFileName() {
        return imageFileName;
    }
    
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
    
    public Long getImageFileSize() {
        return imageFileSize;
    }
    
    public void setImageFileSize(Long imageFileSize) {
        this.imageFileSize = imageFileSize;
    }
}
