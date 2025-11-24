package com.task_mentor.task_mentor.dto;

import com.task_mentor.task_mentor.entity.Booking;
import java.time.LocalDateTime;


public class BookingResponse {
    private Long bookingId;
    private Long studentId;
    private String studentName;
    private Long mentorId;
    private String mentorName;
    private Long taskId;
    private String taskTitle;
    private Integer taskDurationMinutes;
    private LocalDateTime proposedDatetime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public BookingResponse() {}


    public static BookingResponse fromEntity(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setStudentId(booking.getStudent().getStudentId());
        response.setStudentName(booking.getStudent().getName());
        response.setMentorId(booking.getMentor().getMentorId());
        response.setMentorName(booking.getMentor().getName());
        response.setTaskId(booking.getTask().getTaskId());
        response.setTaskTitle(booking.getTask().getTitle());
        response.setTaskDurationMinutes(booking.getTask().getDurationMinutes());
        response.setProposedDatetime(booking.getProposedDatetime());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        return response;
    }


    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public Integer getTaskDurationMinutes() {
        return taskDurationMinutes;
    }

    public void setTaskDurationMinutes(Integer taskDurationMinutes) {
        this.taskDurationMinutes = taskDurationMinutes;
    }

    public LocalDateTime getProposedDatetime() {
        return proposedDatetime;
    }

    public void setProposedDatetime(LocalDateTime proposedDatetime) {
        this.proposedDatetime = proposedDatetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "BookingResponse{" +
                "bookingId=" + bookingId +
                ", studentName='" + studentName + '\'' +
                ", mentorName='" + mentorName + '\'' +
                ", taskTitle='" + taskTitle + '\'' +
                ", proposedDatetime=" + proposedDatetime +
                ", status='" + status + '\'' +
                '}';
    }
}