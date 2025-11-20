package com.task_mentor.task_mentor.controller;


import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Student;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:5173")
public class SearchController {

    private SearchService searchService;

    @GetMapping("/mentors")
    public ResponseEntity<Map<String,Object>> searchMentors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String expertise,
            @RequestParam(required = false) Integer minYearsExperience) {

            List<Mentor> mentors = searchService.searchMentors(
                name, company, industry, expertise, minYearsExperience);

            Map<String, Object> response = new HashMap<>();
            response.put("mentors", mentors);
            response.put("count", mentors.size());
            response.put("filters", buildMentorsFiltersMap(name,company,industry,expertise, minYearsExperience));


        return ResponseEntity.ok(response);

    }

    @GetMapping("/tasks")
    public ResponseEntity<Map<String, Object>> searchTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long mentorId,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration){
        List<Task> tasks = searchService.searchTasks(
                title,category,mentorId,minDuration,maxDuration);

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", tasks);
        response.put("count", tasks.size());
        response.put("filters", buildTaskFiltersMap(title,category,mentorId,minDuration,maxDuration));

        return ResponseEntity.ok(response);

    }

    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> searchStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String careerInterests,
            @RequestParam(required = false) Integer graduationYear,
            @RequestParam(required = false) Integer minGraduationYear){

        List<Student> students = searchService.searchStudents(
                name,bio,major, careerInterests, graduationYear, minGraduationYear);

        Map<String, Object> Response = new HashMap<>();
        Response.put("students", students);
        Response.put("count", students.size());
        Response.put("filters", buildStudentsFiltersMap(name, bio, major, careerInterests, graduationYear,
                minGraduationYear));

        return ResponseEntity.ok(Response);

    }

    @GetMapping("/mentors-with-tasks")
    public ResponseEntity<Map<String, Object>> searchMentorsWithTasks(
            @RequestParam(required = false) String mentorName,
            @RequestParam(required = false ) String expertise,
            @RequestParam(required = false) String taskCategory,
            @RequestParam(required = false) Integer maxDuration
    ){
        List<Mentor> mentors = searchService.mentorsWithTasks(
                mentorName,expertise,taskCategory, maxDuration
        );

        Map<String, Object> Response = new HashMap<>();
        Response.put("mentors", mentors);
        Response.put("count", mentors.size());
        Response.put("filters", buildMentorsWIthTasksFilterMap(mentorName, expertise, taskCategory, maxDuration));

        return ResponseEntity.ok(Response);

    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories(){
        List<Task> tasks = searchService.getAllTaskCategories();

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

    private Map<String, Object> buildTaskFiltersMap(String title, String category, Long mentorId, String minDuration,
                                                    String maxDuration){
        Map<String, Object> filters = new HashMap<>();
        if(title != null) filters.put("title", title);
        if(category != null) filters.put("category", category);
        if(mentorId != null) filters.put("mentorId", mentorId);
        if(minDuration != null) filters.put("minDuration", minDuration);
        if(maxDuration != null) filters.put("maxDuration", maxDuration);

        return filters;
    }

    private Map<String, Object> buildStudentsFiltersMap(String name, String major, String careerInterests,
                                                        Integer graduationYear, Integer minGraduationYear){
        Map<String, Object> filters = new HashMap<>();
        if(name != null) filters.put("name", name);
        if(major != null) filters.put("major", major);
        if(careerInterests != null) filters.put("careerInterests", careerInterests);
        if(graduationYear != null) filters.put("graduationYear", graduationYear);
        if(minGraduationYear != null) filters.put("minGraduationYear", minGraduationYear);

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





}
