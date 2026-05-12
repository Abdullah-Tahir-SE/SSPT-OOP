-- Update students table to include new fields if they don't exist
-- Note: 'semester', 'program_level', and 'degree' are likely already in your table based on previous schema.
-- Run these only if columns are missing.

ALTER TABLE students ADD COLUMN IF NOT EXISTS program_level VARCHAR(50);
ALTER TABLE students ADD COLUMN IF NOT EXISTS degree VARCHAR(100);
ALTER TABLE students ADD COLUMN IF NOT EXISTS semester INT DEFAULT 1;

-- No password handling changes needed in DB if reusing 'password_hash' and 'salt' columns.
-- Verify columns exist:
DESCRIBE students;
