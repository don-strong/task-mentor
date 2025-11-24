package com.task_mentor.task_mentor.service;

import com.task_mentor.task_mentor.entity.Mentor;
import com.task_mentor.task_mentor.entity.Task;
import com.task_mentor.task_mentor.repository.MentorRepository;
import com.task_mentor.task_mentor.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.task_mentor.task_mentor.dto.TaskSearchDTO;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private FileStorageService fileStorageService;


    public static final String CATEGORY_RESUME_REVIEW = "Resume Review";
    public static final String CATEGORY_INTERVIEW_PREP = "Interview Prep";
    public static final String CATEGORY_CAREER_ADVICE = "Career Advice";
    public static final String CATEGORY_TECHNICAL_MENTORING = "Technical Mentoring";
    public static final String CATEGORY_NETWORKING = "Networking";
    public static final String CATEGORY_PROJECT_REVIEW = "Project Review";
    public static final String CATEGORY_PROGRAMMING = "Programming";
    public static final String CATEGORY_OTHER = "Other";


    public static final int MIN_DURATION = 15;
    public static final int MAX_DURATION = 180;



    public Task createTask(Long mentorId, String title, String description,
                           Integer durationMinutes, String category) {

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found with ID: " + mentorId));


        validateTaskData(title, description, durationMinutes, category);


        Task task = new Task();
        task.setMentor(mentor);
        task.setTitle(title.trim());
        task.setDescription(description.trim());
        task.setDurationMinutes(durationMinutes);
        task.setCategory(category.trim());
        task.setCreatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    // Image handling methods for Task images (added for PR #17)
    public void setTaskImage(Long taskId, org.springframework.web.multipart.MultipartFile imageFile) {
        Task task = getTaskById(taskId);
        
        // Delete old image if exists
        if (task.getImageFileName() != null) {
            fileStorageService.deleteFile(task.getImageFileName());
        }
        
        // Store new image
        String fileName = fileStorageService.storeFile(imageFile);
        task.setImageFileName(fileName);
        task.setImageUrl("/api/files/task-images/" + fileName);
        task.setImageFileSize(imageFile.getSize());
        
        taskRepository.save(task);
    }

    public void deleteTaskImage(Long taskId) {
        Task task = getTaskById(taskId);
        
        if (task.getImageFileName() != null) {
            fileStorageService.deleteFile(task.getImageFileName());
            task.setImageFileName(null);
            task.setImageUrl(null);
            task.setImageFileSize(null);
            taskRepository.save(task);
        }
    }


    public Task updateTask(Long taskId, Long mentorId, String title, String description,
                           Integer durationMinutes, String category) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));


        if (!task.getMentor().getMentorId().equals(mentorId)) {
            throw new IllegalStateException("Mentor does not have permission to update this task");
        }


        if (title != null && !title.trim().isEmpty()) {
            validateTitle(title);
            task.setTitle(title.trim());
        }
        if (description != null && !description.trim().isEmpty()) {
            validateDescription(description);
            task.setDescription(description.trim());
        }
        if (durationMinutes != null) {
            validateDuration(durationMinutes);
            task.setDurationMinutes(durationMinutes);
        }
        if (category != null && !category.trim().isEmpty()) {
            validateCategory(category);
            task.setCategory(category.trim());
        }

        return taskRepository.save(task);
    }


    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
    }


    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }


    public void deleteTask(Long taskId, Long mentorId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));


        if (!task.getMentor().getMentorId().equals(mentorId)) {
            throw new IllegalStateException("Mentor does not have permission to delete this task");
        }

        taskRepository.deleteById(taskId);
    }


    public boolean doesTaskExist(Long taskId) {
        return taskRepository.existsById(taskId);
    }




    public List<Task> getTasksByMentor(Long mentorId) {

        if (!mentorRepository.existsById(mentorId)) {
            throw new IllegalArgumentException("Mentor not found with ID: " + mentorId);
        }
        return taskRepository.findByMentorId(mentorId);
    }


    public List<Task> getTasksByMentorAndCategory(Long mentorId, String category) {

        if (!mentorRepository.existsById(mentorId)) {
            throw new IllegalArgumentException("Mentor not found with ID: " + mentorId);
        }
        validateCategory(category);
        return taskRepository.findByMentorIdAndCategory(mentorId, category.trim());
    }


    public long countTasksByMentor(Long mentorId) {

        if (!mentorRepository.existsById(mentorId)) {
            throw new IllegalArgumentException("Mentor not found with ID: " + mentorId);
        }
        return taskRepository.countByMentorId(mentorId);
    }




    public List<Task> getTasksByCategory(String category) {
        validateCategory(category);
        return taskRepository.findByCategory(category.trim());
    }


    public List<Task> searchTasksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Search title cannot be empty");
        }
        return taskRepository.findByTitleContainingIgnoreCase(title.trim());
    }


    public List<Task> getShortDurationTasks(Integer maxDuration) {
        if (maxDuration == null || maxDuration <= 0) {
            maxDuration = 45;
        }
        validateDuration(maxDuration);
        return taskRepository.findByDurationMinutesLessThanEqual(maxDuration);
    }


    public List<Task> getLongDurationTasks(Integer minDuration) {
        if (minDuration == null || minDuration <= 0) {
            minDuration = 90; // Default to 90 minutes or more
        }
        validateDuration(minDuration);
        return taskRepository.findByDurationMinutesGreaterThanEqual(minDuration);
    }


    public List<Task> getTasksByDurationRange(Integer minDuration, Integer maxDuration) {
        if (minDuration == null || maxDuration == null) {
            throw new IllegalArgumentException("Both min and max duration are required");
        }
        if (minDuration > maxDuration) {
            throw new IllegalArgumentException("Min duration cannot be greater than max duration");
        }
        validateDuration(minDuration);
        validateDuration(maxDuration);


        return taskRepository.findAll().stream()
                .filter(task -> task.getDurationMinutes() >= minDuration
                        && task.getDurationMinutes() <= maxDuration)
                .toList();
    }




    public TaskSearchDTO getTaskStatistics(Long taskId) {
        Task task = getTaskById(taskId);

        TaskSearchDTO stats = new TaskSearchDTO();
        stats.setTaskId(task.getTaskId());
        stats.setMentorId(task.getMentor().getMentorId());
        stats.setMentorName(task.getMentor().getName());
        stats.setTitle(task.getTitle());
        stats.setDescription(task.getDescription());
        stats.setDurationMinutes(task.getDurationMinutes());
        stats.setCategory(task.getCategory());

        return stats;
    }


    public List<TaskSearchDTO> getAllTaskStatistics() {
        List<Task> tasks = getAllTasks();

        return tasks.stream()
                .map(task -> {
                    TaskSearchDTO stats = new TaskSearchDTO();
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


    public List<TaskSearchDTO> getTaskStatisticsByMentor(Long mentorId) {
        List<Task> tasks = getTasksByMentor(mentorId);

        return tasks.stream()
                .map(task -> {
                    TaskSearchDTO stats = new TaskSearchDTO();
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



    private void validateTaskData(String title, String description, Integer durationMinutes, String category) {
        validateTitle(title);
        validateDescription(description);
        validateDuration(durationMinutes);
        validateCategory(category);
    }


    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Task title must not exceed 200 characters");
        }
        if (title.trim().length() < 5) {
            throw new IllegalArgumentException("Task title must be at least 5 characters long");
        }
    }


    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description is required");
        }
        if (description.trim().length() < 20) {
            throw new IllegalArgumentException("Task description must be at least 20 characters long");
        }
        if (description.length() > 2000) {
            throw new IllegalArgumentException("Task description must not exceed 2000 characters");
        }
    }


    private void validateDuration(Integer durationMinutes) {
        if (durationMinutes == null) {
            throw new IllegalArgumentException("Task duration is required");
        }
        if (durationMinutes < MIN_DURATION) {
            throw new IllegalArgumentException(
                    "Task duration must be at least " + MIN_DURATION + " minutes");
        }
        if (durationMinutes > MAX_DURATION) {
            throw new IllegalArgumentException(
                    "Task duration must not exceed " + MAX_DURATION + " minutes");
        }
        // Duration should be in 15-minute increments
        if (durationMinutes % 15 != 0) {
            throw new IllegalArgumentException("Task duration must be in 15-minute increments");
        }
    }


    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Task category is required");
        }

        // Validate against predefined categories
        List<String> validCategories = List.of(
                CATEGORY_RESUME_REVIEW,
                CATEGORY_INTERVIEW_PREP,
                CATEGORY_CAREER_ADVICE,
                CATEGORY_TECHNICAL_MENTORING,
                CATEGORY_NETWORKING,
                CATEGORY_PROJECT_REVIEW,
                CATEGORY_PROGRAMMING,
                CATEGORY_OTHER
        );

        if (!validCategories.contains(category.trim())) {
            throw new IllegalArgumentException(
                    "Invalid category. Must be one of: " + String.join(", ", validCategories));
        }
    }
}