package com.task_mentor.task_mentor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User Entity - Stores basic authentication information for all users
 * Corresponds to the 'users' table in PostgreSQL
 * 
 * @author James No
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username", nullable = false, length = 100)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password; // bcrypt hashed password
    
    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType; // 'student' or 'mentor'
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "user")
    private List<Student> students;
    
    @OneToMany(mappedBy = "user")
    private List<Mentor> mentors;
    
    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String username, String email, String password, String accountType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<Student> getStudents() {
        return students;
    }
    
    public void setStudents(List<Student> students) {
        this.students = students;
    }
    
    public List<Mentor> getMentors() {
        return mentors;
    }
    
    public void setMentors(List<Mentor> mentors) {
        this.mentors = mentors;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", accountType='" + accountType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
