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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TaskController - REST API endpoints for Task management
 * Handles CRUD operations with optional image upload support
 *
 * @author James No
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // Configure properly in production
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createTask(
            @RequestPart("task") @Valid TaskCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        try {
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

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error creating task: " + e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            TaskResponse response = TaskResponse.fromEntity(task);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllTasks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long mentorId,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) String search) {

        try {
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

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }


    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<?> getTasksByMentor(@PathVariable Long mentorId) {
        try {
            List<Task> tasks = taskService.getTasksByMentorId(mentorId);

            List<TaskResponse> responses = tasks.stream()
                    .map(TaskResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping(path = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTaskJson(@RequestBody @Valid TaskCreateRequest request) {
        try {
            Task task = new Task();
            task.setTitle(request.getTitle());
            task.setDescription(request.getDescription());
            task.setDurationMinutes(request.getDurationMinutes());
            task.setCategory(request.getCategory());

            Task createdTask = taskService.createTask(request.getMentorId(), task);
            TaskResponse response = TaskResponse.fromEntity(createdTask);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error creating task: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @RequestPart("task") @Valid TaskUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        try {
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

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error updating task: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long id,
            @RequestParam Long mentorId) {

        try {
            taskService.deleteTask(id, mentorId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            // Permission error or has confirmed bookings
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error deleting task: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<?> deleteTaskImage(@PathVariable Long id) {
        try {
            taskService.deleteTaskImage(id);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error deleting task image: " + e.getMessage()));
        }
    }


    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}