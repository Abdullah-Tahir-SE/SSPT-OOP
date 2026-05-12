package app;

import model.AssessmentType;
import model.Course;
import model.CourseRegistration;
import model.Mark;
import model.Student;
import service.AuthService;
import service.ReportService;
import service.StudentService;
import service.AttendanceService;
import util.ConsoleUtil;
import app.UI.MainLandingPage;
import javax.swing.SwingUtilities;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Ensure database schema is correct
        util.SchemaInitializer.initialize();

        // Instantiate Services
        var studentRepo = new repository.DatabaseStudentRepository();
        var courseRepo = new repository.DatabaseCourseRepository();

        AuthService authService = new AuthService(studentRepo);
        StudentService studentService = new StudentService(studentRepo, courseRepo);
        ReportService reportService = new ReportService();
        AttendanceService attendanceService = new AttendanceService();

        // Mode Selection
        System.out.println("Select Mode:");
        System.out.println("1. Console Mode");
        System.out.println("2. GUI Mode");
        int mode = util.ConsoleUtil.readInt("Choose: ");

        if (mode == 2) {
            // GUI Launch
            SwingUtilities.invokeLater(() -> {
                new MainLandingPage().setVisible(true);
            });
        } else {
            // Console Launch
            runConsoleMode(authService, studentService, attendanceService, reportService);
        }
    }

    private static void runConsoleMode(AuthService authService, StudentService studentService,
            AttendanceService attendanceService, ReportService reportService) {
        System.out.println("SMART SEMESTER STUDENT PROGRESS TRACKER");
        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Forgot Password");
            System.out.println("4. Exit");
            int choice = util.ConsoleUtil.readInt("Choose: ");

            switch (choice) {
                case 1:
                    Student s = handleLogin(authService);
                    if (s != null) {
                        handleDashboard(s, studentService, attendanceService, reportService, authService);
                    }
                    break;
                case 2:
                    handleRegister(authService);
                    break;
                case 3:
                    handleForgotPassword(authService);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // -----------------------------------------------------------
    // LOGIN
    // -----------------------------------------------------------
    private static Student handleLogin(AuthService auth) {
        System.out.println("\n--- LOGIN ---");
        String regNo = ConsoleUtil.readLine("Registration No: ");
        String pwd = ConsoleUtil.readLine("Password: ");

        Student s = auth.login(regNo, pwd);
        if (s != null) {
            System.out.println("Login Successful! Welcome " + s.getFullName());
            return s;
        } else {
            System.out.println("Invalid credentials.");
            return null;
        }
    }

    // -----------------------------------------------------------
    // REGISTER
    // -----------------------------------------------------------
    private static void handleRegister(AuthService auth) {
        System.out.println("\n--- REGISTER ---");
        String name = ConsoleUtil.readLine("Full Name: ");
        String regNo = ConsoleUtil.readLine("Registration No (Unique): ");
        String pwd = ConsoleUtil.readLine("Password: ");
        String key = ConsoleUtil.readLine("KeyPass (for recovery): ");

        // Program Level Selection
        String level = chooseProgramLevel();

        // Degree Selection (Dynamic based on Level)
        String degree = chooseDegree(level);

        // Semester Input
        int semester = 1;
        while (true) {
            semester = ConsoleUtil.readInt("Current Semester (1-8): ");
            if (semester >= 1 && semester <= 8)
                break;
            System.out.println("Invalid semester. Must be 1-8.");
        }

        Student newStudent = auth.register(name, regNo, pwd, key, level, degree, semester);

        if (newStudent != null)
            System.out.println("Registration Successful! Please Login.");
        else
            System.out.println("Registration Failed. RegNo might exist.");
    }

    private static String chooseProgramLevel() {
        System.out.println("\nSelect Program Level:");
        System.out.println("1. Bachelor");
        System.out.println("2. Master (MPhil)");
        System.out.println("3. PhD");

        int choice;
        while (true) {
            choice = ConsoleUtil.readInt("Choose (1-3): ");
            if (choice >= 1 && choice <= 3)
                break;
            System.out.println("Invalid choice.");
        }

        if (choice == 1)
            return "Bachelor";
        if (choice == 2)
            return "Master";
        return "PhD";
    }

    private static String chooseDegree(String level) {
        String[] bachelor = {
                "Software Engineering", "Computer Science", "Information Technology",
                "Artificial Intelligence", "BioTechnology", "Mechanical Engineering",
                "Electrical Engineering", "Civil Engineering", "Islamiat", "Mathematics"
        };

        String[] master = {
                "MS Computer Science", "MS Artificial Intelligence", "MS Physics",
                "MS Mathematics", "MS Chemistry", "MS Information Technology"
        };

        String[] phd = {
                "PhD in Computer Science", "PhD in Artificial Intelligence",
                "PhD in Mathematics", "PhD in Physics"
        };

        String[] list;

        if (level.equalsIgnoreCase("Master"))
            list = master;
        else if (level.equalsIgnoreCase("PhD"))
            list = phd;
        else
            list = bachelor;

        System.out.println("\nSelect your Degree for " + level + ":");
        for (int i = 0; i < list.length; i++) {
            System.out.println((i + 1) + ". " + list[i]);
        }

        int choice;
        while (true) {
            choice = ConsoleUtil.readInt("Choose (1-" + list.length + "): ");
            if (choice >= 1 && choice <= list.length)
                break;
            System.out.println("Invalid choice. Try again.");
        }

        return list[choice - 1];
    }

    // -----------------------------------------------------------
    // FORGOT PASSWORD
    // -----------------------------------------------------------
    private static void handleForgotPassword(AuthService auth) {
        System.out.println("\n--- Forgot Password ---");
        String regNo = ConsoleUtil.readLine("Enter Registration Number: ");
        String key = ConsoleUtil.readLine("Enter KeyPass: ");
        String newPwd = ConsoleUtil.readLine("Enter NEW Password: ");
        String newPwd2 = ConsoleUtil.readLine("Confirm NEW Password: ");

        if (!newPwd.equals(newPwd2)) {
            System.out.println("Passwords do not match.");
            return;
        }

        boolean ok = auth.resetPassword(regNo, key, newPwd);
        if (ok)
            System.out.println("Password reset successful.");
        else
            System.out.println("Reset failed – incorrect key or reg No.");
    }

    // -----------------------------------------------------------
    // DASHBOARD
    // -----------------------------------------------------------
    private static void handleDashboard(Student s, StudentService studentService,
            AttendanceService attendanceService,
            ReportService reportService,
            AuthService authService) {
        while (true) {
            System.out.println("\n--- DASHBOARD (" + s.getFullName() + ") ---");
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Add Course");
            System.out.println("4. Update Course");
            System.out.println("5. Delete Course");
            System.out.println("6. Attendance");
            System.out.println("7. Marks Entry");
            System.out.println("8. Generate Report");
            System.out.println("9. Logout");
            System.out.println("10. Reset Semester");

            int c = ConsoleUtil.readInt("Choose: ");

            switch (c) {
                case 1:
                    studentService.viewProfile(s);
                    break;
                case 2:
                    updateProfile(s, authService);
                    break;
                case 3:
                    addCourse(s, studentService);
                    break;
                case 4:
                    updateCourse(s, studentService);
                    break;
                case 5:
                    deleteCourse(s, studentService);
                    break;
                case 6:
                    attendanceMenu(s, attendanceService);
                    break;
                case 7:
                    marksMenu(s, studentService, reportService);
                    break;
                case 8:
                    reportMenu(s, reportService);
                    break;
                case 9:
                    return;
                case 10:
                    semesterResetMenu(s, studentService);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // -----------------------------------------------------------
    // PROFILE UPDATE
    // -----------------------------------------------------------
    private static void updateProfile(Student s, AuthService authService) {
        System.out.println("\n--- Update Profile ---");
        System.out.println("1. Update Name");
        System.out.println("2. Change Password");
        System.out.println("3. Change Degree");
        System.out.println("4. Back");
        int op = ConsoleUtil.readInt("Choose: ");

        if (op == 1) {
            String newName = ConsoleUtil.readLine("Enter new full name: ");
            s.setFullName(newName);
            System.out.println("Name updated.");
        } else if (op == 2) {
            String oldPwd = ConsoleUtil.readLine("Enter current password: ");
            Student verify = authService.login(s.getRegistrationNo(), oldPwd);
            if (verify == null) {
                System.out.println("Incorrect current password.");
                return;
            }
            String newPwd = ConsoleUtil.readLine("Enter new password: ");
            String newPwd2 = ConsoleUtil.readLine("Confirm new password: ");
            if (!newPwd.equals(newPwd2)) {
                System.out.println("Passwords do not match.");
                return;
            }
            boolean ok = authService.resetPassword(s.getRegistrationNo(), s.getKeyPass(), newPwd);
            if (ok)
                System.out.println("Password changed.");
            else
                System.out.println("Failed to change password.");
        } else if (op == 3) {
            String d = chooseDegree(s.getProgramLevel());
            s.setDegree(d);
            System.out.println("Degree updated.");
        }
    }

    // -----------------------------------------------------------
    // ADD COURSE
    // -----------------------------------------------------------
    private static void addCourse(Student s, StudentService studentService) {
        System.out.println("\n--- ADD COURSE ---");
        System.out.println("Registered Hours: " + s.getTotalCredits() + "/21");
        System.out.println("Course Type:");
        System.out.println("1. Theory only");
        System.out.println("2. Theory + Lab");
        int type = ConsoleUtil.readInt("Choose (1/2): ");

        String code = ConsoleUtil.readLine("Course Code (E.g. CSC120) : ");
        String title = ConsoleUtil.readLine("Course Title (E.g. OOP): ");
        boolean ok = false;

        if (type == 1) {
            int credits = ConsoleUtil.readInt("Theory Credit Hours (1-8): ");
            if (credits < 1 || credits > 8) {
                System.out.println("Invalid credit hours. Must be 1-8.");
                return;
            }
            ok = studentService.addCourseToStudent(s, code, title, credits, 0);
        } else if (type == 2) {
            int th = ConsoleUtil.readInt("Theory Credit Hours (E.g, 2 or 3): ");
            int lb = ConsoleUtil.readInt("Lab Credit Hours (E.g, 1 or 0): ");
            if (th < 1 || th > 8 || lb < 0 || lb > 8 || (th + lb) > 8) {
                System.out.println("Invalid credit split. Each part must be reasonable and total <= 8.");
                return;
            }
            ok = studentService.addCourseToStudent(s, code, title, th, lb);
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (ok) {
            System.out.println("Course added.");
            Student fresh = studentService.getLatestStudent(s.getRegistrationNo());
            if (fresh != null)
                s.setRegistrations(fresh.getRegistrations());

            CourseRegistration last = s.getRegistrations().get(s.getRegistrations().size() - 1);
            Course c = last.getCourse();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String ts = (last.getRegisteredAt() != null) ? last.getRegisteredAt().format(fmt) : "N/A";

            if (c.isHasLab()) {
                System.out.printf("Registered: %s - %s (Theory: %d, Lab: %d) on %s\n",
                        c.getCourseCode(), c.getTitle(), c.getTheoryCredits(), c.getLabCredits(), ts);
            } else {
                System.out.printf("Registered: %s - %s (%d credits) on %s\n",
                        c.getCourseCode(), c.getTitle(), c.getCreditHours(), ts);
            }
        } else {
            System.out.println("Failed (exceeds limits or invalid).");
        }
    }

    private static void updateCourse(Student s, StudentService service) {
        if (s.getRegistrations().isEmpty()) {
            System.out.println("No courses.");
            return;
        }
        System.out.println("\n--- UPDATE COURSE ---");
        List<CourseRegistration> regs = s.getRegistrations();
        for (int i = 0; i < regs.size(); i++) {
            Course c = regs.get(i).getCourse();
            System.out.println((i + 1) + ". " + c.getCourseCode() + " - " + c.getTitle());
        }
        int choice = ConsoleUtil.readInt("Enter number of course to update: ");
        if (choice < 1 || choice > regs.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        String oldCode = regs.get(choice - 1).getCourse().getCourseCode();
        String newC = ConsoleUtil.readLine("New Course Code: ");
        String newT = ConsoleUtil.readLine("New Title: ");
        int newCr = ConsoleUtil.readInt("New Credits: ");
        boolean ok = service.updateCourse(s, oldCode, newC, newT, newCr);
        if (ok)
            System.out.println("Course updated.");
        else
            System.out.println("Update failed.");
    }

    private static void deleteCourse(Student s, StudentService service) {
        if (s.getRegistrations().isEmpty()) {
            System.out.println("No courses.");
            return;
        }
        System.out.println("\n--- DELETE COURSE ---");
        List<CourseRegistration> regs = s.getRegistrations();
        for (int i = 0; i < regs.size(); i++) {
            Course c = regs.get(i).getCourse();
            System.out.println((i + 1) + ". " + c.getCourseCode() + " - " + c.getTitle());
        }
        int choice = ConsoleUtil.readInt("Enter number of course to delete: ");
        if (choice < 1 || choice > regs.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        Course selected = regs.get(choice - 1).getCourse();
        String delCode = selected.getCourseCode();
        String conf = ConsoleUtil.readLine("Are you sure you want to delete this course? (yes/y/no): ");
        String c = conf.trim().toLowerCase();
        if (!(c.equals("yes") || c.equals("y"))) {
            System.out.println("Cancelled.");
            return;
        }
        boolean ok = service.deleteCourse(s, delCode);
        if (ok)
            System.out.println("Deleted.");
        else
            System.out.println("Course not found.");
    }

    // -----------------------------------------------------------
    // ATTENDANCE MENU
    // -----------------------------------------------------------
    private static void attendanceMenu(Student s, AttendanceService service) {
        if (s.getRegistrations().isEmpty()) {
            System.out.println("No courses.");
            return;
        }

        System.out.println("\n--- ATTENDANCE MENU ---");
        for (int i = 0; i < s.getRegistrations().size(); i++) {
            CourseRegistration r = s.getRegistrations().get(i);
            System.out.println((i + 1) + ". " + r.getCourse().getCourseCode() + " - " + r.getCourse().getTitle());
        }

        int idx = ConsoleUtil.readInt("Choose course: ") - 1;
        if (idx < 0 || idx >= s.getRegistrations().size()) {
            System.out.println("Invalid.");
            return;
        }

        CourseRegistration reg = s.getRegistrations().get(idx);
        enterAttendance(reg, service);
    }

    private static void enterAttendance(CourseRegistration reg, AttendanceService service) {
        // Since this is console, we can't easily pass a Component parent.
        // But wait, DashboardUI is GUI. Main.java is Console.
        // The user request was "Refactor... logic for button action logic".
        // DashboardUI needs to use the new method. Main.java (Console) logic is
        // separate.
        // This file I am editing is Main.java? NO! The tool call above was meant for
        // DashboardUI?
        // Let me check my previous View.
        // Step 301 viewed DashboardUI.java.
        // But earlier I viewed Main.java.
        // Wait, I am editing Main.java here? No, I should edit DashboardUI.java.
        // But Main.java also has 'enterAttendance'.
        // I need to be careful. The user request "Refactor... button action logic"
        // implies GUI.
        // "Simplify this clas... remove any old Mid/Final".2
        // If I updated AttendanceRecord, Main.java will break.
        // So I MUST update Main.java too.

        System.out.println("--- ATTENDANCE ENTRY ---");
        // Replicate simple logic for Console or just disable it?
        // I'll update it to use the new fields.

        System.out.println("Theory Conducted:");
        int tc = ConsoleUtil.readInt("Value: ");
        System.out.println("Theory Attended:");
        int ta = ConsoleUtil.readInt("Value: ");

        reg.getAttendanceRecord().setTheoryConducted(tc);
        reg.getAttendanceRecord().setTheoryAttended(ta);

        if (reg.getCourse().isHasLab()) {
            System.out.println("Lab Conducted:");
            int lc = ConsoleUtil.readInt("Value: ");
            System.out.println("Lab Attended:");
            int la = ConsoleUtil.readInt("Value: ");
            reg.getAttendanceRecord().setLabConducted(lc);
            reg.getAttendanceRecord().setLabAttended(la);
        }

        // Save
        repository.DatabaseStudentRepository repo = new repository.DatabaseStudentRepository();
        repo.updateAttendance(reg.getId(), reg.getAttendanceRecord());
        System.out.println("Saved.");
    }

    // -----------------------------------------------------------
    // MARKS ENTRY
    // -----------------------------------------------------------
    private static void marksMenu(Student s, StudentService studentService, ReportService reportService) {
        if (s.getRegistrations().isEmpty()) {
            System.out.println("No courses.");
            return;
        }
        System.out.println("\n--- MARKS ENTRY (Full GPA Flow) ---");
        for (int i = 0; i < s.getRegistrations().size(); i++) {
            CourseRegistration r = s.getRegistrations().get(i);
            System.out.println((i + 1) + ". " + r.getCourse().getCourseCode() + " - " + r.getCourse().getTitle());
        }
        int idx = ConsoleUtil.readInt("Choose course: ") - 1;
        if (idx < 0 || idx >= s.getRegistrations().size()) {
            System.out.println("Invalid.");
            return;
        }
        CourseRegistration reg = s.getRegistrations().get(idx);
        System.out.println("\nYou chose: " + reg.getCourse().getCourseCode() + " - " + reg.getCourse().getTitle());
        System.out.println("\n--- THEORY SECTION ---");
        enterTheoryMarks(reg);
        if (reg.getCourse().isHasLab()) {
            System.out.println("\n--- LAB SECTION ---");
            enterLabMarks(reg);
        }
        double percent = reportService.computeCoursePercentage(reg);
        String grade = reportService.percentageToGrade(percent);
        double gpa = reportService.gradeToGPA(grade);
        System.out.println("\n--- COURSE RESULT ---");
        System.out.printf("Percentage: %.2f%%\n", percent);
        System.out.printf("Grade: %s | GPA: %.2f\n", grade, gpa);
    }

    private static void enterTheoryMarks(CourseRegistration reg) {
        int qCount = ConsoleUtil.readInt("How many QUIZZES were there? (min 0): ");
        for (int i = 1; i <= qCount; i++) {
            int total = ConsoleUtil.readInt("Quiz " + i + " total marks: ");
            int obtained = ConsoleUtil.readInt("Quiz " + i + " obtained marks: ");
            reg.addMark(new Mark(AssessmentType.QUIZ, calcPercent(obtained, total)));
        }
        int aCount = ConsoleUtil.readInt("How many ASSIGNMENTS were there? (min 0): ");
        for (int i = 1; i <= aCount; i++) {
            int total = ConsoleUtil.readInt("Assignment " + i + " total marks: ");
            int obtained = ConsoleUtil.readInt("Assignment " + i + " obtained marks: ");
            reg.addMark(new Mark(AssessmentType.ASSIGNMENT, calcPercent(obtained, total)));
        }
        int midTotal = ConsoleUtil.readInt("MID total marks: ");
        int midObt = ConsoleUtil.readInt("MID obtained marks: ");
        reg.addMark(new Mark(AssessmentType.MID, calcPercent(midObt, midTotal)));
        int finTotal = ConsoleUtil.readInt("FINAL total marks: ");
        int finObt = ConsoleUtil.readInt("FINAL obtained marks: ");
        reg.addMark(new Mark(AssessmentType.FINAL, calcPercent(finObt, finTotal)));
    }

    private static void enterLabMarks(CourseRegistration reg) {
        int laCount = ConsoleUtil.readInt("How many LAB ASSIGNMENTS were there? (min 0): ");
        for (int i = 1; i <= laCount; i++) {
            int total = ConsoleUtil.readInt("Lab Assignment " + i + " total marks: ");
            int obtained = ConsoleUtil.readInt("Lab Assignment " + i + " obtained marks: ");
            reg.addMark(new Mark(AssessmentType.LAB_ASSIGNMENT, calcPercent(obtained, total)));
        }
        int lmTotal = ConsoleUtil.readInt("Lab MID total marks: ");
        int lmObt = ConsoleUtil.readInt("Lab MID obtained marks: ");
        reg.addMark(new Mark(AssessmentType.LAB_MID, calcPercent(lmObt, lmTotal)));
        int lfTotal = ConsoleUtil.readInt("Lab FINAL total marks: ");
        int lfObt = ConsoleUtil.readInt("Lab FINAL obtained marks: ");
        reg.addMark(new Mark(AssessmentType.LAB_FINAL, calcPercent(lfObt, lfTotal)));
    }

    private static double calcPercent(int obtained, int total) {
        if (total <= 0)
            return 0.0;
        if (obtained < 0)
            obtained = 0;
        if (obtained > total)
            obtained = total;
        return (obtained * 100.0) / total;
    }

    private static void reportMenu(Student s, ReportService report) {
        if (s.getRegistrations().isEmpty()) {
            System.out.println("No courses.");
            return;
        }
        System.out.println("\n--- REPORT ---");
        double totalPoints = 0;
        double totalCredits = 0;
        for (CourseRegistration r : s.getRegistrations()) {
            double perc = report.computeCoursePercentage(r);
            String grade = report.percentageToGrade(perc);
            double gpa = report.gradeToGPA(grade);
            int credits = r.getCourse().getCreditHours();
            System.out.printf("%s | %s | %.2f%% | %s | GPA %.2f\n",
                    r.getCourse().getCourseCode(),
                    r.getCourse().getTitle(),
                    perc, grade, gpa);
            totalPoints += gpa * credits;
            totalCredits += credits;
        }
        double sgpa = (totalCredits == 0) ? 0 : totalPoints / totalCredits;
        System.out.printf("\nSemester GPA (SGPA): %.2f\n", sgpa);
        if (ConsoleUtil.readLine("Export Report? (yes/no): ").equalsIgnoreCase("yes"))
            exportReport(s, report);
    }

    private static void exportReport(Student s, ReportService report) {
        String file = s.getRegistrationNo() + "_Report.txt";
        try {
            java.io.PrintWriter out = new java.io.PrintWriter(file);
            out.println("SMART SEMESTER PROGRESS TRACKER - REPORT");
            out.println("Name: " + s.getFullName());
            out.println("Reg No: " + s.getRegistrationNo());
            double totalPoints = 0;
            double totalCredits = 0;
            for (CourseRegistration r : s.getRegistrations()) {
                double perc = report.computeCoursePercentage(r);
                String grade = report.percentageToGrade(perc);
                double gpa = report.gradeToGPA(grade);
                int credits = r.getCourse().getCreditHours();
                out.printf("%s | %s | %.2f%% | %s | GPA %.2f\n",
                        r.getCourse().getCourseCode(), r.getCourse().getTitle(), perc, grade, gpa);
                totalPoints += gpa * credits;
                totalCredits += credits;
            }
            double sgpa = (totalCredits == 0) ? 0 : totalPoints / totalCredits;
            out.printf("\nSemester GPA (SGPA): %.2f\n", sgpa);
            out.close();
            System.out.println("Report exported to " + file);
        } catch (Exception e) {
            System.out.println("Export failed.");
        }
    }

    private static void semesterResetMenu(Student s, StudentService service) {
        System.out.println("\n--- SEMESTER RESET ---");
        String c = ConsoleUtil.readLine("Are you sure? (yes/no): ");
        if (!c.equalsIgnoreCase("yes"))
            return;
        int sem = ConsoleUtil.readInt("Enter new semester: ");
        service.resetSemester(s, sem);
        System.out.println("Semester reset complete.");
    }
}
