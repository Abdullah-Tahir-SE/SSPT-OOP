-- Update attendance_records table to new simplified structure

-- 1. Drop old columns
ALTER TABLE attendance_records
DROP COLUMN mid_conducted,
DROP COLUMN mid_attended,
DROP COLUMN final_conducted,
DROP COLUMN final_attended,
DROP COLUMN lab_conducted_mid,
DROP COLUMN lab_attended_mid,
DROP COLUMN lab_conducted_final,
DROP COLUMN lab_attended_final;

-- 2. Add new columns
ALTER TABLE attendance_records
ADD COLUMN theory_conducted INT DEFAULT 0,
ADD COLUMN theory_attended INT DEFAULT 0,
ADD COLUMN lab_conducted INT DEFAULT 0,
ADD COLUMN lab_attended INT DEFAULT 0;
