package com.task_mentor.task_mentor.dto;

import java.time.LocalDateTime;


public class BookingRequest {
    private Long studentId;
    private Long mentorId;
    private Long taskId;
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