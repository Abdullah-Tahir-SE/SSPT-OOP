-- Create table for storing generated reports history
CREATE TABLE IF NOT EXISTS student_reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    report_title VARCHAR(255) NOT NULL,
    report_content TEXT NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE
);
