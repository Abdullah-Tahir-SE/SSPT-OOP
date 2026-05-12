package model;

import java.util.ArrayList;
import java.util.List;

public class CourseResult {
    private String courseCode;
    private String courseTitle;
    private boolean hasLab;

    // Theory Components
    private List<Mark> theoryAssessments = new ArrayList<>(); // Assigns + Quizzes
    private Mark theoryMid;
    private Mark theoryFinal;

    // Lab Components
    private List<Mark> labAssessments = new ArrayList<>(); // Lab Assigns
    private Mark labMid;
    private Mark labFinal;

    // Final Results
    private double totalObtained;
    private double totalMax; // e.g. 100
    private double gpa;
    private String grade;

    public CourseResult(String code, String title, boolean hasLab) {
        this.courseCode = code;
        this.courseTitle = title;
        this.hasLab = hasLab;
    }

    // Getters and Setters
    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public boolean isHasLab() {
        return hasLab;
    }

    public List<Mark> getTheoryAssessments() {
        return theoryAssessments;
    }

    public void addTheoryAssessment(Mark m) {
        theoryAssessments.add(m);
    }

    public Mark getTheoryMid() {
        return theoryMid;
    }

    public void setTheoryMid(Mark theoryMid) {
        this.theoryMid = theoryMid;
    }

    public Mark getTheoryFinal() {
        return theoryFinal;
    }

    public void setTheoryFinal(Mark theoryFinal) {
        this.theoryFinal = theoryFinal;
    }

    public List<Mark> getLabAssessments() {
        return labAssessments;
    }

    public void addLabAssessment(Mark m) {
        labAssessments.add(m);
    }

    public Mark getLabMid() {
        return labMid;
    }

    public void setLabMid(Mark labMid) {
        this.labMid = labMid;
    }

    public Mark getLabFinal() {
        return labFinal;
    }

    public void setLabFinal(Mark labFinal) {
        this.labFinal = labFinal;
    }

    public double getTotalObtained() {
        return totalObtained;
    }

    public void setTotalObtained(double totalObtained) {
        this.totalObtained = totalObtained;
    }

    public double getTotalMax() {
        return totalMax;
    }

    public void setTotalMax(double totalMax) {
        this.totalMax = totalMax;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
