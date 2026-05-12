package repository;

import model.Student;
import java.util.List;

public interface StudentRepository {
    Student save(Student s);

    Student findByRegistrationNo(String regNo);

    List<Student> findAll();

    void delete(String regNo);

    // Direct registration support
    boolean registerCourse(int studentId, String courseCode);

    boolean isRegistered(int studentId, String courseCode);
}
