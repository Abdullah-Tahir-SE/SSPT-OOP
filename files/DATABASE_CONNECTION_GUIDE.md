# Database Connection Setup Guide

## Overview

The project now uses a dedicated `DatabaseConnection` class for managing MySQL database connections. This provides better organization and easier maintenance.

## Files Created

1. **`src/util/DatabaseConnection.java`** - Main database connection class
   - Handles MySQL connection management
   - Provides connection testing functionality
   - Centralized configuration

2. **`src/util/DatabaseConnectionTester.java`** - Standalone connection tester
   - Comprehensive database connectivity testing
   - Schema validation
   - Diagnostic information

3. **`src/util/Db.java`** - Legacy wrapper (for backward compatibility)
   - Delegates to DatabaseConnection
   - Maintains compatibility with existing code

## Configuration

### Step 1: Update Database Credentials

Edit `src/util/DatabaseConnection.java`:

```java
private static final String DB_USER = "root";      // Your MySQL username
private static final String DB_PASSWORD = "";      // Your MySQL password
```

You can also modify:
- `DB_HOST` - Default: "localhost"
- `DB_PORT` - Default: "3306"
- `DB_NAME` - Default: "SmartSemester"

## Testing the Connection

### Method 1: Run the Connection Tester (Recommended)

The `DatabaseConnectionTester` is a standalone utility that provides comprehensive testing:

```bash
# Compile
javac -cp "lib\mysql-connector-j-8.1.0.jar" -d out -sourcepath src src\util\DatabaseConnectionTester.java

# Run
java -cp "out;lib\mysql-connector-j-8.1.0.jar" util.DatabaseConnectionTester
```

**What it tests:**
- ✓ Basic database connection
- ✓ MySQL version and driver information
- ✓ Required tables existence (students, courses, course_registrations, attendance_records, marks)
- ✓ Row counts for each table
- ✓ Sample query execution

**Sample Output:**
```
========================================
Database Connection Tester
========================================

Database Configuration:
  Host: localhost
  Port: 3306
  Database: SmartSemester
  User: root
  URL: jdbc:mysql://localhost:3306/SmartSemester?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true

--- Test 1: Basic Connection ---
=== Testing Database Connection ===
Host: localhost
Port: 3306
Database: SmartSemester
User: root
Attempting connection...
✓ Connection successful!
✓ Database is accessible and valid

--- Test 2: Database Schema ---
Database: SmartSemester
MySQL Version: 8.0.33
Driver: MySQL Connector/J 8.1.0

Checking required tables:
  ✓ Table 'students' exists
    Rows: 0
  ✓ Table 'courses' exists
    Rows: 5
  ✓ Table 'course_registrations' exists
    Rows: 0
  ✓ Table 'attendance_records' exists
    Rows: 0
  ✓ Table 'marks' exists
    Rows: 0

✓ Schema test PASSED - All required tables exist

--- Test 3: Sample Query ---
✓ Sample query executed successfully
  Total courses in database: 5
✓ Database contains sample data

========================================
All tests completed!
========================================
```

### Method 2: Test via Main Application

The main application automatically tests the connection on startup:

```bash
java -cp "out;lib\mysql-connector-j-8.1.0.jar" app.Main
```

If connection fails, you'll see detailed error messages and troubleshooting steps.

## Troubleshooting

### Connection Failed

**Error:** `Connection failed!`

**Solutions:**
1. **Check MySQL Server Status**
   ```bash
   # Windows
   net start MySQL80
   
   # Linux/Mac
   sudo systemctl status mysql
   ```

2. **Verify Database Exists**
   ```sql
   mysql -u root -p
   SHOW DATABASES;
   ```
   If `SmartSemester` doesn't exist, run `database_schema.sql`

3. **Check Credentials**
   - Verify username and password in `DatabaseConnection.java`
   - Test login manually:
     ```bash
     mysql -u root -p
     ```

4. **Check Port**
   - Default MySQL port is 3306
   - Verify MySQL is listening:
     ```bash
     # Windows
     netstat -an | findstr 3306
     
     # Linux/Mac
     netstat -an | grep 3306
     ```

### Tables Missing

**Error:** `Table 'students' NOT FOUND`

**Solution:**
Run the database schema script:
```bash
mysql -u root -p < database_schema.sql
```

### Driver Not Found

**Error:** `MySQL JDBC Driver not found!`

**Solutions:**
1. Verify JAR file exists: `lib/mysql-connector-j-8.1.0.jar`
2. Add JAR to classpath when compiling/running
3. In IDE: Add JAR to project libraries

## Usage in Code

### Getting a Connection

```java
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

try (Connection conn = DatabaseConnection.getConnection()) {
    // Use connection here
    // It will be automatically closed when exiting try block
} catch (SQLException e) {
    System.err.println("Database error: " + e.getMessage());
}
```

### Testing Connection

```java
if (DatabaseConnection.testConnection()) {
    System.out.println("Database is ready!");
} else {
    System.err.println("Database connection failed!");
}
```

### Getting Connection Info

```java
String info = DatabaseConnection.getConnectionInfo();
System.out.println(info);
```

## Architecture

```
DatabaseConnection (Main Class)
    ├── getConnection() - Get new connection
    ├── testConnection() - Test connectivity
    ├── closeConnection() - Close connection safely
    └── getConnectionInfo() - Get configuration details

DatabaseConnectionTester (Standalone Utility)
    ├── testBasicConnection() - Basic connectivity test
    ├── testDatabaseSchema() - Schema validation
    └── testSampleQuery() - Query execution test

Db (Legacy Wrapper)
    └── Delegates to DatabaseConnection for compatibility
```

## Best Practices

1. **Always use try-with-resources** for connections:
   ```java
   try (Connection conn = DatabaseConnection.getConnection()) {
       // Use connection
   }
   ```

2. **Test connection before running application** using `DatabaseConnectionTester`

3. **Update credentials** in `DatabaseConnection.java` only, not in multiple places

4. **Use PreparedStatements** for all queries to prevent SQL injection

5. **Handle SQLException** properly with meaningful error messages

## Next Steps

1. ✅ Update credentials in `DatabaseConnection.java`
2. ✅ Run `database_schema.sql` to create database
3. ✅ Test connection using `DatabaseConnectionTester`
4. ✅ Run main application

Your database connection is now properly organized and ready to use! 🎉

