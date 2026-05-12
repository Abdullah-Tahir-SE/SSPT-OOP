package service;

import model.AttendanceRecord;
import model.CourseRegistration;

public class AttendanceService {

    // Thresholds
    private static final int MAX_THEORY = 16;
    private static final int MAX_LAB = 8;
    // (Assuming original max per term)

    public void calculateAndSaveAttendance(CourseRegistration reg, java.awt.Component parentUI) {
        String courseTitle = reg.getCourse().getTitle();

        // --- Step A: Theory Input ---
        int theoryConducted = -1;
        while (true) {
            String input = javax.swing.JOptionPane.showInputDialog(parentUI,
                    "Enter Theory Classes CONDUCTED (Max 32):",
                    "Attendance: " + courseTitle, javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (input == null)
                return; // Cancelled
            try {
                theoryConducted = Integer.parseInt(input);
                if (theoryConducted < 0 || theoryConducted > 32) {
                    javax.swing.JOptionPane.showMessageDialog(parentUI, "Error: Max limit is 32.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(parentUI, "Invalid number.");
            }
        }

        int theoryAttended = -1;
        while (true) {
            String input = javax.swing.JOptionPane.showInputDialog(parentUI,
                    "Enter Theory Classes ATTENDED (Max " + theoryConducted + "):",
                    "Attendance: " + courseTitle, javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (input == null)
                return;
            try {
                theoryAttended = Integer.parseInt(input);
                if (theoryAttended < 0 || theoryAttended > theoryConducted) {
                    javax.swing.JOptionPane.showMessageDialog(parentUI, "Error: Attended cannot exceed Conducted.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(parentUI, "Invalid number.");
            }
        }

        // --- Step B: Lab Input (Conditional) ---
        int labConducted = 0;
        int labAttended = 0;

        if (reg.getCourse().isHasLab()) {
            while (true) {
                String input = javax.swing.JOptionPane.showInputDialog(parentUI,
                        "Enter Lab Classes CONDUCTED (Max 16):",
                        "Lab Attendance", javax.swing.JOptionPane.QUESTION_MESSAGE);
                if (input == null)
                    return;
                try {
                    labConducted = Integer.parseInt(input);
                    if (labConducted < 0 || labConducted > 16) {
                        javax.swing.JOptionPane.showMessageDialog(parentUI, "Error: Max limit is 16.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    javax.swing.JOptionPane.showMessageDialog(parentUI, "Invalid number.");
                }
            }

            while (true) {
                String input = javax.swing.JOptionPane.showInputDialog(parentUI,
                        "Enter Lab Classes ATTENDED (Max " + labConducted + "):",
                        "Lab Attendance", javax.swing.JOptionPane.QUESTION_MESSAGE);
                if (input == null)
                    return;
                try {
                    labAttended = Integer.parseInt(input);
                    if (labAttended < 0 || labAttended > labConducted) {
                        javax.swing.JOptionPane.showMessageDialog(parentUI, "Error: Attended cannot exceed Conducted.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    javax.swing.JOptionPane.showMessageDialog(parentUI, "Invalid number.");
                }
            }
        }

        // --- Step C: Update Object & Database ---
        AttendanceRecord record = getOrCreateRecord(reg);
        record.setTheoryConducted(theoryConducted);
        record.setTheoryAttended(theoryAttended);
        record.setLabConducted(labConducted);
        record.setLabAttended(labAttended);

        // Save to DB
        repository.DatabaseStudentRepository repo = new repository.DatabaseStudentRepository();
        repo.updateAttendance(reg.getId(), record);

        // --- Step C (continued): Status Message ---
        StringBuilder msg = new StringBuilder();
        if (record.getTheoryPercent() < 80) {
            msg.append("Short Attendance: Not allowed in Theory Exam.\n");
        } else {
            msg.append("Theory Attendance: Safe.\n");
        }

        if (reg.getCourse().isHasLab()) {
            if (record.getLabPercent() < 80) {
                msg.append("Short Attendance: Not allowed in Lab Exam.\n");
            } else {
                msg.append("Lab Attendance: Safe.\n");
            }
        }

        javax.swing.JOptionPane.showMessageDialog(parentUI, msg.toString());
    }

    private AttendanceRecord getOrCreateRecord(CourseRegistration reg) {
        if (reg.getAttendanceRecord() == null) {
            reg.setAttendanceRecord(new AttendanceRecord());
        }
        return reg.getAttendanceRecord();
    }
}
