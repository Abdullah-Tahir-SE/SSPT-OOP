# 📊 Smart Semester Progress Tracker (SSPT)

> A Java-based OOP desktop application designed for students of **COMSATS University Islamabad, Sahiwal Campus** to track and manage their academic semester progress in real-time.

---

## 🎯 Overview

**Smart Semester Progress Tracker (SSPT)** is a comprehensive student progress tracking system built entirely in **Java** using **OOP principles** and **Java Swing GUI**. All grading logic, GPA/SGPA calculations, and assessment weightages are based on the official academic criteria of **COMSATS University Islamabad, Sahiwal Campus**.

The application supports both **Console Mode** and a **full GUI Mode**, allowing students to:
- Register and manage their academic profile
- Add, update, and delete courses per semester
- Enter marks for all assessment components (Quizzes, Assignments, Mids, Finals, Labs)
- Track attendance with theory and lab separation
- Generate semester reports with GPA/SGPA calculations
- Export progress reports to text files

---

## 🏛️ University Criteria

All calculations and grading logic strictly follow the **COMSATS University Islamabad** academic policies:

| Grade | Percentage Range | Grade Points |
|-------|-----------------|-------------|
| A     | 90 – 100%       | 4.00        |
| A-    | 85 – 89%        | 3.67        |
| B+    | 80 – 84%        | 3.33        |
| B     | 75 – 79%        | 3.00        |
| B-    | 70 – 74%        | 2.67        |
| C+    | 65 – 69%        | 2.33        |
| C     | 60 – 64%        | 2.00        |
| C-    | 55 – 59%        | 1.67        |
| D     | 50 – 54%        | 1.00        |
| F     | Below 50%       | 0.00        |

### Assessment Weightage
- **Theory**: Quizzes, Assignments, Mid-Term, Final Exam
- **Lab** (if applicable): Lab Assignments, Lab Mid, Lab Final
- Credit hours split between Theory and Lab components

---

## 🚀 Features

### 👨‍🎓 Student Features
- **Registration & Login** with secure password hashing (SHA-256)
- **Forgot Password** recovery using a personal KeyPass
- **Profile Management** – Update name, password, or degree program
- **Course Management** – Add (Theory / Theory+Lab), Update, and Delete courses
- **Marks Entry** – Enter marks for all assessment types with percentage auto-calculation
- **Attendance Tracking** – Separate theory and lab attendance with visual indicators
- **Report Generation** – View course-wise results, grades, GPA, and SGPA
- **Export Reports** – Save progress reports as `.txt` files
- **Semester Reset** – Start fresh for a new semester

### 🔐 Admin Features
- Admin login panel
- Admin dashboard for student management
- View student details and course progress

### 🖥️ Dual Mode
- **Console Mode** – Full text-based interface
- **GUI Mode** – Modern Java Swing interface with themed icons and custom dialogs

---

## 🏗️ Project Architecture

```
SSPT OOP/
├── src/
│   ├── app/                          # Application layer
│   │   ├── Main.java                 # Entry point (Console + GUI launcher)
│   │   ├── Admin.java                # Admin functionality
│   │   ├── SchemaUpdater.java        # DB schema migration
│   │   ├── UI/                       # Java Swing GUI components
│   │   │   ├── MainLandingPage.java
│   │   │   ├── StudentLoginUI.java
│   │   │   ├── StudentRegisterUI.java
│   │   │   ├── DashboardUI.java
│   │   │   ├── AdminLoginUI.java
│   │   │   ├── AdminDashboardUI.java
│   │   │   ├── CourseRegistrationPanel.java
│   │   │   ├── MarksEntryUI.java
│   │   │   ├── AttendanceEntryDialog.java
│   │   │   ├── ReportGenerationUI.java
│   │   │   ├── SGPACalculatorDialog.java
│   │   │   ├── GpaResultDialog.java
│   │   │   ├── ForgotPasswordUI.java
│   │   │   ├── ThemeIcons.java
│   │   │   └── ... (other UI components)
│   │   └── utils/
│   ├── model/                        # Data models (POJOs)
│   │   ├── Student.java
│   │   ├── Course.java
│   │   ├── CourseRegistration.java
│   │   ├── CourseResult.java
│   │   ├── Mark.java
│   │   ├── AssessmentType.java
│   │   ├── AttendanceRecord.java
│   │   └── User.java
│   ├── service/                      # Business logic layer
│   │   ├── AuthService.java
│   │   ├── StudentService.java
│   │   ├── MarksCalculator.java
│   │   ├── ReportService.java
│   │   └── AttendanceService.java
│   ├── repository/                   # Data access layer
│   │   ├── StudentRepository.java
│   │   ├── CourseRepository.java
│   │   ├── DatabaseStudentRepository.java
│   │   ├── DatabaseCourseRepository.java
│   │   ├── InMemoryStudentRepository.java
│   │   └── InMemoryCourseRepository.java
│   ├── util/                         # Utility classes
│   │   ├── DatabaseConnection.java
│   │   ├── SchemaInitializer.java
│   │   ├── HashUtil.java
│   │   ├── ConsoleUtil.java
│   │   └── Db.java
│   └── resources/
│       └── images/
├── databadd/                         # Database SQL scripts
│   ├── database_schema.sql
│   ├── CREATE_STUDENT_COURSES.sql
│   ├── Query Project.sql
│   ├── DB_UPDATE.sql
│   └── database_update.sql
├── files/                            # Reports & Documentation
│   ├── Documentation of Smart Semester Progress Tracker.pdf
│   ├── SETUP_INSTRUCTIONS.md
│   ├── DATABASE_CONNECTION_GUIDE.md
│   ├── QUICK_START.md
│   ├── ADMIN_COMMANDS.md
│   ├── PROJECT_IMPROVEMENTS.md
│   └── Student Reports (*.txt)
├── lib/                              # External dependencies (JARs)
│   ├── mysql-connector-j-8.1.0.jar
│   └── mssql-jdbc-13.2.1.jre11.jar
└── .gitignore
```

---

## 🛠️ Tech Stack

| Component       | Technology                  |
|----------------|-----------------------------|
| Language        | Java 11+                   |
| GUI Framework   | Java Swing                 |
| Database        | MySQL / MS SQL Server      |
| JDBC Drivers    | MySQL Connector J 8.1.0, MSSQL JDBC 13.2.1 |
| Architecture    | MVC + Repository Pattern   |
| Security        | SHA-256 Password Hashing   |
| Build System    | IntelliJ IDEA / Manual     |

---

## ⚙️ Setup & Installation

### Prerequisites
- **Java JDK 11** or higher
- **MySQL** or **MS SQL Server** installed and running
- **IntelliJ IDEA** (recommended) or any Java IDE

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/SSPT-OOP.git
   cd SSPT-OOP
   ```

2. **Set up the database**
   - Import the SQL schema from `databadd/database_schema.sql`
   - Run additional migration scripts from `databadd/` if needed

3. **Configure database connection**
   - Update connection details in `src/util/DatabaseConnection.java`
   - Refer to `files/DATABASE_CONNECTION_GUIDE.md` for detailed instructions

4. **Add library JARs to classpath**
   - Add `lib/mysql-connector-j-8.1.0.jar` (for MySQL)
   - Add `lib/mssql-jdbc-13.2.1.jre11.jar` (for MS SQL Server)

5. **Run the application**
   - Open in IntelliJ IDEA → Run `Main.java`
   - Choose **Console Mode** or **GUI Mode** at startup

> 📖 For detailed setup, see [`files/SETUP_INSTRUCTIONS.md`](files/SETUP_INSTRUCTIONS.md) and [`files/QUICK_START.md`](files/QUICK_START.md)

---

## 🎓 OOP Concepts Used

- **Encapsulation** – Private fields with getters/setters in all model classes
- **Inheritance** – Student extends User
- **Polymorphism** – Repository interfaces with multiple implementations (Database & InMemory)
- **Abstraction** – Service layer abstracts business logic from UI
- **Interface Segregation** – Separate `StudentRepository` and `CourseRepository` interfaces
- **MVC Pattern** – Model, Service (Controller), UI (View) separation
- **Repository Pattern** – Data access abstracted through repository interfaces

---

## 👥 Team

| Name | Registration No |
|------|----------------|
| Team Member | FA24-BSE-026 |
| Team Member | FA24-BSE-035 |
| Team Member | FA24-BSE-054 |

**Course:** Object-Oriented Programming (OOP)  
**University:** COMSATS University Islamabad, Sahiwal Campus  
**Semester:** Spring 2025

---

## 📄 License

This project is developed as an academic semester project for COMSATS University Islamabad, Sahiwal Campus.

---

<p align="center">
  Made with ❤️ at COMSATS University Islamabad, Sahiwal Campus
</p>
