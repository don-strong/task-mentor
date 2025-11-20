package com.task_mentor.task_mentor.dto;

public class TaskSearchDTO {

    private Long taskId;
    private Long mentorId;
    private String mentorName;
    private String title;
    private String description;
    private Integer durationMinutes;
    private String category;


    public TaskSearchDTO() {}

    public TaskSearchDTO(Long taskId, Long mentorId, String mentorName, String title,
                         String description, Integer durationMinutes, String category) {
        this.taskId = taskId;
        this.mentorId = mentorId;
        this.mentorName = mentorName;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.category = category;
    }


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

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
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