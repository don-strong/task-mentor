package com.task_mentor.task_mentor.controller;


import com.task_mentor.task_mentor.dto.MentorSearchDTO;
import com.task_mentor.task_mentor.dto.TaskSearchDTO;
import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:5173")
public class SearchController {

    private SearchService searchService;

    @GetMapping("/mentors")
    public ResponseEntity<Map<String, Object>> searchMentors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String expertise,
            @RequestParam(required = false) Integer minYearsExperience) {

        List<Mentor> mentors = searchService.searchMentors(
                name, company, industry, expertise, minYearsExperience);

        // Convert entities to DTOs to avoid circular references and control exposed data
        List<MentorSearchDTO> mentorDTOs = mentors.stream()
                .map(this::convertMentorToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("mentors", mentorDTOs);
        response.put("count", mentorDTOs.size());
        response.put("filters", buildMentorsFiltersMap(name, company, industry, expertise, minYearsExperience));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks")
    public ResponseEntity<Map<String, Object>> searchTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long mentorId,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration) {

        List<Task> tasks = searchService.searchTasks(
                title, category, mentorId, minDuration, maxDuration);

        // Convert entities to DTOs
        List<TaskSearchDTO> taskDTOs = tasks.stream()
                .map(this::convertTaskToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", taskDTOs);
        response.put("count", taskDTOs.size());
        response.put("filters", buildTaskFiltersMap(title, category, mentorId, minDuration, maxDuration));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> searchStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Integer graduationYear,
            @RequestParam(required = false) Integer minGraduationYear,
            @RequestParam(required = false) String careerInterests){

        List<Student> students = searchService.searchStudents(
                name,major, graduationYear, minGraduationYear, careerInterests);

        Map<String, Object> Response = new HashMap<>();
        Response.put("students", students);
        Response.put("count", students.size());
        Response.put("filters", buildStudentFiltersMap(name,  major,  graduationYear,
                minGraduationYear, careerInterests));

        return ResponseEntity.ok(Response);

    }

    @GetMapping("/mentors-with-tasks")
    public ResponseEntity<Map<String, Object>> searchMentorsWithTasks(
            @RequestParam(required = false) String mentorName,
            @RequestParam(required = false) String expertise,
            @RequestParam(required = false) String taskCategory,
            @RequestParam(required = false) Integer maxDuration) {

        List<Mentor> mentors = searchService.searchMentorsWithTasks(
                mentorName, expertise, taskCategory, maxDuration);

        // Convert entities to DTOs
        List<MentorSearchDTO> mentorDTOs = mentors.stream()
                .map(this::convertMentorToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("mentors", mentorDTOs);
        response.put("count", mentorDTOs.size());
        response.put("filters", buildMentorsWIthTasksFilterMap(mentorName, expertise, taskCategory, maxDuration));

        return ResponseEntity.ok(response);
    }
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories(){
        List<String> tasks = searchService.getAllCategories();

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", tasks);
        response.put("count", tasks.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/companies")
    public ResponseEntity<Map<String, Object>> getAllCompanies(){
        List<String> companies = searchService.getAllCompanies();

        Map<String, Object> response = new HashMap<>();
        response.put("companies", companies);
        response.put("count", companies.size());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/majors")
    public ResponseEntity<Map<String, Object>> getAllMajors(){
        List<String> majors = searchService.getAllMajors();

        Map<String, Object> response = new HashMap<>();
        response.put("majors", majors);
        response.put("count", majors.size());

        return ResponseEntity.ok(response);
    }


    @GetMapping("filter-options")
    public ResponseEntity<Map<String, Object>> getFilterOptions(){
        Map<String, Object> response = new HashMap<>();

        response.put("categories", searchService.getAllCategories());
        response.put("companies", searchService.getAllCompanies());
        response.put("majors", searchService.getAllMajors());

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> buildMentorsFiltersMap(String name, String company, String industry,
                                                       String expertise, Integer minYearsExperience){
        Map<String, Object> filters = new HashMap<>();
        if(name != null) filters.put("name", name);
        if(company != null) filters.put("company", company);
        if(industry != null) filters.put("industry", industry);
        if(expertise != null) filters.put("expertise", expertise);
        if(minYearsExperience != null) filters.put("minYearsExperience", minYearsExperience);

        return filters;


    }

    private Map<String, Object> buildTaskFiltersMap(String title, String category, Long mentorId, Integer minDuration,
                                                    Integer maxDuration){
        Map<String, Object> filters = new HashMap<>();
        if(title != null) filters.put("title", title);
        if(category != null) filters.put("category", category);
        if(mentorId != null) filters.put("mentorId", mentorId);
        if(minDuration != null) filters.put("minDuration", minDuration);
        if(maxDuration != null) filters.put("maxDuration", maxDuration);

        return filters;
    }

    private Map<String, Object> buildStudentFiltersMap(String name, String major, Integer graduationYear,
                                                       Integer minGraduationYear, String careerInterest) {
        Map<String, Object> filters = new HashMap<>();
        if (name != null) filters.put("name", name);
        if (major != null) filters.put("major", major);
        if (graduationYear != null) filters.put("graduationYear", graduationYear);
        if (minGraduationYear != null) filters.put("minGraduationYear", minGraduationYear);
        if (careerInterest != null) filters.put("careerInterest", careerInterest);
        return filters;
    }

    private Map<String, Object> buildMentorsWIthTasksFilterMap(String mentorName, String expertise,
                                                               String taskCategory, Integer maxDuration){
        Map<String, Object> filters = new HashMap<>();
        if(mentorName != null) filters.put("mentorName", mentorName);
        if(expertise != null) filters.put("expertise", expertise);
        if(taskCategory != null) filters.put("taskCategory", taskCategory);
        if(maxDuration != null) filters.put("maxDuration", maxDuration);
        return filters;
    }

    private MentorSearchDTO convertMentorToDTO(Mentor mentor) {
        MentorSearchDTO dto = new MentorSearchDTO();
        dto.setMentorId(mentor.getMentorId());
        dto.setName(mentor.getName());
        dto.setBio(mentor.getBio());
        dto.setRoleTitle(mentor.getRoleTitle());
        dto.setCompany(mentor.getCompany());
        dto.setYearsExperience(mentor.getYearsExperience());
        dto.setIndustries(mentor.getIndustries());
        dto.setExpertiseAreas(mentor.getExpertiseAreas());
        dto.setProfilePhotoUrl(mentor.getProfilePhotoUrl());

        // Optionally include task count
        if (mentor.getTasks() != null) {
            dto.setTaskCount(mentor.getTasks().size());
        }

        return dto;
    }

    private TaskSearchDTO convertTaskToDTO(Task task) {
        TaskSearchDTO dto = new TaskSearchDTO();
        dto.setTaskId(task.getTaskId());

        // Safely get mentor info
        if (task.getMentor() != null) {
            dto.setMentorId(task.getMentor().getMentorId());
            dto.setMentorName(task.getMentor().getName());
        }

        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDurationMinutes(task.getDurationMinutes());
        dto.setCategory(task.getCategory());

        return dto;
    }








}
