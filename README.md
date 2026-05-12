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
| Abdullah Tahir | FA24-BSE-026 |
| Maryam Zahid   | FA24-BSE-035 |
| M Danish Fraz  | FA24-BSE-054 |

**Course:** Object-Oriented Programming (OOP)  
**University:** COMSATS University Islamabad, Sahiwal Campus  
**Semester:** Spring 2025

---

## 📄 License

This project is developed as an academic semester project for COMSATS University Islamabad, Sahiwal Campus.

## GUI 
<img width="941" height="471" alt="WhatsApp Image 2026-05-12 at 5 04 55 PM" src="https://github.com/user-attachments/assets/b5f4a004-542c-40eb-8d2e-afebf29998cf" />
<img width="941" height="617" alt="WhatsApp Image 2026-05-12 at 5 05 11 PM" src="https://github.com/user-attachments/assets/e1e0746b-cef9-4758-af52-72de4657f3bb" />
<img width="933" height="625" alt="WhatsApp Image 2026-05-12 at 5 05 20 PM" src="https://github.com/user-attachments/assets/457b7a38-cda2-4b19-914a-1f00e6fb881b" />
<img width="941" height="495" alt="WhatsApp Image 2026-05-12 at 5 05 30 PM" src="https://github.com/user-attachments/assets/1546c68a-3781-426c-81e8-99d032d61519" />
<img width="940" height="496" alt="WhatsApp Image 2026-05-12 at 5 05 38 PM" src="https://github.com/user-attachments/assets/9cad6b5d-1f22-4055-bffc-a2a7a397efba" />
<img width="940" height="869" alt="WhatsApp Image 2026-05-12 at 5 05 47 PM" src="https://github.com/user-attachments/assets/10653f85-e7a7-44ae-b291-7f0e27acb472" />
<img width="941" height="495" alt="WhatsApp Image 2026-05-12 at 5 06 12 PM" src="https://github.com/user-attachments/assets/3b914b59-2d73-48c2-93bf-e09588462000" />
<img width="941" height="637" alt="WhatsApp Image 2026-05-12 at 5 06 05 PM" src="https://github.com/user-attachments/assets/456a3381-6599-4a67-8bb8-4019cd6008ab" />
<img width="926" height="484" alt="WhatsApp Image 2026-05-12 at 5 06 19 PM" src="https://github.com/user-attachments/assets/58379be7-08c7-4691-b262-f9a590f9598d" />
<img width="940" height="721" alt="WhatsApp Image 2026-05-12 at 5 06 51 PM" src="https://github.com/user-attachments/assets/01858af4-e5c1-4948-a93d-cb343c1c0525" />
<img width="898" height="585" alt="WhatsApp Image 2026-05-12 at 5 07 02 PM" src="https://github.com/user-attachments/assets/223cacba-e62d-4961-a890-d65594229626" />
<img width="943" height="729" alt="WhatsApp Image 2026-05-12 at 5 05 56 PM" src="https://github.com/user-attachments/assets/dcb29ac8-cf1e-4d9c-a56e-bfce623dc4d6" />
<img width="905" height="497" alt="WhatsApp Image 2026-05-12 at 5 07 26 PM" src="https://github.com/user-attachments/assets/f4fbd5ea-fe0d-4f0e-9a0d-a8db2cbf7acb" />
<img width="949" height="635" alt="WhatsApp Image 2026-05-12 at 5 07 42 PM" src="https://github.com/user-attachments/assets/4935e318-9937-4f0e-beb5-25186ff78626" />
<img width="941" height="494" alt="WhatsApp Image 2026-05-12 at 5 07 51 PM" src="https://github.com/user-attachments/assets/90f5adb2-b51a-44e4-b958-510fe0b96fe0" />



<p align="center">
  Made with ❤️ at COMSATS University Islamabad, Sahiwal Campus
</p>
