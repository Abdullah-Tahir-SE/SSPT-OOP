package model;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private int userId;
    private String fullName;
    private String registrationNo;
    private String passwordHash;
    private String salt;
    private String keyPass;
    private String programLevel;
    private String degree;
    private int semester;
    private double currentSgpa; // New field

    private List<CourseRegistration> registrations = new ArrayList<>();

    public Student() {
    }

    // Basic getters / setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getKeyPass() {
        return keyPass;
    }

    public void setKeyPass(String keyPass) {
        this.keyPass = keyPass;
    }

    public String getProgramLevel() {
        return programLevel;
    }

    public void setProgramLevel(String programLevel) {
        this.programLevel = programLevel;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public double getCurrentSgpa() {
        return currentSgpa;
    }

    public void setCurrentSgpa(double currentSgpa) {
        this.currentSgpa = currentSgpa;
    }

    private double totalQp;
    private String semesterGrade;
    private int semCredits;

    public double getTotalQp() {
        return totalQp;
    }

    public void setTotalQp(double totalQp) {
        this.totalQp = totalQp;
    }

    public String getSemesterGrade() {
        return semesterGrade;
    }

    public void setSemesterGrade(String semesterGrade) {
        this.semesterGrade = semesterGrade;
    }

    public int getSemCredits() {
        return semCredits;
    }

    public void setSemCredits(int semCredits) {
        this.semCredits = semCredits;
    }

    // Registrations list
    public List<CourseRegistration> getRegistrations() {
        if (registrations == null)
            registrations = new ArrayList<>();
        return registrations;
    }

    public void setRegistrations(List<CourseRegistration> regs) {
        this.registrations = regs;
    }

    public void addRegistration(CourseRegistration reg) {
        if (registrations == null)
            registrations = new ArrayList<>();
        registrations.add(reg);
    }

    public void removeRegistration(CourseRegistration reg) {
        if (registrations != null)
            registrations.remove(reg);
    }

    public int getTotalCredits() {
        int sum = 0;
        if (registrations == null)
            return 0;
        for (CourseRegistration r : registrations) {
            if (r != null && r.getCourse() != null)
                sum += r.getCourse().getCreditHours();
        }
        return sum;
    }
}
