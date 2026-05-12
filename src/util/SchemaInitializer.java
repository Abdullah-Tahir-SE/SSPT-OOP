package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {

    public static void initialize() {
        System.out.println("Checking database schema...");
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // Create student_reports table if it doesn't exist
            String sqlToken = "CREATE TABLE IF NOT EXISTS student_reports (" +
                    "report_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "student_id INT NOT NULL, " +
                    "report_title VARCHAR(255) NOT NULL, " +
                    "report_content TEXT NOT NULL, " +
                    "generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE" +
                    ")";
            stmt.execute(sqlToken);

            // Check and Add Missing Columns for SGPA calculation
            checkAndAddColumn(conn, "students", "current_sgpa", "DOUBLE DEFAULT 0.0");
            checkAndAddColumn(conn, "students", "total_qp", "DOUBLE DEFAULT 0.0");
            checkAndAddColumn(conn, "students", "semester_grade", "VARCHAR(10) DEFAULT 'N/A'");
            checkAndAddColumn(conn, "students", "sem_credits", "INT DEFAULT 0");

            // Check and Add Missing Columns for Course Registrations (Grades/GPA)
            checkAndAddColumn(conn, "course_registrations", "final_grade", "VARCHAR(5) DEFAULT NULL");
            checkAndAddColumn(conn, "course_registrations", "gpa", "DOUBLE DEFAULT 0.0");

            System.out.println("Schema check passed: 'student_reports' and 'students' columns verified.");

        } catch (SQLException e) {
            System.err.println("Schema Initialization Failed!");
            e.printStackTrace();
        }
    }

    private static void checkAndAddColumn(Connection conn, String tableName, String columnName, String definition)
            throws SQLException {
        java.sql.ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName);
        if (!rs.next()) {
            System.out.println("Adding missing column '" + columnName + "' to " + tableName);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
            }
        }
    }
}
