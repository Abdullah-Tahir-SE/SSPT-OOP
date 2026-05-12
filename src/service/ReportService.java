package service;

import model.CourseRegistration;
import model.Mark;

import java.util.*;

/**
 * ReportService - calculates percentages, grades and GPA.
 * - Theory-only courses: QUIZ 15%, ASSIGN 10%, MID 25%, FINAL 50%
 * - Theory+Lab: theory contributes 75% (using above split),
 *              lab contributes 25% (ASSIGN 25%, MID 25%, FINAL 50%)
 * Marks are expected to be recorded as absolute "obtained" scores scaled to 0..100 per item.
 * If you record raw obtained/max instead, convert to percent before adding Mark (recommended).
 */
public class ReportService {

    /**
     * Compute course percentage (0..100) for a registration.
     * Works for theory-only and theory+lab courses.
     */
    public double computeCoursePercentage(CourseRegistration reg) {
        if (reg == null || reg.getCourse() == null) return 0.0;

        boolean hasLab = reg.getCourse().isHasLab();
        boolean isFYP = reg.getCourse().isFYP();

        // FYP: simple average of recorded marks (if any)
        if (isFYP) {
            if (reg.getMarks().isEmpty()) return 0.0;
            double total = 0;
            for (Mark m : reg.getMarks()) total += m.getObtained();
            return clampPercent(total / reg.getMarks().size());
        }

        // Accumulators: we assume each Mark.obtained is already a percent value (0..100) for that item.
        double quizSum = 0; int quizCount = 0;
        double assignSum = 0; int assignCount = 0;
        double midSum = 0; int midCount = 0;
        double finalSum = 0; int finalCount = 0;

        double labAssignSum = 0; int labAssignCount = 0;
        double labMidSum = 0; int labMidCount = 0;
        double labFinalSum = 0; int labFinalCount = 0;

        for (Mark m : reg.getMarks()) {
            if (m == null || m.getType() == null) continue;
            String t = m.getType().name();
            double val = m.getObtained(); // expected 0..100 percent for that assessment
            switch (t) {
                case "QUIZ": quizSum += val; quizCount++; break;
                case "ASSIGNMENT": assignSum += val; assignCount++; break;
                case "MID": midSum += val; midCount++; break;
                case "FINAL": finalSum += val; finalCount++; break;
                case "LAB_ASSIGNMENT": labAssignSum += val; labAssignCount++; break;
                case "LAB_MID": labMidSum += val; labMidCount++; break;
                case "LAB_FINAL": labFinalSum += val; labFinalCount++; break;
                default: break;
            }
        }

        // category percentages (0..100). if no items -> 0
        double quizPct = (quizCount == 0) ? 0.0 : (quizSum / quizCount);
        double assignPct = (assignCount == 0) ? 0.0 : (assignSum / assignCount);
        double midPct = (midCount == 0) ? 0.0 : (midSum / midCount);
        double finalPct = (finalCount == 0) ? 0.0 : (finalSum / finalCount);

        // Theory weights -> QUIZ 15%, ASSIGN 10%, MID 25%, FINAL 50% (sum = 100)
        double theoryPercent = (quizPct * 0.15) + (assignPct * 0.10) + (midPct * 0.25) + (finalPct * 0.50);

        if (!hasLab) {
            return clampPercent(theoryPercent);
        }

        // Lab category percents
        double labAssignPct = (labAssignCount == 0) ? 0.0 : (labAssignSum / labAssignCount);
        double labMidPct = (labMidCount == 0) ? 0.0 : (labMidSum / labMidCount);
        double labFinalPct = (labFinalCount == 0) ? 0.0 : (labFinalSum / labFinalCount);

        // Lab internal weights: Assign 25%, Mid 25%, Final 50%
        double labPercent = (labAssignPct * 0.25) + (labMidPct * 0.25) + (labFinalPct * 0.50);

        // Combine: Theory 75% of course, Lab 25%
        double finalCoursePercent = (theoryPercent * 0.75) + (labPercent * 0.25);

        return clampPercent(finalCoursePercent);
    }

    // clamp small FP errors and prevent >100
    private double clampPercent(double p) {
        if (Double.isNaN(p) || p < 0.0) return 0.0;
        if (p > 100.0) return 100.0;
        return p;
    }

    // ---------- GRADE ----------
    public String percentageToGrade(double p) {
        if (p >= 85) return "A+";
        if (p >= 75) return "A";
        if (p >= 65) return "B";
        if (p >= 50) return "C";
        if (p >= 40) return "D";
        return "F";
    }

    // ---------- GPA ----------
    public double gradeToGPA(String grade) {
        switch (grade) {
            case "A+": return 4.0;
            case "A": return 3.7;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            default: return 0.0;
        }
    }

    // ---------- Calculate GPA of a single course ----------
    public double calculateCourseGPA(CourseRegistration reg) {
        double percent = computeCoursePercentage(reg);
        String grade = percentageToGrade(percent);
        return gradeToGPA(grade);
    }

    // ---------- Calculate Semester GPA (SGPA) ----------
    public double calculateSemesterGPA(List<CourseRegistration> regs) {
        double totalPoints = 0;
        double totalCredits = 0;
        if (regs == null) return 0;
        for (CourseRegistration r : regs) {
            double percent = computeCoursePercentage(r);
            String grade = percentageToGrade(percent);
            double gpa = gradeToGPA(grade);
            int credits = r.getCourse() != null ? r.getCourse().getCreditHours() : 0;
            totalPoints += gpa * credits;
            totalCredits += credits;
        }
        if (totalCredits == 0) return 0.0;
        return totalPoints / totalCredits;
    }

    // ---------- Calculate Cumulative GPA ----------
    public double calculateCumulativeGPA(List<Double> semesterGPAs) {
        if (semesterGPAs == null || semesterGPAs.isEmpty()) return 0;
        double sum = 0;
        for (double g : semesterGPAs) sum += g;
        return sum / semesterGPAs.size();
    }
}
