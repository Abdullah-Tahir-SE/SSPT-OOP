-- Add grade and GPA columns to course_registrations
ALTER TABLE course_registrations ADD COLUMN final_grade VARCHAR(5);
ALTER TABLE course_registrations ADD COLUMN gpa DOUBLE DEFAULT 0.0;
