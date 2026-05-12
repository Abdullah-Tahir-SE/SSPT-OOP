# Admin SQL Commands

The **Admin** class performs CRUD operations on student records. The `DatabaseStudentRepository` in the codebase executes these queries using JDBC.

## 1. View All Students
This query fetches all students ordered by their registration number.
```sql
SELECT * FROM students ORDER BY registration_no;
```
*Note: To fetch full details including courses, the application performs additional JOIN queries on `course_registrations`.*

## 2. Update Student
Allows the admin to modify a student's basic information.
```sql
UPDATE students 
SET full_name = ?, 
    program_level = ?, 
    degree = ?, 
    semester = ? 
WHERE registration_no = ?;
```

## 3. Delete Student
Permanently removes a student record. The database schema has `ON DELETE CASCADE` configured on related tables (`course_registrations`, `attendance_records`, `marks`), so deleting a student automatically removes their related data.
```sql
DELETE FROM students WHERE registration_no = ?;
```

## 4. Authentication
The Admin authentication is **hardcoded** in the Java application and does not require a database table.
- **Username**: `abii`
- **Password**: `abii@0827`
