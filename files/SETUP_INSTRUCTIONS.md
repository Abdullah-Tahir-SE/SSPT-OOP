# Smart Semester Progress Tracker - Setup Instructions

## Prerequisites
- Java JDK 11 or higher
- MySQL Server 8.0 or higher
- MySQL Workbench or command-line MySQL client

## Step 1: Database Setup

1. **Start MySQL Server**
   - Ensure MySQL server is running on your machine
   - Default port: 3306

2. **Create Database**
   - Open MySQL Workbench or command-line MySQL client
   - Run the `database_schema.sql` script to create the database and tables:
   ```sql
   mysql -u root -p < database_schema.sql
   ```
   Or open the file in MySQL Workbench and execute it.

3. **Verify Database**
   - Database name: `SmartSemester`
   - Tables created: `students`, `courses`, `course_registrations`, `attendance_records`, `marks`

## Step 2: Configure Database Connection

1. **Update Database Credentials**
   - Open `src/util/Db.java`
   - Update the following variables with your MySQL credentials:
   ```java
   private static final String USER = "root";  // Your MySQL username
   private static final String PASS = "";      // Your MySQL password
   ```

2. **Update Database URL (if needed)**
   - If MySQL is running on a different port or host, update:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/SmartSemester?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
   ```

## Step 3: Add MySQL JDBC Connector to Classpath

The MySQL JDBC connector (`mysql-connector-j-8.1.0.jar`) is already downloaded in the `lib` folder.

### For IntelliJ IDEA:
1. Right-click on the project → Open Module Settings
2. Go to Libraries → Click "+" → Java
3. Select `lib/mysql-connector-j-8.1.0.jar`
4. Click OK

### For Eclipse:
1. Right-click on project → Properties
2. Java Build Path → Libraries → Add External JARs
3. Select `lib/mysql-connector-j-8.1.0.jar`
4. Click OK

### For Command Line Compilation:
```bash
javac -cp "lib/mysql-connector-j-8.1.0.jar" -d out src/**/*.java
java -cp "out:lib/mysql-connector-j-8.1.0.jar" app.Main
```

## Step 4: Run the Application

1. **Compile the project**
   ```bash
   javac -cp "lib/mysql-connector-j-8.1.0.jar" -d out -sourcepath src src/app/Main.java
   ```

2. **Run the application**
   ```bash
   java -cp "out;lib/mysql-connector-j-8.1.0.jar" app.Main
   ```
   (Use `:` instead of `;` on Linux/Mac)

3. **Choose Mode**
   - Option 1: Console Mode (text-based interface)
   - Option 2: GUI Mode (Swing graphical interface)
   - Option 3: Exit

## Features

### Student Management
- Student registration with validation
- Login/authentication system
- Password recovery using key pass
- Profile management

### Course Management
- Add courses to student profile
- Update course information
- Delete courses
- View all registered courses

### Attendance Tracking
- Track mid-term and final attendance
- Track lab attendance (mid and final)
- Calculate attendance percentages
- View attendance statistics

### Marks Management
- Enter marks for different assessment types:
  - Quiz
  - Assignment
  - Mid-term Exam
  - Final Exam
  - Lab Assignment
  - Lab Mid-term
  - Lab Final
- View marks summary
- Calculate GPA

### Reporting
- Generate comprehensive semester reports
- View GPA calculations
- Attendance reports
- Performance analytics

## Database Schema

### Tables:
1. **students** - Student information and authentication
2. **courses** - Available courses
3. **course_registrations** - Links students to courses
4. **attendance_records** - Attendance data per course registration
5. **marks** - Assessment marks per course registration

## Troubleshooting

### Connection Issues
- Verify MySQL server is running: `mysql --version`
- Check MySQL credentials in `Db.java`
- Ensure database `SmartSemester` exists
- Check firewall settings if connecting to remote MySQL

### ClassNotFoundException
- Ensure `mysql-connector-j-8.1.0.jar` is in the classpath
- Verify the JAR file exists in the `lib` folder

### SQL Errors
- Ensure all tables are created by running `database_schema.sql`
- Check MySQL user permissions
- Verify foreign key constraints are properly set up

## Project Structure

```
Final OPP Project - Cursor/
├── src/
│   ├── app/
│   │   ├── Main.java
│   │   └── UI/
│   │       └── DashboardUI.java
│   ├── model/
│   │   ├── Student.java
│   │   ├── Course.java
│   │   ├── CourseRegistration.java
│   │   ├── Mark.java
│   │   ├── AttendanceRecord.java
│   │   └── ...
│   ├── repository/
│   │   ├── DatabaseStudentRepository.java
│   │   ├── DatabaseCourseRepository.java
│   │   └── ...
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── StudentService.java
│   │   └── ...
│   └── util/
│       ├── Db.java
│       └── ...
├── lib/
│   └── mysql-connector-j-8.1.0.jar
├── database_schema.sql
└── SETUP_INSTRUCTIONS.md
```

## Notes

- All data is now persisted in MySQL database
- The application uses prepared statements to prevent SQL injection
- Database connections are properly managed with try-with-resources
- Foreign key constraints ensure data integrity

