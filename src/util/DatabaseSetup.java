package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void checkAndMigrate() {
        System.out.println("Checking database schema...");
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();

            // Check current_sgpa
            if (!columnExists(conn, "students", "current_sgpa")) {
                System.out.println("Adding missing column: current_sgpa");
                stmt.execute("ALTER TABLE students ADD COLUMN current_sgpa DOUBLE DEFAULT 0.0");
            }

            // Check total_qp
            if (!columnExists(conn, "students", "total_qp")) {
                System.out.println("Adding missing column: total_qp");
                stmt.execute("ALTER TABLE students ADD COLUMN total_qp DOUBLE DEFAULT 0.0");
            }

            // Check semester_grade
            if (!columnExists(conn, "students", "semester_grade")) {
                System.out.println("Adding missing column: semester_grade");
                stmt.execute("ALTER TABLE students ADD COLUMN semester_grade VARCHAR(10) DEFAULT 'N/A'");
            }

            // Check sem_credits
            if (!columnExists(conn, "students", "sem_credits")) {
                System.out.println("Adding missing column: sem_credits");
                stmt.execute("ALTER TABLE students ADD COLUMN sem_credits INT DEFAULT 0");
            }

            System.out.println("Database schema check passed.");

        } catch (SQLException e) {
            System.err.println("Database Migration Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName);
        return rs.next();
    }
}
