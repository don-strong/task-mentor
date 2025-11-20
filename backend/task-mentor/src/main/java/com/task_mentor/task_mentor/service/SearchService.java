package com.task_mentor.task_mentor.service;


import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.repository.MentorRepository;
import com.task_mentor.task_mentor.repository.StudentRepository;
import com.task_mentor.task_mentor.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SearchService {

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<Mentor> searchMentors(String name, String company, String industry, String expertise,
                                     Integer minYearsExperience){

        List<Mentor> results;

        results = mentorRepository.findAll();

        if(name != null && !name.trim().isEmpty()){
            String nameLower = name.toLowerCase();
            results = results.stream()
                    .filter(m -> m.getName().toLowerCase().contains(nameLower))
                    .collect(Collectors.toList());
        }

        if(company != null && !company.trim().isEmpty()){
            String companyLower = company.toLowerCase();
            results = results.stream()
                    .filter(m -> m.getCompany().toLowerCase().contains(companyLower))
                    .collect(Collectors.toList());
        }

        if(industry != null && !industry.trim().isEmpty()){
            String industryLower = industry.toLowerCase();
            results = results.stream()
                    .filter(m -> m.getIndustries().toLowerCase().contains(industryLower))
                    .collect(Collectors.toList());

        }

        if(expertise != null && !expertise.trim().isEmpty()){
            String expertiseLower = expertise.toLowerCase();
            results = results.stream()
                    .filter(m -> m.getExpertiseAreas().toLowerCase().contains(expertiseLower))
                    .collect(Collectors.toList());
        }

        if(minYearsExperience != null){
            results = results.stream()
                    .filter(m -> m.getYearsExperience() != null &&
                            m.getYearsExperience() > minYearsExperience)
                    .collect(Collectors.toList());
        }

        return results;

    }

    public List<Task>  searchTasks( String title, String category, Long mentorId,
                                    Integer minDuration, Integer maxDuration){
        List<Task> results;

        if(mentorId != null){
            results = taskRepository.findByMentorId(mentorId);
        }else {
            results = taskRepository.findAll();
        }

        if(title != null && !title.trim().isEmpty()){
            String titleLower = title.toLowerCase();
            results = results.stream()
                    .filter(t -> t.getTitle().toLowerCase().contains(titleLower))
                    .collect(Collectors.toList());
        }

        if(category != null && !category.trim().isEmpty()){
            String categoryLower = category.toLowerCase();
            results = results.stream()
                    .filter(t -> t.getCategory().toLowerCase().contains(categoryLower))
                    .collect(Collectors.toList());
        }

        if(minDuration != null){
            results = results.stream()
                    .filter(t -> t.getDurationMinutes() >= minDuration)
                    .collect(Collectors.toList());
        }

        if(maxDuration != null){
            results = results.stream()
                    .filter(t -> t.getDurationMinutes() <= maxDuration)
                    .collect(Collectors.toList());
        }

        return results;
    }

    public List<Student> searchStudents(String name, String major, Integer graduationYear,
                                        Integer minGraduationYear, String careerInterests){
        List<Student> results;
        results = studentRepository.findAll();

        if(name != null && !name.trim().isEmpty()){
            String nameLower = name.toLowerCase();
            results = results.stream()
                    .filter(s -> s.getName().toLowerCase().contains(nameLower))
                    .collect(Collectors.toList());
        }

        if(major != null && !major.trim().isEmpty()){
            String majorLower = major.toLowerCase();
            results = results.stream()
                    .filter(s -> s.getMajor().toLowerCase().contains(majorLower))
                    .collect(Collectors.toList());
        }

        if(careerInterests != null && !careerInterests.trim().isEmpty()){
            String careerInterestsLower = careerInterests.toLowerCase();
            results = results.stream()
                    .filter(s -> s.getCareerInterests().toLowerCase().contains(careerInterestsLower))
                    .collect(Collectors.toList());
        }

        if(graduationYear != null){
            results = results.stream()
                    .filter(s -> s.getGraduationYear() != null &&
                            s.getGraduationYear().equals(graduationYear))
                    .collect(Collectors.toList());
        }

        if(minGraduationYear != null){
            results = results.stream()
                    .filter(s -> s.getGraduationYear() != null && s.getGraduationYear() >= minGraduationYear)
                    .collect(Collectors.toList());
        }

        return results;

    }

    public List<String> getAllCategories(){
        return taskRepository.findAll().stream()
                .map(Task::getCategory)
                .filter(category -> category != null && !category.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAllCompanies(){
        return mentorRepository.findAll().stream()
                .map(Mentor::getCompany)
                .filter(company -> company != null && !company.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAllMajors(){
        return studentRepository.findAll().stream()
                .map(Student::getMajor)
                .filter(major -> major != null && !major.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Mentor> searchMentorsWithTasks(String mentorName, String expertise, String taskCategory,
                                               Integer maxDuration){
        List<Mentor> mentors = searchMentors(mentorName, null, null, expertise, null);

        if(taskCategory != null || maxDuration != null){
            return mentors.stream()
                    .filter(mentor -> {
                        List<Task> tasks = taskRepository.findByMentorId(mentor.getMentorId());
                        return tasks.stream().anyMatch(task -> {
                            boolean categoryMatch = taskCategory == null ||
                                    (task.getCategory() != null &&
                                            task.getCategory().equalsIgnoreCase(taskCategory));
                            boolean durationMatch = maxDuration == null ||
                                    task.getDurationMinutes() <=  maxDuration;
                            return categoryMatch && durationMatch;
                        });
                    })
                    .collect(Collectors.toList());
        }

        return mentors;
    }

}
