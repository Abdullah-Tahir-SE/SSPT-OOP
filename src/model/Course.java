package model;

public class Course {
    private String courseCode;
    private String title;

    private int theoryCredits;
    private int labCredits;

    private boolean hasLab;
    private boolean isFYP;

    public Course(String courseCode, String title, int creditHours) {
        this.courseCode = courseCode;
        this.title = title;
        this.theoryCredits = creditHours;
        this.labCredits = 0;
        this.hasLab = false;
        this.isFYP = false;
    }

    public Course(String courseCode, String title, int theoryCredits, int labCredits) {
        this.courseCode = courseCode;
        this.title = title;
        this.theoryCredits = theoryCredits;
        this.labCredits = labCredits;
        this.hasLab = labCredits > 0;
        this.isFYP = false;
    }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCreditHours() { return theoryCredits + labCredits; }

    public int getTheoryCredits() { return theoryCredits; }
    public void setTheoryCredits(int theoryCredits) { this.theoryCredits = theoryCredits; }

    public int getLabCredits() { return labCredits; }
    public void setLabCredits(int labCredits) { this.labCredits = labCredits; }

    public boolean isHasLab() { return hasLab; }
    public void setHasLab(boolean hasLab) { this.hasLab = hasLab; }

    public boolean isFYP() { return isFYP; }
    public void setFYP(boolean fyp) { isFYP = fyp; }

    // Legacy setter (if other code expects it)
    public void setCreditHours(int hours) { this.theoryCredits = hours; this.labCredits = 0; this.hasLab = false; }
}
