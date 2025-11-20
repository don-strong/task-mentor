package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.dto.TaskStatistics;
import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.repository.MentorRepository;
import com.task_mentor.task_mentor.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * TaskService - Business logic for Task management
 * Handles CRUD operations, validation, and business rules for mentor tasks
 * 
 * @author James No
 */
@Service
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final MentorRepository mentorRepository;
    private final FileStorageService fileStorageService;
    
    public TaskService(TaskRepository taskRepository, MentorRepository mentorRepository, FileStorageService fileStorageService) {
        this.taskRepository = taskRepository;
        this.mentorRepository = mentorRepository;
        this.fileStorageService = fileStorageService;
    }
    
    /**
     * Create a new task
     * Validates that the mentor exists and task data is valid
     */
    public Task createTask(Long mentorId, Task task) {
        // Find mentor
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found with ID: " + mentorId));
        
        // Set mentor relationship
        task.setMentor(mentor);
        
        // Validation: Required fields and business rules
        validateTask(task);
        
        return taskRepository.save(task);
    }
    
    /**
     * Create a new task with image attachment
     * Handles file upload and stores file metadata
     */
    public Task createTaskWithImage(Long mentorId, Task task, MultipartFile imageFile) {
        // Find mentor
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found with ID: " + mentorId));
        
        // Set mentor relationship
        task.setMentor(mentor);
        
        // Validation: Required fields and business rules
        validateTask(task);
        
        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String storedFileName = fileStorageService.storeFile(imageFile);
            task.setImageFileName(storedFileName);
            task.setImageUrl("/api/files/task-images/" + storedFileName);
            task.setImageFileSize(imageFile.getSize());
        }
        
        return taskRepository.save(task);
    }
    
    /**
     * Get task by task ID
     */
    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }
    
    /**
     * Get all tasks
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    /**
     * Get all tasks offered by a specific mentor
     * Used to display a mentor's "task menu"
     */
    public List<Task> getTasksByMentorId(Long mentorId) {
        return taskRepository.findByMentorId(mentorId);
    }
    
    /**
     * Update task
     */
    public Task updateTask(Long taskId, Task updatedTask) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Validation: Required fields and business rules
        validateTask(updatedTask);
        
        // Update fields
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setDurationMinutes(updatedTask.getDurationMinutes());
        existing.setCategory(updatedTask.getCategory());
        
        return taskRepository.save(existing);
    }
    
    /**
     * Update task with optional new image
     * If new image provided, deletes old image and stores new one
     */
    public Task updateTaskWithImage(Long taskId, Task updatedTask, MultipartFile newImageFile) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Validation: Required fields and business rules
        validateTask(updatedTask);
        
        // Update fields
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setDurationMinutes(updatedTask.getDurationMinutes());
        existing.setCategory(updatedTask.getCategory());
        
        // Handle image update if new file provided
        if (newImageFile != null && !newImageFile.isEmpty()) {
            // Delete old image if exists
            if (existing.getImageFileName() != null) {
                fileStorageService.deleteFile(existing.getImageFileName());
            }
            
            // Store new image
            String storedFileName = fileStorageService.storeFile(newImageFile);
            existing.setImageFileName(storedFileName);
            existing.setImageUrl("/api/files/task-images/" + storedFileName);
            existing.setImageFileSize(newImageFile.getSize());
        }
        
        return taskRepository.save(existing);
    }
    
    /**
     * Delete task
     * Also deletes associated image file if exists
     */
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Delete associated image file if exists
        if (task.getImageFileName() != null) {
            fileStorageService.deleteFile(task.getImageFileName());
        }
        
        taskRepository.deleteById(taskId);
    }
    
    /**
     * Search tasks by category
     */
    public List<Task> getTasksByCategory(String category) {
        return taskRepository.findByCategory(category);
    }
    
    /**
     * Search tasks by title (partial match, case-insensitive)
     */
    public List<Task> searchTasksByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Get tasks by duration range (max duration)
     * Useful for students looking for quick sessions
     */
    public List<Task> getTasksByMaxDuration(Integer maxDuration) {
        return taskRepository.findByDurationMinutesLessThanEqual(maxDuration);
    }
    
    /**
     * Get tasks by duration range (min duration)
     * Useful for students looking for longer sessions
     */
    public List<Task> getTasksByMinDuration(Integer minDuration) {
        return taskRepository.findByDurationMinutesGreaterThanEqual(minDuration);
    }
    
    /**
     * Get tasks by mentor and category
     */
    public List<Task> getTasksByMentorAndCategory(Long mentorId, String category) {
        return taskRepository.findByMentorIdAndCategory(mentorId, category);
    }
    
    /**
     * Count tasks for a mentor
     * Used for mentor profile statistics
     */
    public long countTasksByMentor(Long mentorId) {
        return taskRepository.countByMentorId(mentorId);
    }
    
    /**
     * Get task statistics for a single task
     * Returns TaskStatistics DTO with task details
     */
    public TaskStatistics getTaskStatistics(Long taskId) {
        Task task = getTaskById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        TaskStatistics stats = new TaskStatistics();
        stats.setTaskId(task.getTaskId());
        stats.setMentorId(task.getMentor().getMentorId());
        stats.setMentorName(task.getMentor().getName());
        stats.setTitle(task.getTitle());
        stats.setDescription(task.getDescription());
        stats.setDurationMinutes(task.getDurationMinutes());
        stats.setCategory(task.getCategory());
        
        return stats;
    }
    
    /**
     * Get statistics for all tasks
     * Used for analytics and reporting
     */
    public List<TaskStatistics> getAllTaskStatistics() {
        List<Task> tasks = getAllTasks();
        
        return tasks.stream()
                .map(task -> {
                    TaskStatistics stats = new TaskStatistics();
                    stats.setTaskId(task.getTaskId());
                    stats.setMentorId(task.getMentor().getMentorId());
                    stats.setMentorName(task.getMentor().getName());
                    stats.setTitle(task.getTitle());
                    stats.setDescription(task.getDescription());
                    stats.setDurationMinutes(task.getDurationMinutes());
                    stats.setCategory(task.getCategory());
                    return stats;
                })
                .toList();
    }
    
    /**
     * Get statistics for all tasks by a specific mentor
     * Used for mentor dashboard and analytics
     */
    public List<TaskStatistics> getTaskStatisticsByMentor(Long mentorId) {
        List<Task> tasks = getTasksByMentorId(mentorId);
        
        return tasks.stream()
                .map(task -> {
                    TaskStatistics stats = new TaskStatistics();
                    stats.setTaskId(task.getTaskId());
                    stats.setMentorId(task.getMentor().getMentorId());
                    stats.setMentorName(task.getMentor().getName());
                    stats.setTitle(task.getTitle());
                    stats.setDescription(task.getDescription());
                    stats.setDurationMinutes(task.getDurationMinutes());
                    stats.setCategory(task.getCategory());
                    return stats;
                })
                .toList();
    }
    
    /**
     * Check if a task exists
     * Helper method for validation
     */
    public boolean doesTaskExist(Long taskId) {
        return taskRepository.existsById(taskId);
    }
    
    /**
     * Validate task data
     * Business rules for tasks
     */
    private void validateTask(Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Task description is required");
        }
        
        if (task.getDurationMinutes() == null || task.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Task duration must be greater than 0");
        }
        
        // Business rule: Task duration should be reasonable (between 15 min and 8 hours)
        if (task.getDurationMinutes() < 15 || task.getDurationMinutes() > 480) {
            throw new IllegalArgumentException("Task duration must be between 15 minutes and 8 hours");
        }
        
        if (task.getMentor() == null) {
            throw new IllegalArgumentException("Mentor is required");
        }
    }
}
