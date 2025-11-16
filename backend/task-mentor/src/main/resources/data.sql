-- TaskMentor Seed Data
-- Sample test data for development and testing
-- Author: James No
-- Note: Passwords are bcrypt hashed version of "password123"
    --data.sql file

-- USERS
INSERT INTO users (user_id, email, password, account_type, created_at)
VALUES
    (1, 'student1@example.com', '$2a$10$Dow1EwFbdmj1VxwTn9H8bu4lB6Rz/8J6OHX2z6lxXKxi7RjFz8zfi', 'student', NOW()),
    (2, 'mentor1@example.com',  '$2a$10$Dow1EwFbdmj1VxwTn9H8bu4lB6Rz/8J6OHX2z6lxXKxi7RjFz8zfi', 'mentor', NOW());

-- STUDENTS
INSERT INTO students (student_id, user_id, name, bio, major, graduation_year, career_interests, profile_photo_url, created_at)
VALUES
    (1, 1, 'John Student', 'Aspiring software engineer', 'Computer Science', 2026, 'Web Development, AI', NULL, NOW());

-- MENTORS
INSERT INTO mentors (mentor_id, user_id, name, bio, role_title, company, years_experience, industries, expertise_areas, profile_photo_url, created_at)
VALUES
    (1, 2, 'Jane Mentor', 'Experienced software engineer', 'Senior Engineer', 'TechCorp', 10, 'Software', 'Java, Spring Boot', NULL, NOW());

-- TASKS
INSERT INTO tasks (task_id, mentor_id, title, description, duration_minutes, category, created_at)
VALUES
    (1, 1, 'Java Tutoring', 'Mentor session for Java basics', 60, 'Programming', NOW()),
    (2, 1, 'Spring Boot Guidance', 'Mentor session for Spring Boot project', 90, 'Programming', NOW());

-- BOOKINGS
INSERT INTO bookings (booking_id, student_id, mentor_id, task_id, proposed_datetime, status, created_at, updated_at)
VALUES
    (1, 1, 1, 1, NOW() + INTERVAL '2 DAY', 'pending', NOW(), NOW()),
    (2, 1, 1, 2, NOW() + INTERVAL '3 DAY', 'pending', NOW(), NOW());