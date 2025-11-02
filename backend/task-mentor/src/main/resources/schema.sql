-- TaskMentor Database Schema
-- Database: taskmentor
-- Author: James No
-- Date: November 2025

-- Drop tables if they exist (in reverse order due to foreign keys)
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS mentors CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Table 1: users
-- Stores basic authentication information for all users
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- bcrypt hashed
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('student', 'mentor')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: mentors
-- Stores mentor-specific profile information
CREATE TABLE mentors (
    mentor_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    name VARCHAR(150) NOT NULL,
    bio TEXT,
    role_title VARCHAR(150),
    company VARCHAR(150),
    years_experience INTEGER,
    industries TEXT,
    expertise_areas TEXT,
    profile_photo_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Table 3: students
-- Stores student-specific profile information
CREATE TABLE students (
    student_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    name VARCHAR(150) NOT NULL,
    bio TEXT,
    major VARCHAR(100),
    graduation_year INTEGER,
    career_interests TEXT,
    profile_photo_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Table 4: tasks
-- Stores tasks/services offered by mentors
CREATE TABLE tasks (
    task_id SERIAL PRIMARY KEY,
    mentor_id INTEGER NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    duration_minutes INTEGER NOT NULL,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mentor_id) REFERENCES mentors(mentor_id) ON DELETE CASCADE
);

-- Table 5: bookings
-- Stores session booking requests and confirmations
CREATE TABLE bookings (
    booking_id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,
    mentor_id INTEGER NOT NULL,
    task_id INTEGER NOT NULL,
    proposed_datetime TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'declined', 'cancelled')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (mentor_id) REFERENCES mentors(mentor_id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_account_type ON users(account_type);
CREATE INDEX idx_mentors_user_id ON mentors(user_id);
CREATE INDEX idx_students_user_id ON students(user_id);
CREATE INDEX idx_tasks_mentor_id ON tasks(mentor_id);
CREATE INDEX idx_bookings_student_id ON bookings(student_id);
CREATE INDEX idx_bookings_mentor_id ON bookings(mentor_id);
CREATE INDEX idx_bookings_status ON bookings(status);
