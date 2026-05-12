-- Fix for Course Cards Disappearing (Unknown Column Error)
-- Run this entire script in MySQL Workbench

-- 1. Drop the old columns that are no longer needed
-- Note: If some columns don't exist, these lines might fail. You can ignore those specific errors or run them one by one.
ALTER TABLE attendance_records DROP COLUMN mid_conducted;
ALTER TABLE attendance_records DROP COLUMN mid_attended;
ALTER TABLE attendance_records DROP COLUMN final_conducted;
ALTER TABLE attendance_records DROP COLUMN final_attended;

-- If your DB had camelCase versions, you might need these instead (commented out by default):
-- ALTER TABLE attendance_records DROP COLUMN midConducted;
-- ALTER TABLE attendance_records DROP COLUMN midAttended;

-- 2. Add the NEW required columns with default values (0)
ALTER TABLE attendance_records ADD COLUMN theory_conducted INT DEFAULT 0;
ALTER TABLE attendance_records ADD COLUMN theory_attended INT DEFAULT 0;
ALTER TABLE attendance_records ADD COLUMN lab_conducted INT DEFAULT 0;
ALTER TABLE attendance_records ADD COLUMN lab_attended INT DEFAULT 0;
