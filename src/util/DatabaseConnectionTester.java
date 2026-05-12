package util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DatabaseConnectionTester - Standalone utility to test database connection
 * This class provides comprehensive testing of database connectivity and schema.
 * 
 * Usage: Run this class directly to test your database setup
 */
public class DatabaseConnectionTester {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Database Connection Tester");
        System.out.println("========================================");
        System.out.println();
        
        // Display configuration
        System.out.println(DatabaseConnection.getConnectionInfo());
        System.out.println();
        
        // Test basic connection
        if (!testBasicConnection()) {
            System.exit(1);
        }
        
        System.out.println();
        
        // Test database schema
        testDatabaseSchema();
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("All tests completed!");
        System.out.println("========================================");
    }
    
    /**
     * Tests basic database connection
     */
    private static boolean testBasicConnection() {
        System.out.println("--- Test 1: Basic Connection ---");
        
        if (!DatabaseConnection.testConnection()) {
            System.err.println("✗ Basic connection test FAILED");
            return false;
        }
        
        System.out.println("✓ Basic connection test PASSED");
        return true;
    }
    
    /**
     * Tests database schema and tables
     */
    private static void testDatabaseSchema() {
        System.out.println("--- Test 2: Database Schema ---");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Get database name
            String databaseName = DatabaseConnection.getDatabaseName();
            System.out.println("Database: " + databaseName);
            
            // Get MySQL version
            String dbVersion = metaData.getDatabaseProductVersion();
            System.out.println("MySQL Version: " + dbVersion);
            System.out.println("Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
            System.out.println();
            
            // Check required tables
            String[] requiredTables = {
                "students",
                "courses",
                "course_registrations",
                "attendance_records",
                "marks"
            };
            
            System.out.println("Checking required tables:");
            boolean allTablesExist = true;
            
            for (String tableName : requiredTables) {
                if (tableExists(conn, tableName)) {
                    System.out.println("  ✓ Table '" + tableName + "' exists");
                    
                    // Get row count
                    int rowCount = getTableRowCount(conn, tableName);
                    System.out.println("    Rows: " + rowCount);
                } else {
                    System.out.println("  ✗ Table '" + tableName + "' NOT FOUND");
                    allTablesExist = false;
                }
            }
            
            if (!allTablesExist) {
                System.err.println("\n✗ Schema test FAILED - Some tables are missing!");
                System.err.println("Please run database_schema.sql to create the required tables.");
            } else {
                System.out.println("\n✓ Schema test PASSED - All required tables exist");
            }
            
            // Test sample query
            System.out.println("\n--- Test 3: Sample Query ---");
            testSampleQuery(conn);
            
        } catch (SQLException e) {
            System.err.println("✗ Schema test FAILED");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Checks if a table exists in the database
     */
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }
    
    /**
     * Gets the row count for a table
     */
    private static int getTableRowCount(Connection conn, String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM " + tableName;
            try (java.sql.Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            // Table might not exist or other error
            return -1;
        }
        return -1;
    }
    
    /**
     * Tests a sample query to verify database is working
     */
    private static void testSampleQuery(Connection conn) {
        try {
            // Test query: Get all courses
            String sql = "SELECT COUNT(*) as total FROM courses";
            try (java.sql.Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                if (rs.next()) {
                    int totalCourses = rs.getInt("total");
                    System.out.println("✓ Sample query executed successfully");
                    System.out.println("  Total courses in database: " + totalCourses);
                    
                    if (totalCourses > 0) {
                        System.out.println("✓ Database contains sample data");
                    } else {
                        System.out.println("  Note: Database is empty (no courses yet)");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Sample query FAILED");
            System.err.println("Error: " + e.getMessage());
        }
    }
}

