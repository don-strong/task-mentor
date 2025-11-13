# Task Mentor Database ER Diagram & Design

## Entity Relationship Diagram (Text Representation)

```
┌─────────────────┐
│     USERS       │
├─────────────────┤
│ PK user_id      │
│    username     │
│    email (UQ)   │
│    password     │
│    account_type │
│    created_at   │
│    updated_at   │
└─────────────────┘
         │
         ├─────────────────────────┐
         │                         │
         ▼                         ▼
┌─────────────────┐       ┌─────────────────┐
│    MENTORS      │       │    STUDENTS     │
├─────────────────┤       ├─────────────────┤
│ PK mentor_id    │       │ PK student_id   │
│ FK user_id (UQ) │       │ FK user_id (UQ) │
│    name         │       │    name         │
│    bio          │       │    bio          │
│    role_title   │       │    major        │
│    company      │       │    grad_year    │
│    yrs_exp      │       │    interests    │
│    industries   │       │    photo_url    │
│    expertise    │       │    created_at   │
│    photo_url    │       │    updated_at   │
│    created_at   │       └─────────────────┘
│    updated_at   │                │
└─────────────────┘                │
         │                         │
         │                         │
         ▼                         │
┌─────────────────┐                │
│     TASKS       │                │
├─────────────────┤                │
│ PK task_id      │                │
│ FK mentor_id    │                │
│    title        │                │
│    description  │                │
│    duration_min │                │
│    category     │                │
│    is_active    │                │
│    created_at   │                │
│    updated_at   │                │
└─────────────────┘                │
         │                         │
         └──────────┬──────────────┘
                    ▼
           ┌─────────────────┐
           │    BOOKINGS     │
           ├─────────────────┤
           │ PK booking_id   │
           │ FK student_id   │
           │ FK mentor_id    │
           │ FK task_id      │
           │    proposed_dt  │
           │    status       │
           │    stud_msg     │
           │    mentor_resp  │
           │    created_at   │
           │    updated_at   │
           └─────────────────┘
```

## Table Relationships

### 1. USERS → MENTORS (1:0..1)
- **Type:** One-to-Zero-or-One
- **Relationship:** A user with account_type='mentor' can have one mentor profile
- **Foreign Key:** mentors.user_id → users.user_id
- **Delete Rule:** CASCADE (deleting user deletes mentor profile)
- **Constraint:** UNIQUE on user_id (prevents multiple mentor profiles per user)

### 2. USERS → STUDENTS (1:0..1)
- **Type:** One-to-Zero-or-One
- **Relationship:** A user with account_type='student' can have one student profile
- **Foreign Key:** students.user_id → users.user_id
- **Delete Rule:** CASCADE (deleting user deletes student profile)
- **Constraint:** UNIQUE on user_id (prevents multiple student profiles per user)

### 3. MENTORS → TASKS (1:N)
- **Type:** One-to-Many
- **Relationship:** A mentor can create multiple tasks
- **Foreign Key:** tasks.mentor_id → mentors.mentor_id
- **Delete Rule:** CASCADE (deleting mentor deletes their tasks)
- **Business Logic:** Mentors manage their own task menu

### 4. BOOKINGS → STUDENTS (N:1)
- **Type:** Many-to-One
- **Relationship:** A student can make multiple booking requests
- **Foreign Key:** bookings.student_id → students.student_id
- **Delete Rule:** CASCADE (deleting student deletes their bookings)

### 5. BOOKINGS → MENTORS (N:1)
- **Type:** Many-to-One
- **Relationship:** A mentor can receive multiple booking requests
- **Foreign Key:** bookings.mentor_id → mentors.mentor_id
- **Delete Rule:** CASCADE (deleting mentor deletes bookings for them)

### 6. BOOKINGS → TASKS (N:1)
- **Type:** Many-to-One
- **Relationship:** A task can be booked multiple times by different students
- **Foreign Key:** bookings.task_id → tasks.task_id
- **Delete Rule:** CASCADE (deleting task deletes associated bookings)

## Table Details

### USERS
**Purpose:** Base authentication and account management
**Primary Key:** user_id (SERIAL)
**Unique Constraints:** email
**Check Constraints:** account_type IN ('student', 'mentor')
**Indexes:**
- idx_users_email (for login lookups)
- idx_users_account_type (for filtering)

### MENTORS
**Purpose:** Mentor-specific profile information
**Primary Key:** mentor_id (SERIAL)
**Foreign Keys:** user_id → users.user_id (UNIQUE)
**Check Constraints:** years_experience >= 0
**Indexes:**
- idx_mentors_user_id (for user → mentor lookups)

### STUDENTS
**Purpose:** Student-specific profile information
**Primary Key:** student_id (SERIAL)
**Foreign Keys:** user_id → users.user_id (UNIQUE)
**Check Constraints:** graduation_year BETWEEN 2020 AND 2035
**Indexes:**
- idx_students_user_id (for user → student lookups)

### TASKS
**Purpose:** Mentor's offerings/services menu
**Primary Key:** task_id (SERIAL)
**Foreign Keys:** mentor_id → mentors.mentor_id
**Check Constraints:** duration_minutes > 0
**Indexes:**
- idx_tasks_mentor_id (for mentor → tasks queries)
- idx_tasks_category (for filtering by category)
- idx_tasks_active (for filtering active tasks)

### BOOKINGS
**Purpose:** Session requests and scheduling
**Primary Key:** booking_id (SERIAL)
**Foreign Keys:** 
- student_id → students.student_id
- mentor_id → mentors.mentor_id
- task_id → tasks.task_id
**Check Constraints:** 
- status IN ('pending', 'accepted', 'declined', 'cancelled')
- proposed_datetime > CURRENT_TIMESTAMP
**Indexes:**
- idx_bookings_student_id (student's bookings)
- idx_bookings_mentor_id (mentor's requests)
- idx_bookings_status (filtering by status)
- idx_bookings_datetime (scheduling queries)

## Data Types Explained

### TEXT vs VARCHAR
- **VARCHAR(n):** Limited length, good for fields with known max size (email, names)
- **TEXT:** Unlimited length, good for user-generated content (bio, descriptions)

### SERIAL
- Auto-incrementing integer
- Automatically creates a sequence
- Perfect for primary keys

### TIMESTAMP
- Stores date and time
- DEFAULT CURRENT_TIMESTAMP for automatic creation time
- Triggers update updated_at automatically

### CHECK Constraints
- Enforce data validity at database level
- Prevents invalid data even if application code fails
- Examples: status values, positive durations, valid years

## Common Queries for Your API Endpoints

### Auth Endpoints

```sql
-- Register user
INSERT INTO users (username, email, password, account_type) 
VALUES ($1, $2, $3, $4) 
RETURNING user_id;

-- Login (find by email)
SELECT user_id, email, password, account_type 
FROM users 
WHERE email = $1;
```

### Profile Endpoints

```sql
-- Create mentor profile
INSERT INTO mentors (user_id, name, bio, role_title, company, years_experience, industries, expertise_areas)
VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
RETURNING mentor_id;

-- Get mentor profile with user info
SELECT u.email, u.username, m.* 
FROM mentors m
JOIN users u ON m.user_id = u.user_id
WHERE m.mentor_id = $1;

-- Update mentor profile
UPDATE mentors 
SET name = $1, bio = $2, company = $3, ...
WHERE mentor_id = $4;
```

### Task Endpoints

```sql
-- Create task
INSERT INTO tasks (mentor_id, title, description, duration_minutes, category)
VALUES ($1, $2, $3, $4, $5)
RETURNING task_id;

-- Get all tasks for a mentor
SELECT * FROM tasks 
WHERE mentor_id = $1 AND is_active = true
ORDER BY category, title;

-- Get tasks by userId (need to join through mentor)
SELECT t.* FROM tasks t
JOIN mentors m ON t.mentor_id = m.mentor_id
WHERE m.user_id = $1 AND t.is_active = true;
```

### Search/Discovery Endpoints

```sql
-- Get all mentors (browse)
SELECT m.*, COUNT(t.task_id) as task_count
FROM mentors m
LEFT JOIN tasks t ON m.mentor_id = t.mentor_id AND t.is_active = true
GROUP BY m.mentor_id
ORDER BY m.name;

-- Search mentors by keyword
SELECT DISTINCT m.*
FROM mentors m
WHERE m.name ILIKE '%' || $1 || '%'
   OR m.company ILIKE '%' || $1 || '%'
   OR m.expertise_areas ILIKE '%' || $1 || '%';

-- Filter by industry
SELECT * FROM mentors
WHERE industries ILIKE '%' || $1 || '%';
```

### Booking Endpoints

```sql
-- Create booking request
INSERT INTO bookings (student_id, mentor_id, task_id, proposed_datetime, student_message)
VALUES ($1, $2, $3, $4, $5)
RETURNING booking_id;

-- Get all bookings for student
SELECT b.*, m.name as mentor_name, t.title as task_title
FROM bookings b
JOIN mentors m ON b.mentor_id = m.mentor_id
JOIN tasks t ON b.task_id = t.task_id
WHERE b.student_id = $1
ORDER BY b.proposed_datetime;

-- Get all bookings for mentor
SELECT b.*, s.name as student_name, s.major, t.title as task_title
FROM bookings b
JOIN students s ON b.student_id = s.student_id
JOIN tasks t ON b.task_id = t.task_id
WHERE b.mentor_id = $1 AND b.status = 'pending'
ORDER BY b.created_at;

-- Accept booking
UPDATE bookings 
SET status = 'accepted', mentor_response = $1
WHERE booking_id = $2 AND status = 'pending'
RETURNING *;

-- Check for double booking (conflict detection)
SELECT COUNT(*) 
FROM bookings 
WHERE mentor_id = $1 
  AND status = 'accepted'
  AND proposed_datetime = $2;
```

## Design Decisions & Rationale

### Why separate USERS from MENTORS/STUDENTS?
- **Authentication vs Profile Data:** Keeps auth logic separate from profile details
- **Flexibility:** User can theoretically be both (future feature)
- **Security:** Profile updates don't touch authentication data
- **Clean separation:** Follows Single Responsibility Principle

### Why TEXT for industries/expertise instead of separate tables?
- **MVP Simplicity:** Comma-separated strings are easier to start with
- **Quick Queries:** ILIKE searches work well for small datasets
- **Future Improvement:** Can migrate to JSON or separate tables later
- **Recommendation:** For Week 6+, consider JSON type or normalization

### Why is_active instead of hard delete for tasks?
- **Data Integrity:** Preserve historical bookings even if task removed
- **Audit Trail:** Can see what tasks existed at booking time
- **Easy Restore:** Can reactivate tasks without recreating them

### Why store mentor_id in bookings when we have task_id?
- **Performance:** Avoid JOIN to get mentor_id (direct index lookup)
- **Denormalization:** Acceptable for read-heavy operations
- **Query Simplicity:** "Get all mentor's bookings" is one lookup

### Why CHECK constraints?
- **Last Line of Defense:** Even if application validation fails
- **Database Integrity:** Enforces rules at lowest level
- **Documentation:** Constraints document business rules in schema

## Migration Strategy for Team

### Week 2-3: Initial Setup
1. All team members run schema script
2. Verify sample data works
3. Document database credentials in team wiki (not GitHub!)

### Week 4-5: JPA Entity Creation
1. Map each table to @Entity class
2. Use @Column annotations matching SQL types
3. Set up relationships with @OneToMany, @ManyToOne
4. Test CRUD operations

### Week 8-9: Schema Updates
1. Create migration scripts (new .sql files)
2. Version them: V2_add_ratings.sql, V3_add_availability.sql
3. Test locally first, then apply to shared dev database

### Week 10: Production Migration
1. Export data from dev database
2. Run schema on production PostgreSQL (Azure)
3. Import clean sample/test data
4. Verify all queries work

## Database Conventions Used

- **Table Names:** Lowercase, plural (users, mentors, bookings)
- **Column Names:** Lowercase, snake_case (user_id, created_at)
- **Primary Keys:** Always tablename_id (user_id, mentor_id)
- **Foreign Keys:** Same name as referenced column (user_id references user_id)
- **Timestamps:** created_at, updated_at (automatic via triggers)
- **Boolean Flags:** is_active, is_deleted (prefix with is_)
- **Indexes:** idx_tablename_columnname

## Performance Considerations

### Indexes Created
- All foreign keys (automatic lookups)
- Email (login queries)
- Status fields (filtering)
- Category fields (search/filter)
- Datetime fields (scheduling queries)

### When to Add More Indexes
- If queries on industries/expertise become slow
- If search by company becomes common
- If need to sort by years_experience

### Don't Over-Index
- Indexes slow down INSERT/UPDATE
- Each index takes storage space
- Start minimal, add based on actual performance issues

## Future Enhancements (Post-MVP)

1. **Ratings Table:** Add mentor_ratings (booking_id, rating, review)
2. **Availability Table:** Add mentor_availability (mentor_id, day, start_time, end_time)
3. **Normalize Expertise:** Create expertise table with many-to-many relationship
4. **Add Industries Table:** Similar to expertise normalization
5. **Session Notes:** Add session_notes field to bookings (post-session)
6. **Notification Preferences:** Add to users table
7. **Profile Views Tracking:** Add profile_views table for analytics

Your database is production-ready for MVP! 🎉
