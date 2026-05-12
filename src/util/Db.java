package util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Db - Legacy wrapper class for backward compatibility
 * This class now delegates to DatabaseConnection for better organization.
 * 
 * @deprecated Consider using DatabaseConnection directly for new code
 */
public class Db {

    /**
     * Gets a database connection
     * Delegates to DatabaseConnection.getConnection()
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    /**
     * Tests the database connection
     * Delegates to DatabaseConnection.testConnection()
     * 
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        return DatabaseConnection.testConnection();
    }
}
