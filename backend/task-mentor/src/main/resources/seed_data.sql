-- TaskMentor Seed Data
-- Sample test data for development and testing
-- Author: James No
-- Note: Passwords are bcrypt hashed version of "password123"

-- Insert sample users
INSERT INTO users (username, email, password, account_type) VALUES
('john_mentor', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'mentor'),
('jane_mentor', 'jane@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'mentor'),
('alex_student', 'alex@student.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student'),
('maria_student', 'maria@student.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student');

-- Insert mentor profiles
INSERT INTO mentors (user_id, name, bio, role_title, company, years_experience, industries, expertise_areas) VALUES
(1, 'John Smith', 'Senior software engineer with 10+ years of experience in full-stack development. Passionate about mentoring the next generation of developers.', 
 'Senior Software Engineer', 'Google', 10, 'Technology, Software Development', 'Java, Spring Boot, React, System Design'),
(2, 'Jane Doe', 'Product manager with expertise in agile methodologies and user-centered design. Love helping students break into tech.', 
 'Senior Product Manager', 'Microsoft', 8, 'Technology, Product Management', 'Product Strategy, Agile, User Research');

-- Insert student profiles
INSERT INTO students (user_id, name, bio, major, graduation_year, career_interests) VALUES
(3, 'Alex Johnson', 'Junior computer science student eager to learn about software engineering best practices.', 
 'Computer Science', 2026, 'Software Engineering, Backend Development'),
(4, 'Maria Garcia', 'Senior business student interested in tech product management roles.', 
 'Business Administration', 2025, 'Product Management, UX Design');

-- Insert tasks
INSERT INTO tasks (mentor_id, title, description, duration_minutes, category) VALUES
(1, 'Resume Review', 'Comprehensive review of your technical resume with actionable feedback', 30, 'Career Prep'),
(1, 'Mock Technical Interview', 'Practice coding interview with real-world problems and feedback', 60, 'Interview Prep'),
(1, 'System Design Discussion', 'Walk through a system design problem and learn best practices', 45, 'Technical Skills'),
(2, 'Product Management Career Chat', 'Learn about breaking into product management from a senior PM', 30, 'Career Prep'),
(2, 'Product Strategy Session', 'Review your product ideas and learn strategic thinking', 45, 'Product Skills');

-- Insert bookings
INSERT INTO bookings (student_id, mentor_id, task_id, proposed_datetime, status) VALUES
(1, 1, 1, '2025-12-01 14:00:00', 'accepted'),
(1, 1, 2, '2025-12-05 15:00:00', 'pending'),
(2, 2, 4, '2025-12-03 10:00:00', 'accepted'),
(2, 1, 3, '2025-12-10 16:00:00', 'pending');
