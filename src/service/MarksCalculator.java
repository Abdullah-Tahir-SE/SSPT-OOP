package service;

import model.AssessmentType;
import model.CourseRegistration;
import model.Mark;
import java.util.List;

public class MarksCalculator {

    // --- Theory Weights ---
    private static final double W_ASSIGNMENT = 0.10; // 10%
    private static final double W_QUIZ = 0.15; // 15%
    private static final double W_MID = 0.25; // 25%
    private static final double W_FINAL = 0.50; // 50%

    // --- Lab Weights ---
    private static final double W_LAB_ASSIGNMENT = 0.25; // 25%
    private static final double W_LAB_MID = 0.25; // 25%
    private static final double W_LAB_FINAL = 0.50; // 50%

    // --- Component Weights ---
    private static final double W_THEORY_COMPONENT = 0.75;
    private static final double W_LAB_COMPONENT = 0.25;

    public double calculateTheoryTotal(List<Mark> marks) {
        double assignObtained = 0, assignTotal = 0;
        double quizObtained = 0, quizTotal = 0;
        double midObtained = 0; // Total fixed 25
        double finalObtained = 0; // Total fixed 50

        for (Mark m : marks) {
            switch (m.getType()) {
                case ASSIGNMENT:
                    assignObtained += m.getObtained();
                    assignTotal += m.getTotalMarks();
                    break;
                case QUIZ:
                    quizObtained += m.getObtained();
                    quizTotal += m.getTotalMarks();
                    break;
                case MID:
                    midObtained = m.getObtained(); // Single entry expected
                    break;
                case FINAL:
                    finalObtained = m.getObtained(); // Single entry expected
                    break;
                default:
                    break;
            }
        }

        // Normalize Assignments to 10%
        double assignScore = (assignTotal > 0) ? (assignObtained / assignTotal) * 10.0 : 0.0;

        // Normalize Quizzes to 15%
        double quizScore = (quizTotal > 0) ? (quizObtained / quizTotal) * 15.0 : 0.0;

        // Mid and Final are absolute values out of 25 and 50 respectively
        // (Assuming user enters them directly scaled, or we treat them as absolute
        // points)
        // Requirement says: "Ask for 'Obtained Marks' (Total is fixed at 25)" -> So
        // input is 22/25

        return assignScore + quizScore + midObtained + finalObtained;
    }

    public double calculateLabTotal(List<Mark> marks) {
        double labAssignObtained = 0, labAssignTotal = 0;
        double labMidObtained = 0;
        double labFinalObtained = 0;

        for (Mark m : marks) {
            switch (m.getType()) {
                case LAB_ASSIGNMENT:
                    labAssignObtained += m.getObtained();
                    labAssignTotal += m.getTotalMarks();
                    break;
                case LAB_MID:
                    labMidObtained = m.getObtained();
                    break;
                case LAB_FINAL:
                    labFinalObtained = m.getObtained();
                    break;
                default:
                    break;
            }
        }

        // Lab Assignments 25%
        double labAssignScore = (labAssignTotal > 0) ? (labAssignObtained / labAssignTotal) * 25.0 : 0.0;

        // Lab Mid 25% (Absolute)
        // Lab Final 50% (Absolute)

        return labAssignScore + labMidObtained + labFinalObtained;
    }

    public double calculateFinalScore(CourseRegistration reg) {
        double theoryTotal = calculateTheoryTotal(reg.getMarks());

        if (reg.getCourse().isHasLab()) {
            double labTotal = calculateLabTotal(reg.getMarks()); // Out of 100
            return (theoryTotal * W_THEORY_COMPONENT) + (labTotal * W_LAB_COMPONENT);
        } else {
            return theoryTotal;
        }
    }

    public String calculateGrade(double score) {
        if (score >= 85)
            return "A";
        if (score >= 80)
            return "A-";
        if (score >= 75)
            return "B+";
        if (score >= 70)
            return "B";
        if (score >= 65)
            return "B-";
        if (score >= 61)
            return "C+";
        if (score >= 58)
            return "C";
        if (score >= 55)
            return "C-";
        if (score >= 50)
            return "D";
        return "F";
    }

    public double gradeToGPA(String grade) {
        switch (grade) {
            case "A":
                return 4.0;
            case "A-":
                return 3.7;
            case "B+":
                return 3.3;
            case "B":
                return 3.0;
            case "B-":
                return 2.7;
            case "C+":
                return 2.3;
            case "C":
                return 2.0;
            case "C-":
                return 1.7;
            case "D":
                return 1.0;
            default:
                return 0.0;
        }
    }
}
