package repository;

import model.Course;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCourseRepository implements CourseRepository {

    @Override
    public Course save(Course c) {
        if (c == null) return null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO courses (course_code, title, theory_credits, lab_credits, has_lab, is_fyp) " +
                         "VALUES (?, ?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE " +
                         "title=VALUES(title), theory_credits=VALUES(theory_credits), " +
                         "lab_credits=VALUES(lab_credits), has_lab=VALUES(has_lab), is_fyp=VALUES(is_fyp)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, c.getCourseCode());
                pstmt.setString(2, c.getTitle());
                pstmt.setInt(3, c.getTheoryCredits());
                pstmt.setInt(4, c.getLabCredits());
                pstmt.setBoolean(5, c.isHasLab());
                pstmt.setBoolean(6, c.isFYP());
                
                pstmt.executeUpdate();
            }
            return c;
        } catch (SQLException e) {
            System.err.println("Error saving course: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM courses ORDER BY course_code";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Course course = mapCourse(rs);
                        courses.add(course);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all courses: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }

    @Override
    public Course findByCourseCode(String code) {
        if (code == null) return null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM courses WHERE course_code = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, code);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return mapCourse(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding course: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course c = new Course(
            rs.getString("course_code"),
            rs.getString("title"),
            rs.getInt("theory_credits"),
            rs.getInt("lab_credits")
        );
        c.setHasLab(rs.getBoolean("has_lab"));
        c.setFYP(rs.getBoolean("is_fyp"));
        return c;
    }
}

