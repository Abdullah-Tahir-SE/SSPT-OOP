-- SQL Update Script for Attendance Records
-- Execute these commands in your database to update the schema

-- 1. Drop old columns
ALTER TABLE attendance_records DROP COLUMN mid_conducted;
ALTER TABLE attendance_records DROP COLUMN mid_attended;
ALTER TABLE attendance_records DROP COLUMN final_conducted;
ALTER TABLE attendance_records DROP COLUMN final_attended;
ALTER TABLE attendance_records DROP COLUMN lab_conducted_mid;
ALTER TABLE attendance_records DROP COLUMN lab_attended_mid;
ALTER TABLE attendance_records DROP COLUMN lab_conducted_final;
ALTER TABLE attendance_records DROP COLUMN lab_attended_final;

-- 2. Add new columns
ALTER TABLE attendance_records ADD COLUMN theory_conducted INT DEFAULT 0;
ALTER TABLE attendance_records ADD COLUMN theory_attended INT DEFAULT 0;
ALTER TABLE attendance_records ADD COLUMN lab_conducted INT DEFAULT 0;
ALTER TABLE attendance_records ADD COLUMN lab_attended INT DEFAULT 0;
