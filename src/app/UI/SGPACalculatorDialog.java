package app.UI;

import app.utils.StyleUtils;
import model.Student;
import model.CourseRegistration;
import model.CourseResult;
import repository.DatabaseStudentRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class SGPACalculatorDialog extends JDialog {

    private final Student student;
    private final DatabaseStudentRepository repo;

    // Theme Colors
    private final Color SIDEBAR_COLOR = new Color(0, 18, 51, 220); // Deep Navy Glass
    private final Color ACCENT_GRADIENT_1 = StyleUtils.SLATE_BLUE;
    private final Color ACCENT_GRADIENT_2 = StyleUtils.NAVY_PRIMARY;
    private final Color NEON_CYAN = new Color(0, 229, 255);

    public SGPACalculatorDialog(Window owner, Student s) {
        super(owner, "Semester Result Card", ModalityType.APPLICATION_MODAL);
        this.student = s;
        this.repo = new DatabaseStudentRepository();

        setSize(800, 600);
        setLocationRelativeTo(owner);

        // Main Gradient Background
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
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(getWidth(), 80));
        header.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel title = new JLabel("Semester Result: " + s.getFullName(), SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(title, BorderLayout.CENTER);
        mainPanel.add(header, BorderLayout.NORTH);

        // --- Content - Table ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        String[] cols = { "Code", "Course Name", "Cr. Hrs", "GPA", "Grade" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        double totalQP = 0;
        int totalGradedCredits = 0;
        int totalRegisteredCredits = 0;

        List<CourseRegistration> regs = s.getRegistrations();
        for (CourseRegistration reg : regs) {
            String grade = reg.getFinalGrade() != null ? reg.getFinalGrade() : "N/A";
            double gpa = reg.getGpa(); // Assuming this is populated from DB or calc

            int credits = reg.getCourse().getCreditHours();
            totalRegisteredCredits += credits; // Count all registered credits

            model.addRow(new Object[] {
                    reg.getCourse().getCourseCode(),
                    reg.getCourse().getTitle(),
                    credits,
                    String.format("%.2f", gpa),
                    grade
            });

            if (gpa > 0) {
                totalQP += (gpa * credits);
                totalGradedCredits += credits; // Count only graded credits for SGPA
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 240));
        table.setShowVerticalLines(false);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 14));
        th.setBackground(StyleUtils.NAVY_PRIMARY); // Navy Header
        th.setForeground(Color.WHITE);
        th.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 5; i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        // Wrap in Glass Effect
        JPanel glassTableWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 20)); // Subtle white glass
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };
        glassTableWrapper.setOpaque(false);
        glassTableWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        glassTableWrapper.add(scroll);

        tablePanel.add(glassTableWrapper, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // --- Footer - Calculation ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 30, 20, 30));

        // Stats Panel (Glass)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_COLOR);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(15, 10, 15, 10));
        statsPanel.setPreferredSize(new Dimension(getWidth(), 100));

        final double finalSGPA = totalGradedCredits > 0 ? totalQP / totalGradedCredits : 0.0;

        addStat(statsPanel, "Total Credits", String.valueOf(totalRegisteredCredits));
        addStat(statsPanel, "Total QP", String.format("%.2f", totalQP));
        addStat(statsPanel, "SGPA", String.format("%.2f", finalSGPA));

        // Grade Logic (Approx)
        String semGrade = "F";
        if (finalSGPA >= 4.0)
            semGrade = "A";
        else if (finalSGPA >= 3.7)
            semGrade = "A-";
        else if (finalSGPA >= 3.3)
            semGrade = "B+";
        else if (finalSGPA >= 3.0)
            semGrade = "B";
        else if (finalSGPA >= 2.7)
            semGrade = "B-";
        else if (finalSGPA >= 2.3)
            semGrade = "C+";
        else if (finalSGPA >= 2.0)
            semGrade = "C";
        else if (finalSGPA >= 1.7)
            semGrade = "C-";
        else if (finalSGPA >= 1.0)
            semGrade = "D";

        addStat(statsPanel, "Sem Grade", semGrade);
        footer.add(statsPanel, BorderLayout.CENTER);

        // Save Button
        JButton btnSave = new JButton("Save Result") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_GRADIENT_1, getWidth(), 0, ACCENT_GRADIENT_2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setContentAreaFilled(false);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSave.setPreferredSize(new Dimension(200, 50));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final double closingTotalQP = totalQP;
        final String closingSemGrade = semGrade;
        final int closingCredits = totalRegisteredCredits;

        btnSave.addActionListener(e -> {
            // Save to database
            this.repo.updateSemesterResult(student.getUserId(), finalSGPA, closingTotalQP, closingSemGrade,
                    closingCredits);
            student.setCurrentSgpa(finalSGPA);
            student.setTotalQp(closingTotalQP);
            student.setSemesterGrade(closingSemGrade);
            student.setSemCredits(closingCredits);

            JOptionPane.showMessageDialog(this, "Result saved successfully!");
            dispose();
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        btnPanel.add(btnSave);
        footer.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(footer, BorderLayout.SOUTH);
    }

    private void addStat(JPanel p, String title, String val) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setForeground(new Color(200, 200, 220)); // Light Grey

        JLabel v = new JLabel(val, SwingConstants.CENTER);
        v.setFont(new Font("Segoe UI", Font.BOLD, 22));
        v.setForeground(NEON_CYAN); // Neon Cyan Value

        item.add(t, BorderLayout.NORTH);
        item.add(v, BorderLayout.CENTER);
        p.add(item);
    }
}
