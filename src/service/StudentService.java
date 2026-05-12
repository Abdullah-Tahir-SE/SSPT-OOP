package service;

import model.AttendanceRecord;
import model.Course;
import model.CourseRegistration;
import model.Student;
import repository.CourseRepository;
import repository.StudentRepository;

import java.time.LocalDateTime;

public class StudentService {

    private StudentRepository studentRepo;
    private CourseRepository courseRepo;

    public StudentService(StudentRepository sRepo, CourseRepository cRepo) {
        this.studentRepo = sRepo;
        this.courseRepo = cRepo;
    }

    // backward-compatible (theory-only)
    public boolean addCourseToStudent(Student s, String code, String title, int credits) {
        return addCourseToStudent(s, code, title, credits, 0);
    }

    // new: supports theory + lab
    public boolean addCourseToStudent(Student s, String code, String title, int theoryCredits, int labCredits) {
        if (s == null || code == null || code.trim().isEmpty()) return false;
        code = code.trim();
        title = (title == null ? "" : title.trim());

        int newCourseCredits = Math.max(0, theoryCredits) + Math.max(0, labCredits);
        if (newCourseCredits <= 0) return false;

        // check duplicate
        for (CourseRegistration r : s.getRegistrations()) {
            if (r.getCourse() != null && code.equalsIgnoreCase(r.getCourse().getCourseCode())) {
                return false;
            }
        }

        // total credit limit
        int current = s.getTotalCredits();
        if (current + newCourseCredits > 21) return false;

        // construct Course (try split constructor, fallback)
        Course c;
        if (labCredits > 0) {
            try {
                c = new Course(code, title, theoryCredits, labCredits);
                c.setHasLab(true);
            } catch (Exception e) {
                c = new Course(code, title, newCourseCredits);
                c.setHasLab(true);
            }
        } else {
            c = new Course(code, title, theoryCredits);
            c.setHasLab(false);
        }

        // persist course if possible
        try {
            if (courseRepo != null) courseRepo.save(c);
        } catch (Exception ignored) {}

        CourseRegistration reg = new CourseRegistration();
        reg.setCourse(c);
        reg.setRegisteredAt(LocalDateTime.now());
        reg.setAttendanceRecord(new AttendanceRecord());

        s.addRegistration(reg);

        // save student
        Student stored;
        try {
            stored = studentRepo.save(s);
        } catch (Exception ex) {
            return false;
        }

        if (stored != null) s.setRegistrations(stored.getRegistrations());
        return true;
    }

    public Student getLatestStudent(String regNo) {
        if (regNo == null) return null;
        return studentRepo.findByRegistrationNo(regNo);
    }

    // FIXED: removed typo 'ppublic' and ensured method compiles
    public boolean updateCourse(Student s, String oldCode, String newCode, String newTitle, int newCredits) {
        if (s == null) return false;
        for (CourseRegistration reg : s.getRegistrations()) {
            if (reg.getCourse() != null && reg.getCourse().getCourseCode().equalsIgnoreCase(oldCode)) {
                int currentTotal = s.getTotalCredits() - reg.getCourse().getCreditHours();
                if (currentTotal + newCredits > 21) return false;

                reg.getCourse().setCourseCode(newCode);
                reg.getCourse().setTitle(newTitle);

                // try setting split fields if available, otherwise legacy
                try {
                    int lab = 0;
                    try { lab = reg.getCourse().getLabCredits(); } catch (Exception ignored) {}
                    if (lab > 0) {
                        int newTheory = Math.max(0, newCredits - lab);
                        try { reg.getCourse().setTheoryCredits(newTheory); } catch (Exception ignored) {}
                    } else {
                        try { reg.getCourse().setTheoryCredits(newCredits); }
                        catch (Exception e1) {
                            try { reg.getCourse().setCreditHours(newCredits); } catch (Exception ignored) {}
                        }
                    }
                } catch (Exception e) {
                    try { reg.getCourse().setCreditHours(newCredits); } catch (Exception ignored) {}
                }

                Student stored = studentRepo.save(s);
                if (stored != null) s.setRegistrations(stored.getRegistrations());
                return true;
            }
        }
        return false;
    }

    public boolean deleteCourse(Student s, String courseCode) {
        if (s == null) return false;
        CourseRegistration toDelete = null;
        for (CourseRegistration reg : s.getRegistrations()) {
            if (reg.getCourse() != null && reg.getCourse().getCourseCode().equalsIgnoreCase(courseCode)) {
                toDelete = reg;
                break;
            }
        }
        if (toDelete != null) {
            s.removeRegistration(toDelete);
            Student stored = studentRepo.save(s);
            if (stored != null) s.setRegistrations(stored.getRegistrations());
            return true;
        }
        return false;
    }

    public void viewProfile(Student s) {
        System.out.println("\n--- PROFILE ---");
        System.out.println("Name: " + s.getFullName());
        System.out.println("Registration No: " + s.getRegistrationNo());
        System.out.println("Program: " + s.getProgramLevel() + " - " + s.getDegree());
        System.out.println("Semester: " + s.getSemester());

        var regs = s.getRegistrations();
        System.out.println("Registered Courses:");
        if (regs == null || regs.isEmpty()) {
            System.out.println("Total Credits: 0 / 21");
            System.out.println("No registered courses.");
            return;
        }

        for (CourseRegistration r : regs) {
            System.out.printf("%s - %s, %d Credits, Registered at: %s\n",
                    r.getCourse().getCourseCode(),
                    r.getCourse().getTitle(),
                    r.getCourse().getCreditHours(),
                    r.getRegisteredAt() != null ? r.getRegisteredAt().toString() : "N/A");
        }
        System.out.println("Total Credits: " + s.getTotalCredits() + " / 21");
    }

    public void resetSemester(Student s, int newSemester) {
        if (s == null) return;
        s.getRegistrations().clear();
        s.setSemester(newSemester);
        studentRepo.save(s);
    }
}
