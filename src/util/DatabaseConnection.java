package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Handles MySQL database connection management
 * This class provides a centralized way to manage database connections
 * with proper error handling and configuration.
 */
public class DatabaseConnection {
    
    // Database configuration constants
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "SmartSemester";
    private static final String DB_USER = "root";  // Change to your MySQL username
    private static final String DB_PASSWORD = "Abii@0827";   // Change to your MySQL password
    
    // Connection URL with parameters
    private static final String CONNECTION_URL = 
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + 
        "?useSSL=false" +
        "&allowPublicKeyRetrieval=true" +
        "&serverTimezone=UTC" +
        "&autoReconnect=true";
    
    // Static block to load MySQL JDBC Driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found!");
            System.err.println("Please ensure mysql-connector-j-8.1.0.jar is in the lib folder and added to classpath.");
            System.err.println("Error details: " + e.getMessage());
        }
    }
    
    /**
     * Gets a new database connection
     * 
     * @return Connection object if successful
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(CONNECTION_URL, DB_USER, DB_PASSWORD);
            if (conn != null && !conn.isClosed()) {
                return conn;
            } else {
                throw new SQLException("Failed to establish database connection.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Tests the database connection
     * 
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        System.out.println("\n=== Testing Database Connection ===");
        System.out.println("Host: " + DB_HOST);
        System.out.println("Port: " + DB_PORT);
        System.out.println("Database: " + DB_NAME);
        System.out.println("User: " + DB_USER);
        System.out.println("Attempting connection...");
        
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                // Test query to verify database is accessible
                boolean isValid = conn.isValid(5); // 5 second timeout
                
                if (isValid) {
                    System.out.println("✓ Connection successful!");
                    System.out.println("✓ Database is accessible and valid");
                    System.out.println("✓ Connection URL: " + CONNECTION_URL);
                    return true;
                } else {
                    System.err.println("✗ Connection established but validation failed.");
                    return false;
                }
            } else {
                System.err.println("✗ Connection is null or closed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("\n✗ Connection failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nTroubleshooting steps:");
            System.err.println("1. Verify MySQL server is running");
            System.err.println("2. Check if database '" + DB_NAME + "' exists");
            System.err.println("3. Verify username and password in DatabaseConnection.java");
            System.err.println("4. Ensure MySQL is listening on port " + DB_PORT);
            System.err.println("5. Check firewall settings if connecting remotely");
            return false;
        }
    }
    
    /**
     * Closes a database connection safely
     * 
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("Database connection closed successfully.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Gets database configuration information
     * 
     * @return String with database configuration details
     */
    public static String getConnectionInfo() {
        return String.format(
            "Database Configuration:\n" +
            "  Host: %s\n" +
            "  Port: %s\n" +
            "  Database: %s\n" +
            "  User: %s\n" +
            "  URL: %s",
            DB_HOST, DB_PORT, DB_NAME, DB_USER, CONNECTION_URL
        );
    }
    
    /**
     * Gets the database name
     */
    public static String getDatabaseName() {
        return DB_NAME;
    }
    
    /**
     * Gets the database user
     */
    public static String getDatabaseUser() {
        return DB_USER;
    }
}

