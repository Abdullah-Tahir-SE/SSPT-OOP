-- Add 'has_lab' column to courses table if it doesn't exist
-- Note: 'lab_credits' and 'is_fyp' are also assumed to be handled or existing, 
-- but this script specifically addresses the user's request for Lab Status.

ALTER TABLE courses ADD COLUMN IF NOT EXISTS has_lab BOOLEAN DEFAULT FALSE;
