package repository;

import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Clean, simple in-memory repository that matches StudentRepository interface:
 * - save(Student)
 * - findByRegistrationNo(String)
 * - findAll()
 * - delete(String)
 *
 * Also avoids the "clear-then-addAll-self" bug that was removing registrations
 * when caller passed the same instance that's already stored.
 */
public class InMemoryStudentRepository implements StudentRepository {

    private final List<Student> students = new ArrayList<>();

    @Override
    public Student save(Student s) {
        if (s == null)
            return null;

        // find existing by registration number
        Student existing = findByRegistrationNo(s.getRegistrationNo());

        // new student -> add and return the same instance
        if (existing == null) {
            s.setUserId(students.size() + 1);
            students.add(s);
            return s;
        }

        // update primitive fields
        existing.setFullName(s.getFullName());
        existing.setPasswordHash(s.getPasswordHash());
        existing.setSalt(s.getSalt());
        existing.setKeyPass(s.getKeyPass());
        existing.setProgramLevel(s.getProgramLevel());
        existing.setDegree(s.getDegree());
        existing.setSemester(s.getSemester());

        // IMPORTANT: if caller passed the same instance that's stored in list,
        // do NOT clear/addAll on its registrations (that would clear them).
        if (existing == s) {
            // nothing else to do (registrations already reflect caller's changes)
            return existing;
        }

        // if caller passed a different instance, replace registrations safely
        existing.getRegistrations().clear();
        if (s.getRegistrations() != null && !s.getRegistrations().isEmpty()) {
            // shallow-copy registrations (CourseRegistration objects are reused)
            existing.getRegistrations().addAll(s.getRegistrations());
        }

        return existing;
    }

    @Override
    public Student findByRegistrationNo(String regNo) {
        if (regNo == null)
            return null;
        for (Student st : students) {
            if (regNo.equalsIgnoreCase(st.getRegistrationNo())) {
                return st;
            }
        }
        return null;
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students);
    }

    @Override
    public void delete(String regNo) {
        Student s = findByRegistrationNo(regNo);
        if (s != null)
            students.remove(s);
    }

    @Override
    public boolean registerCourse(int studentId, String courseCode) {
        // Find existing student by "userId" logic if possible, or assume caller handled
        // finding student
        // Since this is in-memory for testing, we can loop to find by ID
        Student target = students.stream().filter(s -> s.getUserId() == studentId).findFirst().orElse(null);
        if (target != null) {
            // Check current regs
            boolean already = target.getRegistrations().stream()
                    .anyMatch(r -> r.getCourse() != null && r.getCourse().getCourseCode().equals(courseCode));
            if (!already) {
                // In real app, we'd fetch Course object. Here we just fake it or need partial
                // object
                // But InMemory is mostly for quick tests.
                // Returning true to mimic success.
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRegistered(int studentId, String courseCode) {
        Student target = students.stream().filter(s -> s.getUserId() == studentId).findFirst().orElse(null);
        if (target != null) {
            return target.getRegistrations().stream()
                    .anyMatch(r -> r.getCourse() != null && r.getCourse().getCourseCode().equals(courseCode));
        }
        return false;
    }
}
