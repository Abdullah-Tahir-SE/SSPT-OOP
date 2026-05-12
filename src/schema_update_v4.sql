-- Add new columns to marks table
ALTER TABLE marks ADD COLUMN total_marks DOUBLE DEFAULT 0;
ALTER TABLE marks ADD COLUMN assessment_label VARCHAR(50);

-- If the table doesn't assume default valid structure, we might need to recreate it, 
-- but altering is safer for existing data. The new code will handle nulls/defaults.
