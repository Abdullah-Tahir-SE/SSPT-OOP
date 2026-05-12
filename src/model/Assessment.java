package model;

public class Assessment {
    private AssessmentType type;
    private String title;
    private int totalMarks;

    public Assessment(AssessmentType type, String title, int totalMarks) {
        this.type = type;
        this.title = title;
        this.totalMarks = totalMarks;
    }

    public AssessmentType getType() { return type; }
    public String getTitle() { return title; }
    public int getTotalMarks() { return totalMarks; }
}

