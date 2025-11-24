# Task 1: File Storage Integration and Task Database Operations

**Assigned to**: James No  
**Start Date**: November 16, 2025  
**Start Time**: 00:34 UTC

## Objective
Implement file storage integration and enhance task database operations to support file attachments for tasks.

## Background Context
- **Previous Work**: 
  - Nov 5: Added JPA entity relationships (User, Student, Mentor, Task)
  - Nov 8: Created service layer with TaskService, StudentService, MentorService
  - Nov 9: Updated services for entity compatibility

## Requirements
1. File storage configuration (local or cloud-based)
2. File metadata storage in database
3. Task entity enhancement for file references
4. Repository methods for file-related operations
5. Service layer methods for file handling

## Timeline

### 2025-11-16 00:34 UTC
- Started Task 1
- Created documentation file
- Reviewing current codebase structure

### 2025-11-16 00:38 UTC
- Added file storage configuration to application.properties

### 2025-11-16 00:40 UTC
- Enhanced Task entity with image fields

### 2025-11-16 00:42 UTC
- Created FileStorageService with full file management capabilities

### 2025-11-16 00:45 UTC
- Updated TaskService with image handling methods

### 2025-11-16 00:47 UTC
- Added file-related query methods to TaskRepository

### 2025-11-16 00:50 UTC
- **Task 1 Completed**
- All code compiles successfully (Maven build successful)
- Documentation updated with full implementation details
- Total time: ~16 minutes

## Progress Log
- [x] Review current Task entity structure
- [x] Review current TaskRepository
- [x] Review current TaskService
- [x] Design file storage approach
- [x] Implement file storage configuration
- [x] Add file metadata fields to Task entity
- [x] Create/update repository methods
- [x] Implement file handling in service layer
- [ ] Test file operations (pending)

## Implementation Details

### 1. File Storage Configuration (Completed: 2025-11-16 00:38 UTC)
**File**: `application.properties`
- Added Spring multipart configuration (max 5MB file size)
- Configured upload directory: `./uploads/task-images`
- Allowed image extensions: jpg, jpeg, png, gif, webp
- Max file size: 5MB (5242880 bytes)

### 2. Task Entity Enhancement (Completed: 2025-11-16 00:40 UTC)
**File**: `entity/Task.java`
- Added `imageUrl` field (VARCHAR 500) - URL path to access the image
- Added `imageFileName` field (VARCHAR 255) - Stored filename in filesystem
- Added `imageFileSize` field (BIGINT) - File size in bytes
- Added getters/setters for all new fields

### 3. FileStorageService Creation (Completed: 2025-11-16 00:42 UTC)
**File**: `service/FileStorageService.java`
**Features**:
- File upload validation (size, extension, path traversal)
- UUID-based unique filename generation
- Local filesystem storage
- File deletion support
- Security checks (path traversal prevention)

**Methods**:
- `storeFile(MultipartFile)` - Stores file and returns unique filename
- `deleteFile(String)` - Deletes file from storage
- `getFilePath(String)` - Returns full path to stored file
- `fileExists(String)` - Checks if file exists
- `validateFile(MultipartFile)` - Validates file before upload

### 4. TaskService Enhancement (Completed: 2025-11-16 00:45 UTC)
**File**: `service/TaskService.java`
**New Methods**:
- `createTaskWithImage(Task, MultipartFile)` - Creates task with image attachment
- `updateTaskWithImage(Long, Task, MultipartFile)` - Updates task and optionally replaces image

**Modified Methods**:
- `deleteTask(Long)` - Now also deletes associated image file

**Features**:
- Automatic file cleanup on task deletion
- Old image deletion when updating with new image
- Image metadata storage (filename, URL, size)

### 5. TaskRepository Enhancement (Completed: 2025-11-16 00:47 UTC)
**File**: `repository/TaskRepository.java`
**New Query Methods**:
- `findTasksWithImages()` - Returns all tasks that have images
- `findTasksWithoutImages()` - Returns tasks without images
- `findTasksWithImagesByMentorId(Long)` - Returns mentor's tasks with images

## Files Modified/Created
1. **Modified**: `backend/task-mentor/src/main/resources/application.properties`
2. **Modified**: `backend/task-mentor/src/main/java/com/task_mentor/task_mentor/entity/Task.java`
3. **Created**: `backend/task-mentor/src/main/java/com/task_mentor/task_mentor/service/FileStorageService.java`
4. **Modified**: `backend/task-mentor/src/main/java/com/task_mentor/task_mentor/service/TaskService.java`
5. **Modified**: `backend/task-mentor/src/main/java/com/task_mentor/task_mentor/repository/TaskRepository.java`

## Technical Decisions

### Local File Storage
- **Decision**: Use local filesystem storage instead of cloud storage (AWS S3)
- **Rationale**: 
  - Simpler setup for development and testing
  - No external dependencies or API keys needed
  - Can easily migrate to cloud storage later if needed
  - Sufficient for MVP and small-scale deployment

### Image Metadata in Database
- Stored three separate fields: URL, filename, and size
- URL is for API access (`/api/files/task-images/{filename}`)
- Filename is the actual stored filename (UUID-based)
- Size is stored for potential quota management

### UUID Filenames
- Prevents filename conflicts
- Adds security through obscurity
- Preserves original extension for content type

### File Validation
- File size limit: 5MB (reasonable for task images)
- Allowed formats: Common image types (jpg, png, gif, webp)
- Path traversal attack prevention

## Database Schema Impact

When the application runs with `spring.jpa.hibernate.ddl-auto=update`, the following columns will be added to the `tasks` table:

```sql
ALTER TABLE tasks 
ADD COLUMN image_url VARCHAR(500),
ADD COLUMN image_file_name VARCHAR(255),
ADD COLUMN image_file_size BIGINT;
```

## API Usage Examples

### Create Task with Image
```java
Task task = new Task(mentorId, "Resume Review", "I'll review your resume", 30);
MultipartFile imageFile = // from request
Task created = taskService.createTaskWithImage(task, imageFile);
```

### Update Task with New Image
```java
Task updates = new Task();
updates.setTitle("Updated Title");
MultipartFile newImage = // from request
Task updated = taskService.updateTaskWithImage(taskId, updates, newImage);
```

### Delete Task (Auto-deletes Image)
```java
taskService.deleteTask(taskId); // Automatically deletes associated image file
```

## Next Steps (Task 3)
- Create REST controller endpoints for file upload
- Create file download/serving endpoint
- Add multipart form data handling
- Add DTOs for task creation with images
- Add proper error handling and HTTP responses

## Notes
- Branch: `profile-task-services`
- All changes are backward compatible (image fields are nullable)
- Existing tasks without images will continue to work
- File storage directory will be created automatically on startup
