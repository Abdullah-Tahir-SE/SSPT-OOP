package app;

import util.DatabaseConnection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

public class SchemaUpdater {
    public static void main(String[] args) {
        System.out.println("Running Schema Update...");
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // Drop old columns (ignore errors if they don't exist)
            try {
                stmt.executeUpdate("ALTER TABLE attendance_records DROP COLUMN mid_conducted");
                stmt.executeUpdate("ALTER TABLE attendance_records DROP COLUMN mid_attended");
                stmt.executeUpdate("ALTER TABLE attendance_records DROP COLUMN final_conducted");
                stmt.executeUpdate("ALTER TABLE attendance_records DROP COLUMN final_attended");
                stmt.executeUpdate("ALTER TABLE attendance_records DROP COLUMN lab_conducted_mid");
                stmt.executeUpdate("ALTER TABLE attendance_records DROP COLUMN lab_attended_mid");
                stmt.executeUpdate("ALTER TABLE attendance_records DROP COLUMN lab_conducted_final");
                stmt.executeUpdate("ALTER TABLE attendance_records DROP COLUMN lab_attended_final");
            } catch (Exception e) {
                System.out.println("Note: Could not drop old columns (might already be gone).");
            }

            // Add new columns (ignore errors if they exist)
            try {
                stmt.executeUpdate("ALTER TABLE attendance_records ADD COLUMN theory_conducted INT DEFAULT 0");
                stmt.executeUpdate("ALTER TABLE attendance_records ADD COLUMN theory_attended INT DEFAULT 0");
                stmt.executeUpdate("ALTER TABLE attendance_records ADD COLUMN lab_conducted INT DEFAULT 0");
                stmt.executeUpdate("ALTER TABLE attendance_records ADD COLUMN lab_attended INT DEFAULT 0");
            } catch (Exception e) {
                System.out.println("Note: Could not add new columns (might already exist).");
            }

            // Update for Marks Table (v4)
            try {
                stmt.executeUpdate("ALTER TABLE marks ADD COLUMN total_marks DOUBLE DEFAULT 0");
                stmt.executeUpdate("ALTER TABLE marks ADD COLUMN assessment_label VARCHAR(50)");
                System.out.println("Added columns to marks table.");
            } catch (Exception e) {
                System.out.println("Note: Could not add marks columns (might already exist).");
            }

            // Update for GPA (v5)
            try {
                stmt.executeUpdate("ALTER TABLE course_registrations ADD COLUMN final_grade VARCHAR(5)");
                stmt.executeUpdate("ALTER TABLE course_registrations ADD COLUMN gpa DOUBLE DEFAULT 0.0");
                System.out.println("Added grade/gpa columns to course_registrations.");
            } catch (Exception e) {
                System.out.println("Note: Could not add rules to course_registrations (might already exist).");
            }

            // Update for SGPA (v6)
            try {
                stmt.executeUpdate("ALTER TABLE students ADD COLUMN current_sgpa DOUBLE DEFAULT 0.0");
                System.out.println("Added current_sgpa column to students.");
            } catch (Exception e) {
                System.out.println("Note: Could not add current_sgpa (might already exist).");
            }

            System.out.println("Schema Update Completed (or checked).");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
