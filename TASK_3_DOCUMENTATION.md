# Task 3: File Upload Endpoint and Task API Endpoints

**Assigned to**: James No  
**Start Date**: November 16, 2025  
**Start Time**: 01:51 UTC

## Objective
Create REST API endpoints for task CRUD operations with file upload support, enabling frontend interaction with the task management system.

## Background Context
- **Task 1 Completed**: File storage integration and database operations
- **Current Branch**: `profile-task-services`
- **Prerequisites**: 
  - Task entity with image fields ✅
  - TaskService with image handling ✅
  - FileStorageService for file operations ✅

## Requirements
1. REST API endpoints for task CRUD operations
2. Multipart form data handling for image uploads
3. File serving endpoint for accessing uploaded images
4. DTO (Data Transfer Object) classes for request/response
5. Proper HTTP status codes and error responses
6. Exception handling across all endpoints

## API Endpoints to Implement

### Task Endpoints
- `POST /api/tasks` - Create new task with optional image
- `GET /api/tasks/{id}` - Get single task by ID
- `GET /api/tasks` - Get all tasks (with optional filters)
- `GET /api/tasks/mentor/{mentorId}` - Get tasks by mentor
- `PUT /api/tasks/{id}` - Update task with optional new image
- `DELETE /api/tasks/{id}` - Delete task and associated image

### File Endpoints
- `GET /api/files/task-images/{filename}` - Serve image file

## Timeline

### 2025-11-16 01:51 UTC
- Started Task 3
- Created documentation file
- Created TODO list

## Progress Log
- [x] Create DTO classes
- [x] Create TaskController with CRUD endpoints
- [x] Create FileController for serving images
- [x] Add global exception handling
- [x] Test compilation
- [ ] Create learning guide (pending)

##  Implementation Details

### 1. DTO Classes (Completed: 2025-11-16 07:18 UTC)

**Created 3 DTO classes:**

#### TaskCreateRequest.java
- Used for creating new tasks
- Includes validation annotations:
  - `@NotNull` for mentorId
  - `@NotBlank` for title and description
  - `@Min(15)` and `@Max(480)` for duration
  - `@Size` constraints for text fields
- Image file handled separately as MultipartFile

#### TaskUpdateRequest.java
- Used for updating existing tasks
- All fields optional (only update what's provided)
- Same validation constraints as create request
- Enables partial updates

#### TaskResponse.java
- Used for returning task data to frontend
- Includes all task fields plus image metadata
- Has `fromEntity()` factory method for easy conversion
- Returns mentorId from Mentor relationship

### 2. TaskController (Completed: 2025-11-16 07:19 UTC)
**File**: `controller/TaskController.java` (189 lines)

**Endpoints Implemented:**

1. **POST /api/tasks** - Create task with optional image
   - Accepts multipart form data
   - Validates request using `@Valid`
   - Returns 201 Created with task details

2. **GET /api/tasks/{id}** - Get single task
   - Returns 200 OK with task
   - Returns 400 Bad Request if not found

3. **GET /api/tasks** - Get all tasks with filtering
   - Query parameters:
     - `category` - Filter by category
     - `mentorId` - Filter by mentor  
     - `minDuration` - Minimum duration
     - `maxDuration` - Maximum duration
     - `search` - Search in title
   - Returns 200 OK with list of tasks

4. **GET /api/tasks/mentor/{mentorId}** - Get tasks by mentor
   - Dedicated endpoint for mentor's tasks
   - Returns 200 OK with list

5. **PUT /api/tasks/{id}** - Update task with optional new image
   - Accepts multipart form data
   - Only updates provided fields
   - Replaces image if new one provided
   - Returns 200 OK with updated task

6. **DELETE /api/tasks/{id}** - Delete task
   - Also deletes associated image file
   - Returns 204 No Content

**Features:**
- `@CrossOrigin(origins = "*")` for frontend access
- Multipart form data support
- DTO to Entity conversion
- Query parameter filtering
- Stream API for list transformations

### 3. FileController (Completed: 2025-11-16 07:20 UTC)
**File**: `controller/FileController.java` (76 lines)

**Endpoint:**
- **GET /api/files/task-images/{filename}** - Serve image file
  - Returns image with proper Content-Type
  - Uses `inline` disposition for browser display
  - Supports jpg, jpeg, png, gif, webp
  - Returns 500 if file not found

**Features:**
- Automatic content type detection
- Resource-based file serving
- Proper HTTP headers for inline display
- Error handling for missing files

### 4. GlobalExceptionHandler (Completed: 2025-11-16 07:21 UTC)
**File**: `controller/GlobalExceptionHandler.java` (161 lines)

**Exception Handlers:**

1. **MethodArgumentNotValidException** (400 Bad Request)
   - Validation errors from `@Valid`
   - Returns field-specific error messages
   - Example: {"title": "Title is required"}

2. **IllegalArgumentException** (400 Bad Request)
   - Business logic errors
   - Example: "Mentor not found with ID: 5"

3. **MaxUploadSizeExceededException** (413 Payload Too Large)
   - File size exceeded (>5MB)
   - Clear error message

4. **RuntimeException** (500 Internal Server Error)
   - File storage errors
   - Unexpected runtime issues

5. **Exception** (500 Internal Server Error)
   - Catch-all for unexpected errors
   - Prevents stack trace exposure

**Error Response Format:**
```json
{
  "status": 400,
  "message": "Validation failed",
  "fieldErrors": {
    "title": "Title is required",
    "durationMinutes": "Duration must be at least 15 minutes"
  },
  "timestamp": "2025-11-16T07:20:00"
}
```

### 5. Service Layer Updates (Completed: 2025-11-16 07:17 UTC)

**TaskService modifications:**
- Updated `createTask()` to accept `mentorId` parameter
- Updated `createTaskWithImage()` to accept `mentorId` parameter
- Changed to fetch Mentor entity and set relationship
- Updated validation to check `task.getMentor() != null`

**MentorService fix:**
- Fixed `createMentor()` to use `mentor.getUser().getUserId()`
- Compatible with User relationship instead of direct userId

## Files Created/Modified

### Created (7 files):
1. `dto/TaskCreateRequest.java` - 84 lines
2. `dto/TaskUpdateRequest.java` - 69 lines  
3. `dto/TaskResponse.java` - 140 lines
4. `controller/TaskController.java` - 189 lines
5. `controller/FileController.java` - 76 lines
6. `controller/GlobalExceptionHandler.java` - 161 lines
7. `TASK_3_DOCUMENTATION.md` - This file

### Modified (2 files):
1. `service/TaskService.java` - Updated for Mentor relationship
2. `service/MentorService.java` - Fixed User relationship

**Total new code**: ~719 lines

## Technical Decisions

### 1. DTOs vs Direct Entity Exposure
**Decision**: Use separate DTO classes for requests and responses

**Rationale**:
- **Security**: Don't expose internal entity structure
- **Validation**: Can add `@Valid` annotations
- **Flexibility**: Can change entity without breaking API
- **Clean API**: Only expose what frontend needs

### 2. Multipart Form Data
**Decision**: Use `@RequestPart` for JSON + file upload

**Rationale**:
- Allows sending structured data (JSON) + file in single request
- Alternative would be separate endpoints (create task, then upload image)
- More efficient for users (one API call instead of two)

**Format**:
```
POST /api/tasks
Content-Type: multipart/form-data

--boundary
Content-Disposition: form-data; name="task"
Content-Type: application/json

{"mentorId": 1, "title": "...", ...}
--boundary
Content-Disposition: form-data; name="image"; filename="photo.jpg"
Content-Type: image/jpeg

[binary image data]
--boundary--
```

### 3. Query Parameters for Filtering
**Decision**: Use optional `@RequestParam` in single GET endpoint

**Rationale**:
- Flexible: `/api/tasks?category=resume&mentorId=5`
- Alternative: Separate endpoints for each filter (too many endpoints)
- Frontend can easily combine filters
- RESTful: GET with query parameters is standard

### 4. Global Exception Handling
**Decision**: Use `@ControllerAdvice` instead of try-catch in each controller

**Rationale**:
- **DRY principle**: Don't repeat error handling
- **Consistency**: All errors have same format
- **Maintainability**: Single place to update error responses
- **Clean controllers**: No cluttered try-catch blocks

### 5. HTTP Status Codes
**Chosen codes**:
- 200 OK - Successful GET/PUT
- 201 Created - Successful POST
- 204 No Content - Successful DELETE
- 400 Bad Request - Validation errors, not found
- 413 Payload Too Large - File too big
- 500 Internal Server Error - Unexpected errors

### 6. CORS Configuration
**Decision**: `@CrossOrigin(origins = "*")` for development

**Note**: In production, should be:
```java
@CrossOrigin(origins = "http://localhost:3000") // or actual frontend URL
```

## API Examples

### Create Task with Image
```bash
curl -X POST http://localhost:8080/api/tasks \
  -F 'task={"mentorId":1,"title":"Resume Review","description":"I will review your resume","durationMinutes":30,"category":"Career"};type=application/json' \
  -F 'image=@photo.jpg'
```

### Get All Tasks
```bash
curl http://localhost:8080/api/tasks
```

### Get Tasks by Mentor
```bash
curl http://localhost:8080/api/tasks/mentor/1
```

### Search Tasks
```bash
curl "http://localhost:8080/api/tasks?search=resume&category=Career"
```

### Update Task
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -F 'task={"title":"Updated Title","durationMinutes":45};type=application/json'
```

### Delete Task
```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

### Get Image
```bash
curl http://localhost:8080/api/files/task-images/abc123-uuid.jpg
```

## Testing

### Compilation Test (Completed: 2025-11-16 07:22 UTC)
- ✅ All code compiles successfully
- ✅ No syntax errors
- ✅ 30 source files compiled
- ✅ Maven build success

### Manual Testing (Recommended)
Use Postman or similar to test:
1. Create task without image
2. Create task with image
3. Get task by ID
4. Get all tasks
5. Filter tasks by category/mentor
6. Update task
7. Update task with new image
8. Delete task
9. Serve image file
10. Test error cases (invalid data, missing mentor, file too large)

## Next Steps (Task 4+)

Backend is now complete for basic task operations. Frontend can:
1. Create task forms with image upload
2. Display task lists with images
3. Filter/search tasks
4. Edit tasks
5. Delete tasks

Remaining work:
- Task 4: Profile display page and task creation UI (Frontend)
- Task 5: Search and filter business logic (Backend - mostly done!)
- Task 6: Search API endpoint (Backend - done via query params!)
- Task 7: Discovery page UI and filter components (Frontend)

## Notes
- All endpoints use JSON for requests/responses
- Images handled as multipart form data
- Authentication not yet enforced (add @PreAuthorize later)
- CORS configured for development (update for production)
- Error responses are consistent across all endpoints
