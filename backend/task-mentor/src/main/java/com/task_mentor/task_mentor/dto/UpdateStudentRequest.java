package com.task_mentor.task_mentor.dto;

/**
 * Request DTO for updating an existing student profile
 * Used in PUT /api/students/me endpoint
 * All fields are optional (only send what you want to update)
 *
 * @author Tyson Ringelstetter
 */

public class UpdateStudentRequest {
    private String name;
    private String bio;
    private String major;
    private Integer graduationYear;
    private String careerInterests;
    private String profilePhotoUrl;

    // Default constructor
    public UpdateStudentRequest() {}

    // Parameterized constructor
    public UpdateStudentRequest(String name, String bio, String major,
                                Integer graduationYear, String careerInterests,
                                String profilePhotoUrl) {
        this.name = name;
        this.bio = bio;
        this.major = major;
        this.graduationYear = graduationYear;
        this.careerInterests = careerInterests;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    // Getters & Setters
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

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getCareerInterests() {
        return careerInterests;
    }

    public void setCareerInterests(String careerInterests) {
        this.careerInterests = careerInterests;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
}
