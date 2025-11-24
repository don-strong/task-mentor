package com.task_mentor.task_mentor.dto;

import jakarta.validation.constraints.*;

/**
 * TaskCreateRequest - DTO for creating new tasks
 * Used with multipart form data for file uploads
 * 
 * @author James No
 */
public class TaskCreateRequest {
    
    @NotNull(message = "Mentor ID is required")
    private Long mentorId;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 480, message = "Duration must be less than 8 hours (480 minutes)")
    private Integer durationMinutes;
    
    @Size(max = 100, message = "Category must be less than 100 characters")
    private String category;
    
    // Image file is handled separately as MultipartFile in controller
    
    // Constructors
    public TaskCreateRequest() {}
    
    public TaskCreateRequest(Long mentorId, String title, String description, Integer durationMinutes, String category) {
        this.mentorId = mentorId;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.category = category;
    }
    
    // Getters and Setters
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
}
