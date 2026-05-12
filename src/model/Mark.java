package model;

import java.time.LocalDateTime;

public class Mark {

    private AssessmentType type;
    private double obtained;
    private double totalMarks;
    private String label; // e.g., "Assignment 1"
    private LocalDateTime gradedAt;

    // Legacy constructor (keeps total=0 or handles validation later if needed)
    public Mark(AssessmentType type, double obtained) {
        this(type, obtained, 0, type.toString());
    }

    public Mark(AssessmentType type, double obtained, double totalMarks, String label) {
        this.type = type;
        this.obtained = obtained;
        this.totalMarks = totalMarks;
        this.label = label;
        this.gradedAt = LocalDateTime.now();
    }

    public AssessmentType getType() {
        return type;
    }

    public double getObtained() {
        return obtained;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public String getLabel() {
        return label;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }
}
