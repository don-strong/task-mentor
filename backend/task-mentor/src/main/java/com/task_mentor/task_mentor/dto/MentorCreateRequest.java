package com.task_mentor.task_mentor.dto;

public class MentorCreateRequest {
    private Long userId;
    private String name;
    private String bio;
    private String roleTitle;
    private String company;
    private Integer yearsExperience;
    private String industries;
    private String expertiseAreas;
    private String profilePhotoUrl;


    public MentorCreateRequest() {}


    public MentorCreateRequest(Long userId, String name, String bio, String roleTitle,
                               String company, Integer yearsExperience, String industries,
                               String expertiseAreas, String profilePhotoUrl) {
        this.userId = userId;
        this.name = name;
        this.bio = bio;
        this.roleTitle = roleTitle;
        this.company = company;
        this.yearsExperience = yearsExperience;
        this.industries = industries;
        this.expertiseAreas = expertiseAreas;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public String getIndustries() {
        return industries;
    }

    public void setIndustries(String industries) {
        this.industries = industries;
    }

    public String getExpertiseAreas() {
        return expertiseAreas;
    }

    public void setExpertiseAreas(String expertiseAreas) {
        this.expertiseAreas = expertiseAreas;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
}