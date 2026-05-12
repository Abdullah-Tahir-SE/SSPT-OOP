-- Smart Semester Progress Tracker Database Schema
-- Run this script in MySQL to create the database and tables

CREATE DATABASE IF NOT EXISTS SmartSemester;
USE SmartSemester;

-- Students table
CREATE TABLE IF NOT EXISTS students (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    registration_no VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    key_pass VARCHAR(255),
    program_level VARCHAR(50),
    degree VARCHAR(100),
    semester INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_registration_no (registration_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Courses table
CREATE TABLE IF NOT EXISTS courses (
    course_code VARCHAR(20) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    theory_credits INT NOT NULL DEFAULT 0,
    lab_credits INT NOT NULL DEFAULT 0,
    has_lab BOOLEAN DEFAULT FALSE,
    is_fyp BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Course Registrations table (links students to courses)
CREATE TABLE IF NOT EXISTS course_registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES courses(course_code) ON DELETE CASCADE,
    UNIQUE KEY unique_student_course (student_id, course_code),
    INDEX idx_student_id (student_id),
    INDEX idx_course_code (course_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Attendance Records table
CREATE TABLE IF NOT EXISTS attendance_records (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    registration_id INT NOT NULL,
    mid_conducted INT DEFAULT 0,
    mid_attended INT DEFAULT 0,
    final_conducted INT DEFAULT 0,
    final_attended INT DEFAULT 0,
    lab_conducted_mid INT DEFAULT 0,
    lab_attended_mid INT DEFAULT 0,
    lab_conducted_final INT DEFAULT 0,
    lab_attended_final INT DEFAULT 0,
    FOREIGN KEY (registration_id) REFERENCES course_registrations(registration_id) ON DELETE CASCADE,
    UNIQUE KEY unique_registration_attendance (registration_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Marks table
CREATE TABLE IF NOT EXISTS marks (
    mark_id INT AUTO_INCREMENT PRIMARY KEY,
    registration_id INT NOT NULL,
    assessment_type ENUM('QUIZ', 'ASSIGNMENT', 'MID', 'FINAL', 'LAB_ASSIGNMENT', 'LAB_MID', 'LAB_FINAL') NOT NULL,
    obtained DOUBLE NOT NULL,
    graded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES course_registrations(registration_id) ON DELETE CASCADE,
    INDEX idx_registration_id (registration_id),
    INDEX idx_assessment_type (assessment_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert some sample courses
INSERT INTO courses (course_code, title, theory_credits, lab_credits, has_lab, is_fyp) VALUES
('CS101', 'Introduction to Computer Science', 3, 0, FALSE, FALSE),
('CS201', 'Data Structures', 3, 1, TRUE, FALSE),
('CS301', 'Database Systems', 3, 1, TRUE, FALSE),
('CS401', 'Software Engineering', 3, 0, FALSE, FALSE),
('CS499', 'Final Year Project', 0, 6, FALSE, TRUE)
ON DUPLICATE KEY UPDATE title=VALUES(title);

