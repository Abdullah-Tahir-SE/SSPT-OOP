package repository;

import model.Course;

import java.util.ArrayList;
import java.util.List;

public class InMemoryCourseRepository implements CourseRepository {

    private final List<Course> courses = new ArrayList<>();

    @Override
    public Course save(Course c) {
        if (c == null) return null;

        Course existing = findByCourseCode(c.getCourseCode());
        if (existing == null) {
            // assign no id here unless you have id field, just store
            courses.add(c);
            return c;
        } else {
            existing.setTitle(c.getTitle());
            existing.setCreditHours(c.getCreditHours());
            existing.setHasLab(c.isHasLab());
            existing.setFYP(c.isFYP());
            return existing;
        }
    }

    @Override
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    @Override
    public Course findByCourseCode(String code) {
        if (code == null) return null;
        for (Course c : courses) {
            if (code.equalsIgnoreCase(c.getCourseCode())) return c;
        }
        return null;
    }
}
