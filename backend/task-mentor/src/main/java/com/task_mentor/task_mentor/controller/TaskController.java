package com.task_mentor.task_mentor.controller;

import com.task_mentor.task_mentor.dto.TaskCreateRequest;
import com.task_mentor.task_mentor.dto.TaskResponse;
import com.task_mentor.task_mentor.dto.TaskUpdateRequest;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TaskController - REST API endpoints for Task management
 * Handles CRUD operations with optional image upload support
 * 
 * @author James No
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // Allow frontend access (configure properly in production)
public class TaskController {
    
    private final TaskService taskService;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    /**
     * Create a new task with optional image
     * POST /api/tasks
     * 
     * Accepts multipart form data:
     * - task: JSON with task details (TaskCreateRequest)
     * - image: Optional image file
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TaskResponse> createTask(
            @RequestPart("task") @Valid TaskCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        
        // Convert DTO to entity
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDurationMinutes(request.getDurationMinutes());
        task.setCategory(request.getCategory());
        
        // Create task with or without image
        Task createdTask;
        if (imageFile != null && !imageFile.isEmpty()) {
            createdTask = taskService.createTaskWithImage(request.getMentorId(), task, imageFile);
        } else {
            createdTask = taskService.createTask(request.getMentorId(), task);
        }
        
        // Convert to response DTO
        TaskResponse response = TaskResponse.fromEntity(createdTask);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get a single task by ID
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + id));
        
        TaskResponse response = TaskResponse.fromEntity(task);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all tasks
     * GET /api/tasks
     * 
     * Optional query parameters:
     * - category: Filter by category
     * - mentorId: Filter by mentor
     * - minDuration: Minimum duration in minutes
     * - maxDuration: Maximum duration in minutes
     * - search: Search in title
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long mentorId,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) String search) {
        
        List<Task> tasks;
        
        // Apply filters based on query parameters
        if (search != null && !search.trim().isEmpty()) {
            tasks = taskService.searchTasksByTitle(search);
        } else if (category != null && mentorId != null) {
            tasks = taskService.getTasksByMentorAndCategory(mentorId, category);
        } else if (mentorId != null) {
            tasks = taskService.getTasksByMentorId(mentorId);
        } else if (category != null) {
            tasks = taskService.getTasksByCategory(category);
        } else if (maxDuration != null) {
            tasks = taskService.getTasksByMaxDuration(maxDuration);
        } else if (minDuration != null) {
            tasks = taskService.getTasksByMinDuration(minDuration);
        } else {
            tasks = taskService.getAllTasks();
        }
        
        // Convert to response DTOs
        List<TaskResponse> responses = tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get all tasks by a specific mentor
     * GET /api/tasks/mentor/{mentorId}
     */
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<TaskResponse>> getTasksByMentor(@PathVariable Long mentorId) {
        List<Task> tasks = taskService.getTasksByMentorId(mentorId);
        
        List<TaskResponse> responses = tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Update a task with optional new image
     * PUT /api/tasks/{id}
     * 
     * Accepts multipart form data:
     * - task: JSON with updated task details (TaskUpdateRequest)
     * - image: Optional new image file (replaces existing if provided)
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestPart("task") @Valid TaskUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        
        // Convert DTO to entity (only non-null fields will be updated)
        Task updates = new Task();
        if (request.getTitle() != null) updates.setTitle(request.getTitle());
        if (request.getDescription() != null) updates.setDescription(request.getDescription());
        if (request.getDurationMinutes() != null) updates.setDurationMinutes(request.getDurationMinutes());
        if (request.getCategory() != null) updates.setCategory(request.getCategory());
        
        // Update task with or without image
        Task updatedTask;
        if (imageFile != null && !imageFile.isEmpty()) {
            updatedTask = taskService.updateTaskWithImage(id, updates, imageFile);
        } else {
            updatedTask = taskService.updateTask(id, updates);
        }
        
        // Convert to response DTO
        TaskResponse response = TaskResponse.fromEntity(updatedTask);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete a task
     * DELETE /api/tasks/{id}
     * 
     * Also deletes associated image file if it exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
