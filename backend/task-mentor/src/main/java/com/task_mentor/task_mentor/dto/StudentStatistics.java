package com.task_mentor.task_mentor.dto;

public class StudentStatistics {
    private Long studentId;
    private String name;
    private String bio;
    private String major;
    private Integer graduationYear;
    private String careerInterests;
    private String profilePhotoUrl;


    public StudentStatistics() {}

    public StudentStatistics(Long studentId, String name, String bio, String major,
                             Integer graduationYear, String careerInterests, String profilePhotoUrl) {
        this.studentId = studentId;
        this.name = name;
        this.bio = bio;
        this.major = major;
        this.graduationYear = graduationYear;
        this.careerInterests = careerInterests;
        this.profilePhotoUrl = profilePhotoUrl;
    }


    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

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

    @Override
    public String toString() {
        return "StudentStatistics{" +
                "studentId=" + studentId +
                ", name='" + name + '\'' +
                ", major='" + major + '\'' +
                ", graduationYear=" + graduationYear +
                '}';
    }
}