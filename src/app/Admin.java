package app;

import model.Student;
import repository.StudentRepository;
import repository.DatabaseStudentRepository;
import util.ConsoleUtil;

import java.util.List;

public class Admin {

    private static final String ADMIN_USER = "abii";
    private static final String ADMIN_PASS = "abii@0827";

    private final StudentRepository studentRepo;

    public Admin() {
        this.studentRepo = new DatabaseStudentRepository();
    }

    public void start() {
        if (login()) {
            adminMenu();
        } else {
            System.out.println("Invalid credentials. Access denied.");
        }
    }

    private boolean login() {
        System.out.println("\n=== Admin Login ===");
        String u = ConsoleUtil.readLine("Username: ");
        String p = ConsoleUtil.readLine("Password: ");
        return ADMIN_USER.equals(u) && ADMIN_PASS.equals(p);
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n=== Admin Panel ===");
            System.out.println("1. View All Students");
            System.out.println("2. Update Student");
            System.out.println("3. Delete Student");
            System.out.println("4. Logout");

            int choice = ConsoleUtil.readInt("Choose option: ");

            switch (choice) {
                case 1:
                    viewAllStudents();
                    break;
                case 2:
                    updateStudent();
                    break;
                case 3:
                    deleteStudent();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void viewAllStudents() {
        System.out.println("\n-- All Students --");
        List<Student> list = studentRepo.findAll();
        if (list.isEmpty()) {
            System.out.println("No students found.");
        } else {
            System.out.printf("%-5s %-20s %-15s %-10s %-20s%n", "ID", "Name", "Reg No", "Sem", "Degree");
            System.out.println("---------------------------------------------------------------------------");
            for (Student s : list) {
                System.out.printf("%-5d %-20s %-15s %-10d %-20s%n",
                        s.getUserId(),
                        truncate(s.getFullName(), 20),
                        s.getRegistrationNo(),
                        s.getSemester(),
                        truncate(s.getDegree(), 20));
            }
        }
    }

    private void updateStudent() {
        System.out.println("\n-- Update Student --");
        String regNo = ConsoleUtil.readLine("Enter Registration No of student to update: ");
        Student s = studentRepo.findByRegistrationNo(regNo);

        if (s == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("Found: " + s.getFullName() + " (" + s.getDegree() + ")");
        System.out.println("Leave blank to keep current value.");

        String name = ConsoleUtil.readLine("New Name [" + s.getFullName() + "]: ");
        if (!name.trim().isEmpty())
            s.setFullName(name);

        String semStr = ConsoleUtil.readLine("New Semester [" + s.getSemester() + "]: ");
        if (!semStr.trim().isEmpty()) {
            try {
                s.setSemester(Integer.parseInt(semStr));
            } catch (NumberFormatException e) {
                System.out.println("Invalid semester. Skipping update.");
            }
        }

        // Save updates (the repo save handles update if ID exists)
        studentRepo.save(s);
        System.out.println("Student record updated successfully.");
    }

    private void deleteStudent() {
        System.out.println("\n-- Delete Student --");
        String regNo = ConsoleUtil.readLine("Enter Registration No of student to delete: ");

        Student s = studentRepo.findByRegistrationNo(regNo);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }

        String confirm = ConsoleUtil.readLine("Are you sure you want to delete " + s.getFullName() + "? (yes/no): ");
        if ("yes".equalsIgnoreCase(confirm)) {
            studentRepo.delete(regNo);
            System.out.println("Student deleted.");
        } else {
            System.out.println("Operation cancelled.");
        }
    }

    private String truncate(String s, int len) {
        if (s == null)
            return "";
        if (s.length() <= len)
            return s;
        return s.substring(0, len - 3) + "...";
    }
}
