-- SQL for creating student_courses table
-- Note: The existing system uses 'course_registrations' which matches the requirement
-- (linking student_id and course_code). 
-- This SQL guarantees the table exists as expected by the existing repository logic.

CREATE TABLE IF NOT EXISTS course_registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES courses(course_code) ON DELETE CASCADE,
    UNIQUE(student_id, course_code)
);
