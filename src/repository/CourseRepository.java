package repository;

import model.Course;
import java.util.List;

public interface CourseRepository {
    Course save(Course c);
    List<Course> getAllCourses();

    // add this so services can lookup courses by code
    Course findByCourseCode(String code);
}
