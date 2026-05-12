package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CourseRegistration {
    private Course course;
    private AttendanceRecord attendanceRecord = new AttendanceRecord();
    private List<Mark> marks = new ArrayList<>();
    private LocalDateTime registeredAt;

    private int id; // Database Primary Key (registration_id)
    private String finalGrade;
    private double gpa;

    public CourseRegistration() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public AttendanceRecord getAttendanceRecord() {
        if (attendanceRecord == null)
            attendanceRecord = new AttendanceRecord();
        return attendanceRecord;
    }

    public void setAttendanceRecord(AttendanceRecord ar) {
        this.attendanceRecord = ar;
    }

    public List<Mark> getMarks() {
        if (marks == null)
            marks = new ArrayList<>();
        return marks;
    }

    public void addMark(Mark m) {
        if (marks == null)
            marks = new ArrayList<>();
        marks.add(m);
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }
}
