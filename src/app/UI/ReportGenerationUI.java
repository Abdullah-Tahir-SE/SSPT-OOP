package app.UI;

import app.utils.StyleUtils;
import model.CourseRegistration;
import model.Student;
import service.ReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.print.PrinterException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerationUI extends JFrame {

    private final Student student;
    private final ReportService reportService;

    // Theme Colors
    private final Color NEON_CYAN = new Color(0, 229, 255);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color NAVY_TEXT = new Color(0, 18, 51);

    private JTextArea reportArea;
    private JComboBox<String> typeCombo;
    private JComboBox<String> courseCombo;
    private JComboBox<String> categoryCombo;

    public ReportGenerationUI(Student s) {
        this.student = s;
        this.reportService = new ReportService();

        setTitle("Report Generation - " + s.getFullName());
        setSize(900, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main Background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, StyleUtils.NAVY_PRIMARY, getWidth(), getHeight(),
                        StyleUtils.SLATE_BLUE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // --- Header ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Report Generation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(NEON_CYAN);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Glass Content Panel ---
        JPanel glassPanel = createGlassPanel();
        glassPanel.setLayout(new BorderLayout(15, 15));
        glassPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Wrapper for Glass Panel
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        wrapper.add(glassPanel);
        mainPanel.add(wrapper, BorderLayout.CENTER);

        // --- Filter Panel ---
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Report Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        filterPanel.add(createLabel("Report Type:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] types = { "Full Academic Report", "Course-wise Report", "Category-wise Report" };
        typeCombo = createStyledComboBox(types);
        filterPanel.add(typeCombo, gbc);

        // Row 2: Select Course
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        filterPanel.add(createLabel("Select Course (If Course-wise):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        courseCombo = createStyledComboBox(new String[] {});
        courseCombo.addItem("All Courses");
        for (CourseRegistration r : s.getRegistrations()) {
            courseCombo.addItem(r.getCourse().getCourseCode());
        }
        courseCombo.setEnabled(false);
        filterPanel.add(courseCombo, gbc);

        // Row 3: Select Category
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        filterPanel.add(createLabel("Select Category (If Category-wise):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] cats = { "Attendance Only", "Grades Only" };
        categoryCombo = createStyledComboBox(cats);
        categoryCombo.setEnabled(false);
        filterPanel.add(categoryCombo, gbc);

        glassPanel.add(filterPanel, BorderLayout.NORTH);

        // --- Main Text Area ---
        reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportArea.setEditable(false);
        reportArea.setMargin(new Insets(15, 15, 15, 15));
        reportArea.setBackground(Color.WHITE);
        reportArea.setForeground(NAVY_TEXT);
        reportArea.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        glassPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Buttons ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        // Buttons WITHOUT hover effects (Static Gradients)
        JButton btnGen = createNoHoverButton("Preview Report", new Color(0, 200, 255), new Color(0, 100, 200)); // Cyan/Blue
        JButton btnSaveInfo = createNoHoverButton("Save as Text", new Color(0, 180, 100), new Color(0, 100, 50)); // Green
        JButton btnPrint = createNoHoverButton("Print Report", new Color(100, 100, 120), new Color(60, 60, 80)); // Grey

        btnPanel.add(btnGen);
        btnPanel.add(btnSaveInfo);
        btnPanel.add(btnPrint);
        glassPanel.add(btnPanel, BorderLayout.SOUTH);

        // Logic
        typeCombo.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            boolean course = "Course-wise Report".equals(type);
            boolean cat = "Category-wise Report".equals(type);
            courseCombo.setEnabled(course);
            categoryCombo.setEnabled(cat);
        });

        btnGen.addActionListener(e -> generateReport());
        btnSaveInfo.addActionListener(e -> saveReport());
        btnPrint.addActionListener(e -> printReport());
    }

    private void generateReport() {
        if (student == null)
            return;
        repository.DatabaseStudentRepository repo = new repository.DatabaseStudentRepository();

        String type = (String) typeCombo.getSelectedItem();
        StringBuilder sb = new StringBuilder();

        sb.append("=================================================================\n");
        sb.append("                   SMART SEMESTER PROGRESS REPORT                \n");
        sb.append("=================================================================\n");
        sb.append("Generated On: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .append("\n");
        sb.append("Student: ").append(student.getFullName()).append(" (").append(student.getRegistrationNo())
                .append(")\n");
        sb.append("Degree : ").append(student.getDegree()).append(" | Semester: ").append(student.getSemester())
                .append("\n");
        sb.append("-----------------------------------------------------------------\n\n");

        if ("Full Academic Report".equals(type)) {
            appendFullReport(sb, repo);
        } else if ("Course-wise Report".equals(type)) {
            appendCourseReport(sb, repo);
        } else if ("Category-wise Report".equals(type)) {
            appendCategoryReport(sb);
        }

        sb.append("\n=================================================================\n");
        sb.append("                   END OF REPORT                                 \n");
        sb.append("=================================================================\n");

        reportArea.setText(sb.toString());
        reportArea.setCaretPosition(0);
    }

    private void appendFullReport(StringBuilder sb, repository.DatabaseStudentRepository repo) {
        sb.append("SEMESTER SUMMARY\n");
        sb.append("-----------------------------------------------------------------\n");

        double totalQP = student.getTotalQp();
        int totalCredits = student.getSemCredits();
        double sgpa = student.getCurrentSgpa();
        String semGrade = student.getSemesterGrade();

        if (semGrade == null || semGrade.isEmpty()) {
            semGrade = "N/A";
        }

        sb.append(String.format("Total Credits  : %d\n", totalCredits));
        sb.append(String.format("Total QP       : %.2f\n", totalQP));
        sb.append(String.format("Semester GPA   : %.2f\n", sgpa));
        sb.append(String.format("Semester Grade : %s\n", semGrade));
        sb.append("-----------------------------------------------------------------\n\n");

        sb.append("DETAILED COURSE PERFORMANCE\n");
        sb.append("=================================================================\n");

        for (CourseRegistration r : student.getRegistrations()) {
            appendCourseDetails(sb, repo, r.getCourse().getCourseCode());
            sb.append("-----------------------------------------------------------------\n");
        }
    }

    private void appendCourseReport(StringBuilder sb, repository.DatabaseStudentRepository repo) {
        String code = (String) courseCombo.getSelectedItem();
        if (code == null || code.equals("All Courses")) {
            for (CourseRegistration r : student.getRegistrations()) {
                appendCourseDetails(sb, repo, r.getCourse().getCourseCode());
                sb.append("-----------------------------------------------------------------\n");
            }
        } else {
            appendCourseDetails(sb, repo, code);
        }
    }

    private void appendCategoryReport(StringBuilder sb) {
        String cat = (String) categoryCombo.getSelectedItem();
        sb.append("CATEGORY: ").append(cat).append("\n\n");

        boolean showAtt = "Attendance Only".equals(cat);

        for (CourseRegistration r : student.getRegistrations()) {
            sb.append(String.format("[%s] %s\n", r.getCourse().getCourseCode(), r.getCourse().getTitle()));

            if (showAtt) {
                if (r.getAttendanceRecord() != null) {
                    sb.append("   - Theory Attendance : ")
                            .append(String.format("%.1f%%", r.getAttendanceRecord().getTheoryPercent()))
                            .append("\n");

                    if (r.getCourse().isHasLab()) {
                        sb.append("   - Lab Attendance    : ")
                                .append(String.format("%.1f%%", r.getAttendanceRecord().getLabPercent()))
                                .append("\n");
                    }
                } else {
                    sb.append("   - No attendance records.\n");
                }
            } else {
                // Grades Only - simpler view
                sb.append("   - Final Grade: ").append(r.getFinalGrade() != null ? r.getFinalGrade() : "N/A")
                        .append("\n");
                sb.append("   - GPA        : ").append(String.format("%.2f", r.getGpa())).append("\n");
            }
            sb.append("\n");
        }
    }

    private void appendCourseDetails(StringBuilder sb, repository.DatabaseStudentRepository repo, String courseCode) {
        model.CourseResult res = repo.getCourseResult(student.getUserId(), courseCode);
        if (res == null) {
            sb.append("No data found for ").append(courseCode).append("\n");
            return;
        }

        sb.append("[").append(res.getCourseCode()).append("] ").append(res.getCourseTitle()).append("\n");
        sb.append("Final Grade: ").append(res.getGrade() != null ? res.getGrade() : "N/A").append("   |   ");
        sb.append("GPA: ").append(String.format("%.2f", res.getGpa())).append("\n");

        sb.append("\n   [MARKS BREAKDOWN]\n");

        // Theory
        if (!res.getTheoryAssessments().isEmpty() || res.getTheoryMid() != null || res.getTheoryFinal() != null) {
            sb.append("   Theory:\n");
            for (model.Mark m : res.getTheoryAssessments()) {
                sb.append(String.format("      - %-15s: %.1f / %.1f\n", m.getLabel(), m.getObtained(),
                        m.getTotalMarks()));
            }
            if (res.getTheoryMid() != null) {
                sb.append(String.format("      - %-15s: %.1f / %.1f\n", "Mid Term", res.getTheoryMid().getObtained(),
                        res.getTheoryMid().getTotalMarks()));
            }
            if (res.getTheoryFinal() != null) {
                sb.append(String.format("      - %-15s: %.1f / %.1f\n", "Final Term",
                        res.getTheoryFinal().getObtained(), res.getTheoryFinal().getTotalMarks()));
            }
        }

        // Lab
        if (res.isHasLab()) {
            sb.append("\n   Lab:\n");
            for (model.Mark m : res.getLabAssessments()) {
                sb.append(String.format("      - %-15s: %.1f / %.1f\n", m.getLabel(), m.getObtained(),
                        m.getTotalMarks()));
            }
            if (res.getLabMid() != null) {
                sb.append(String.format("      - %-15s: %.1f / %.1f\n", "Lab Mid", res.getLabMid().getObtained(),
                        res.getLabMid().getTotalMarks()));
            }
            if (res.getLabFinal() != null) {
                sb.append(String.format("      - %-15s: %.1f / %.1f\n", "Lab Final", res.getLabFinal().getObtained(),
                        res.getLabFinal().getTotalMarks()));
            }
        }

        sb.append(String.format("\n   TOTAL OBTAINED: %.1f / %.1f\n", res.getTotalObtained(), res.getTotalMax()));
    }

    private void saveReport() {
        String txt = reportArea.getText();
        if (txt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Generate a report first.");
            return;
        }

        String fname = "Report_" + student.getRegistrationNo() + "_" + System.currentTimeMillis() + ".txt";
        try (FileWriter fw = new FileWriter(fname)) {
            fw.write(txt);
            JOptionPane.showMessageDialog(this, "Saved to " + fname);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
        }
    }

    private void printReport() {
        String txt = reportArea.getText();
        if (txt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Generate a report first.");
            return;
        }

        try {
            boolean complete = reportArea.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "Printing Complete.");
            } else {
                JOptionPane.showMessageDialog(this, "Printing Cancelled.");
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Printing Failed: " + e.getMessage());
        }
    }

    // --- Styled Components ---

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(NEON_CYAN);
        return l;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(Color.WHITE);
        cb.setForeground(NAVY_TEXT);
        return cb;
    }

    private JButton createNoHoverButton(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Static Gradient - No Hover Check
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 40));
        return btn;
    }

    private JPanel createGlassPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 15)); // Glass White
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
    }
}
