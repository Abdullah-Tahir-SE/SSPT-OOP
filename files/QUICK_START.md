# Quick Start Guide

## 1. Database Setup (One-time)

```bash
# Login to MySQL
mysql -u root -p

# Create database and tables
source database_schema.sql
# OR
mysql -u root -p < database_schema.sql
```

## 2. Update Database Credentials

Edit `src/util/Db.java`:
```java
private static final String USER = "root";     // Your MySQL username
private static final String PASS = "yourpass"; // Your MySQL password
```

## 3. Compile & Run

### Windows (PowerShell):
```powershell
# Compile
javac -cp "lib\mysql-connector-j-8.1.0.jar" -d out -sourcepath src src\app\Main.java

# Run
java -cp "out;lib\mysql-connector-j-8.1.0.jar" app.Main
```

### Linux/Mac:
```bash
# Compile
javac -cp "lib/mysql-connector-j-8.1.0.jar" -d out -sourcepath src src/app/Main.java

# Run
java -cp "out:lib/mysql-connector-j-8.1.0.jar" app.Main
```

## 4. Test Registration

1. Choose **Console Mode** (Option 1)
2. Select **Register** (Option 1)
3. Enter student details:
   - Name: Your Name
   - Registration No: FA24-BSE-001 (format: XX##-XXX-###)
   - Password: Your password
   - Key Pass: Recovery key
   - Program Level: Undergraduate
   - Degree: BSE
   - Semester: 1

## 5. Features Available

✅ **Student Management**: Register, Login, Profile Update  
✅ **Course Management**: Add, Update, Delete Courses  
✅ **Attendance Tracking**: Mid-term, Final, Lab Attendance  
✅ **Marks Entry**: Quiz, Assignment, Exams, Lab Assessments  
✅ **Reports**: GPA Calculation, Semester Reports  
✅ **Database Persistence**: All data stored in MySQL  

## Troubleshooting

**Connection Error?**
- Check MySQL is running: `mysql --version`
- Verify credentials in `Db.java`
- Ensure database exists: `SHOW DATABASES;`

**ClassNotFoundException?**
- Verify JAR is in classpath
- Check `lib/mysql-connector-j-8.1.0.jar` exists

**SQL Errors?**
- Run `database_schema.sql` again
- Check table exists: `USE SmartSemester; SHOW TABLES;`

