package com.task_mentor.task_mentor.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public class BookingRequest {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "Proposed datetime is required")
    @Future(message = "Proposed date must be in the future")
    private LocalDateTime proposedDatetime;


    public BookingRequest() {}

    public BookingRequest(Long studentId, Long mentorId, Long taskId, LocalDateTime proposedDatetime) {
        this.studentId = studentId;
        this.mentorId = mentorId;
        this.taskId = taskId;
        this.proposedDatetime = proposedDatetime;
    }


    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getProposedDatetime() {
        return proposedDatetime;
    }

    public void setProposedDatetime(LocalDateTime proposedDatetime) {
        this.proposedDatetime = proposedDatetime;
    }

    @Override
    public String toString() {
        return "BookingRequest{" +
                "studentId=" + studentId +
                ", mentorId=" + mentorId +
                ", taskId=" + taskId +
                ", proposedDatetime=" + proposedDatetime +
                '}';
    }
}