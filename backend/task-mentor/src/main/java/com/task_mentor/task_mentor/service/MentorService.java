package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.dto.MentorStatistics;
import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.User;
import com.task_mentor.task_mentor.repository.MentorRepository;
import com.task_mentor.task_mentor.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MentorService {

    @Autowired
    private MentorRepository mentorRepository;



    @Autowired
    private UserRepository userRepository;

    private static final String DEFAULT_MENTOR_IMAGE = "https://api.dicebear.com/7.x/avataaars/svg?seed=default";

    public Mentor createMentor(Long userId, String name, String bio, String roleTitle, String company,
                               Integer yearsExperience, String industries, String expertiseAreas, String
                                       profilePhotoUrl) {

        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("User not found"));

        if(!"mentor".equalsIgnoreCase(user.getAccountType())){
            throw new IllegalArgumentException("User is not a mentor");
        }

        if(mentorRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("User is already a mentor");
        }

        validateMentorData(name, yearsExperience);

        if(profilePhotoUrl != null && !profilePhotoUrl.trim().isEmpty()) {
            validateProfilePhotoUrl(profilePhotoUrl);
        }

        Mentor mentor = new Mentor();
        mentor.setUser(user);
        mentor.setName(name);
        if(bio != null){
            mentor.setBio(bio.trim());
        }else{
            mentor.setBio(null);
        }
        if(roleTitle != null){
            mentor.setRoleTitle(roleTitle.trim());
        }else{
            mentor.setRoleTitle(null);
        }
        if(company != null){
            mentor.setCompany(company.trim());
        }else{
            mentor.setCompany(null);
        }
        mentor.setYearsExperience(yearsExperience);
        if(industries != null){
            mentor.setIndustries(industries.trim());
        }else{
            mentor.setIndustries(null);
        }
        if(expertiseAreas != null){
            mentor.setExpertiseAreas(expertiseAreas.trim());
        }else{
            mentor.setExpertiseAreas(null);
        }
        mentor.setCreatedAt(LocalDateTime.now());

        if(profilePhotoUrl != null && !profilePhotoUrl.trim().isEmpty()){
            mentor.setProfilePhotoUrl(profilePhotoUrl.trim());
        } else {
            mentor.setProfilePhotoUrl(DEFAULT_MENTOR_IMAGE);
        }

        return mentorRepository.save(mentor);
    }

    public Mentor updateMentorProfile(Long userId, String name, String bio, String roleTitle, String company,
                                      Integer yearsExperience, String industries, String expertiseAreas, String
                                              profilePhotoUrl) {
        Mentor mentor = mentorRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("Mentor not found"));

        if (name != null && !name.trim().isEmpty()) {
            validateName(name);
            mentor.setName(name);
        }

        if (bio != null) {
            if (bio.trim().isEmpty()) {
                mentor.setBio(null);
            } else {
                mentor.setBio(bio.trim());
            }
        }

        if (roleTitle != null) {
            if (roleTitle.trim().isEmpty()) {
                mentor.setRoleTitle(null);
            } else {
                mentor.setRoleTitle(roleTitle.trim());
            }
        }

        if (company != null) {
            if (company.trim().isEmpty()) {
                mentor.setCompany(null);
            } else {
                mentor.setCompany(company.trim());
            }
        }

        if (yearsExperience != null) {
            validateYearsExperience(yearsExperience);
            mentor.setYearsExperience(yearsExperience);
        }

        if (industries != null) {
            if (industries.trim().isEmpty()) {
                mentor.setIndustries(null);
            } else {
                mentor.setIndustries(industries.trim());
            }
        }

        if (expertiseAreas != null) {
            if (expertiseAreas.trim().isEmpty()) {
                mentor.setExpertiseAreas(null);
            } else {
                mentor.setExpertiseAreas(expertiseAreas.trim());
            }
        }

        if (profilePhotoUrl != null) {
            if(profilePhotoUrl.trim().isEmpty()){
                validateProfilePhotoUrl(profilePhotoUrl);
                mentor.setProfilePhotoUrl(profilePhotoUrl.trim());
                } else {
                mentor.setProfilePhotoUrl(DEFAULT_MENTOR_IMAGE);
            }
        }

        return mentorRepository.save(mentor);
    }

    public Mentor getMentorById(Long mentorId) {
        return mentorRepository.findById(mentorId).orElseThrow(()->new IllegalArgumentException("Mentor with that id " +
                " not found"));
    }

    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    public void deleteMenotorProfile(Long mentorId) {
        if(!mentorRepository.existsById(mentorId)){
            throw new IllegalArgumentException("Mentor with that id not found");
        }
        mentorRepository.deleteById(mentorId);
    }

    public boolean doesMentorExist(Long userId){
        return mentorRepository.existsById(userId);
    }

    public List<Mentor> SearchMentorsByName(String name){
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Mentor name cannot be empty");
        }
        return mentorRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Mentor> SearchMentorsByCompany(String company){
        if(company == null || company.trim().isEmpty()){
            throw new IllegalArgumentException("Company name cannot be empty");
        }
        return mentorRepository.findByNameContainingIgnoreCase(company);
    }

    public List<Mentor> SearchMentorsByExpertise(String expertiseAreas){
        if(expertiseAreas == null || expertiseAreas.trim().isEmpty()){
            throw new IllegalArgumentException("Expertise areas cannot be empty");
        }
        return mentorRepository.findByExpertise(expertiseAreas.trim());
    }

    public MentorStatistics getMentorStatistics(Long mentorId) {
        Mentor mentor = getMentorById(mentorId);

        MentorStatistics stats = new MentorStatistics();
        stats.setMentorId(mentor.getMentorId());
        stats.setName(mentor.getName());
        stats.setBio(mentor.getBio());
        stats.setRoleTitle(mentor.getRoleTitle());
        stats.setCompany(mentor.getCompany());
        stats.setYearsExperience(mentor.getYearsExperience());
        stats.setIndustries(mentor.getIndustries());
        stats.setExpertiseAreas(mentor.getExpertiseAreas());
        stats.setProfilePhotoUrl(mentor.getProfilePhotoUrl());

        return stats;
    }

    public List<MentorStatistics> getAllMentorStatistics() {
        List<Mentor> mentors = getAllMentors();

        return mentors.stream()
                .map(mentor -> {
                    MentorStatistics stats = new MentorStatistics();
                    stats.setMentorId(mentor.getMentorId());
                    stats.setName(mentor.getName());
                    stats.setBio(mentor.getBio());
                    stats.setRoleTitle(mentor.getRoleTitle());
                    stats.setCompany(mentor.getCompany());
                    stats.setYearsExperience(mentor.getYearsExperience());
                    stats.setIndustries(mentor.getIndustries());
                    stats.setExpertiseAreas(mentor.getExpertiseAreas());
                    stats.setProfilePhotoUrl(mentor.getProfilePhotoUrl());
                    return stats;
                })
                .toList();
    }

    private void validateMentorData(String name, Integer yearsExperience){
        validateName(name);
        validateYearsExperience(yearsExperience);
    }

    private void validateName(String name) {
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Mentor name cannot be empty");
        }
        if(name.trim().length()<2){
            throw new IllegalArgumentException("Mentor name cannot be less than 2 characters");
        }
        if(name.trim().length()>150){
            throw new IllegalArgumentException("Mentor name cannot be greater than 150 characters");
        }
    }

    private void validateYearsExperience(Integer yearsExperience){
        if(yearsExperience == null){
            throw new IllegalArgumentException("Years experience cannot be null");
        }
        if(yearsExperience<0){
            throw new IllegalArgumentException("Years experience cannot be less than 0");
        }
        if(yearsExperience>60){
            throw new IllegalArgumentException("Years experience seems unrealistic(no more than 60 :))");
        }
    }

    private void validateProfilePhotoUrl(String profilePhotoUrl){
        if(profilePhotoUrl == null || profilePhotoUrl.trim().isEmpty()){
            return;
        }
        if(profilePhotoUrl.length()>500){
            throw new IllegalArgumentException("Profile photo url cannot be greater than 500 characters");
        }
        if(!profilePhotoUrl.startsWith("http://") && !profilePhotoUrl.startsWith("https://")){
            throw new IllegalArgumentException("Profile photo url must be a valid HTTTP or HTTPS URL");
        }
    }



}
