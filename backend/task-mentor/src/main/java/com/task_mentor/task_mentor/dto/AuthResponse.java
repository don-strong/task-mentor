package com.task_mentor.task_mentor.dto;


public class AuthResponse {

    private Long userId;
    private String email;
    private String accountType;
    private String message;

    public AuthResponse() {}

    public AuthResponse(Long userId, String email, String accountType, String message) {
        this.userId = userId;
        this.email = email;
        this.accountType = accountType;
        this.message = message;
    }

    public static AuthResponse fromUser(com.task_mentor.task_mentor.entity.User user, String message) {
        return new AuthResponse(
                user.getUserId(),
                user.getEmail(),
                user.getAccountType(),
                message
        );
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", accountType='" + accountType + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}