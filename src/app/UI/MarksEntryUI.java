package app.UI;

import app.utils.StyleUtils;
import model.AssessmentType;
import model.CourseRegistration;
import model.Mark;
import model.Student;
import repository.DatabaseStudentRepository;
import service.MarksCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MarksEntryUI extends JFrame {

    private final Student student;
    private final DatabaseStudentRepository repository;
    private final MarksCalculator calculator;

    private JComboBox<String> courseCombo;
    private JPanel contentArea;

    // Dynamic Inputs
    private List<ComponentGroup> theoryInputs = new ArrayList<>();
    private List<ComponentGroup> labInputs = new ArrayList<>();

    // Theme Colors
    private final Color SIDEBAR_COLOR = new Color(0, 18, 51, 220); // Deep Navy Glass
    private final Color ACCENT_GRADIENT_1 = StyleUtils.SLATE_BLUE;
    private final Color ACCENT_GRADIENT_2 = StyleUtils.NAVY_PRIMARY;
    private final Color NEON_CYAN = new Color(0, 229, 255);

    public MarksEntryUI(Student s) {
        this.student = s;
        this.repository = new DatabaseStudentRepository();
        this.calculator = new MarksCalculator();

        setTitle("Advanced Marks Entry");
        setSize(1000, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
        header.setBorder(new EmptyBorder(20, 30, 0, 30));

        JLabel title = new JLabel("Marks & GPA Calculation", SwingConstants.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title, BorderLayout.WEST);

        mainPanel.add(header, BorderLayout.NORTH);

        // --- Top: Course Selection (Inside Main for better layout) ---
        JPanel topControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topControlPanel.setOpaque(false);
        topControlPanel.setBorder(new EmptyBorder(0, 30, 10, 30));

        JLabel lblCourse = new JLabel("Select Course:");
        lblCourse.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCourse.setForeground(NEON_CYAN);
        topControlPanel.add(lblCourse);

        courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(350, 40));
        courseCombo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        courseCombo.setBackground(Color.WHITE);
        courseCombo.setForeground(StyleUtils.NAVY_PRIMARY);

        courseCombo.addItem("Select a Course");
        for (CourseRegistration reg : s.getRegistrations()) {
            courseCombo.addItem(reg.getCourse().getCourseCode() + " - " + reg.getCourse().getTitle());
        }
        courseCombo.addActionListener(e -> loadCourseInterface());
        topControlPanel.add(courseCombo);

        // Wrap header and top control
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.setOpaque(false);
        northContainer.add(header);
        northContainer.add(topControlPanel);

        mainPanel.add(northContainer, BorderLayout.NORTH);

        // --- Center: Scrollable Content ---
        contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(10, 30, 10, 30));

        JScrollPane scroll = new JScrollPane(contentArea);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // --- Footer: Actions ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 0, 10, 30));

        JButton btnCalc = createStyledButton("Calculate GPA", ACCENT_GRADIENT_1, ACCENT_GRADIENT_2); // Cyan/Blue
        JButton btnSave = createStyledButton("Save Marks", new Color(0, 150, 80), new Color(0, 120, 60)); // Green
        JButton btnClose = createStyledButton("Close", new Color(100, 100, 100), new Color(70, 70, 70)); // Grey

        btnCalc.addActionListener(e -> calculateGPA());
        btnSave.addActionListener(e -> saveMarks());
        btnClose.addActionListener(e -> dispose());

        footer.add(btnCalc);
        footer.add(btnSave);
        footer.add(btnClose);
        mainPanel.add(footer, BorderLayout.SOUTH);
    }

    private void loadCourseInterface() {
        contentArea.removeAll();
        theoryInputs.clear();
        labInputs.clear();

        String selected = (String) courseCombo.getSelectedItem();
        if (selected == null || selected.equals("Select a Course")) {
            contentArea.revalidate();
            contentArea.repaint();
            return;
        }

        String code = selected.split(" - ")[0];
        CourseRegistration reg = findRegistration(code);
        if (reg == null)
            return;

        // --- Theory Section ---
        JPanel theoryPanel = createSectionPanel("Theory Configuration (100 Marks)");
        addConfigRow(theoryPanel, "Assignments (10%)", AssessmentType.ASSIGNMENT, reg, theoryInputs);
        addConfigRow(theoryPanel, "Quizzes (15%)", AssessmentType.QUIZ, reg, theoryInputs);
        addFixedRow(theoryPanel, "Mid Term (25%)", AssessmentType.MID, reg, theoryInputs, 25);
        addFixedRow(theoryPanel, "Final Term (50%)", AssessmentType.FINAL, reg, theoryInputs, 50);
        contentArea.add(theoryPanel);
        contentArea.add(Box.createVerticalStrut(20));

        // --- Lab Section ---
        if (reg.getCourse().isHasLab()) {
            JPanel labPanel = createSectionPanel("Lab Configuration (100 Marks)");
            addConfigRow(labPanel, "Lab Assignments (25%)", AssessmentType.LAB_ASSIGNMENT, reg, labInputs);
            addFixedRow(labPanel, "Lab Mid (25%)", AssessmentType.LAB_MID, reg, labInputs, 25);
            addFixedRow(labPanel, "Lab Final (50%)", AssessmentType.LAB_FINAL, reg, labInputs, 50);
            contentArea.add(labPanel);
        }

        contentArea.revalidate();
        contentArea.repaint();
    }

    // --- Dynamic UI Makers ---

    private JPanel createSectionPanel(String title) {
        // Glass Panel Container
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_COLOR);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Label
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(NEON_CYAN);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lblTitle);
        p.add(Box.createVerticalStrut(15));

        return p;
    }

    private void addConfigRow(JPanel parent, String title, AssessmentType type, CourseRegistration reg,
            List<ComponentGroup> tracker) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(title + ": ");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        lbl.setPreferredSize(new Dimension(200, 30));

        JButton btnSet = createStyledButton("Set Count", new Color(100, 50, 180), new Color(70, 30, 140)); // Purple-ish
        btnSet.setPreferredSize(new Dimension(100, 30));
        btnSet.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JTextField txtCount = createTextField(3);
        txtCount.setText("0");

        // Items Container (will hold the dynamic rows)
        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setOpaque(false);
        itemsContainer.setBorder(new EmptyBorder(5, 20, 5, 0));
        itemsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSet.addActionListener(e -> {
            try {
                int count = Integer.parseInt(txtCount.getText().trim());
                itemsContainer.removeAll();

                // Remove old inputs from tracker for this type
                tracker.removeIf(cg -> cg.type == type);

                for (int i = 1; i <= count; i++) {
                    ComponentGroup cg = new ComponentGroup();
                    cg.type = type;
                    cg.label = type.toString() + " " + i; // Default label

                    JPanel itemRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    itemRow.setOpaque(false);

                    JLabel l = new JLabel("#" + i + " Total:");
                    l.setForeground(new Color(220, 220, 255));

                    JTextField tTotal = createTextField(4);
                    tTotal.setText("10");

                    JLabel lObj = new JLabel("Obt:");
                    lObj.setForeground(new Color(220, 220, 255));

                    JTextField tObt = createTextField(4);
                    tObt.setText("0");

                    cg.txtTotal = tTotal;
                    cg.txtObtained = tObt;

                    itemRow.add(l);
                    itemRow.add(tTotal);
                    itemRow.add(lObj);
                    itemRow.add(tObt);
                    itemsContainer.add(itemRow);

                    tracker.add(cg);
                }
                itemsContainer.revalidate();
                itemsContainer.repaint();
                parent.revalidate(); // Resize parent
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number");
            }
        });

        // Pre-load Logic
        List<Mark> existing = reg.getMarks();
        long existingCount = existing.stream().filter(m -> m.getType() == type).count();
        if (existingCount > 0) {
            txtCount.setText(String.valueOf(existingCount));
            itemsContainer.removeAll();
            tracker.removeIf(cg -> cg.type == type);

            int i = 1;
            for (Mark m : existing) {
                if (m.getType() == type) {
                    ComponentGroup cg = new ComponentGroup();
                    cg.type = type;
                    cg.label = m.getLabel() != null ? m.getLabel() : (type.toString() + " " + i);

                    JPanel itemRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    itemRow.setOpaque(false);

                    JLabel l = new JLabel(cg.label + " Total:");
                    l.setForeground(new Color(220, 220, 255));

                    JTextField tTotal = createTextField(4);
                    tTotal.setText(String.valueOf(m.getTotalMarks()));

                    JLabel lObj = new JLabel("Obt:");
                    lObj.setForeground(new Color(220, 220, 255));

                    JTextField tObt = createTextField(4);
                    tObt.setText(String.valueOf(m.getObtained()));

                    cg.txtTotal = tTotal;
                    cg.txtObtained = tObt;

                    itemRow.add(l);
                    itemRow.add(tTotal);
                    itemRow.add(lObj);
                    itemRow.add(tObt);
                    itemsContainer.add(itemRow);
                    tracker.add(cg);
                    i++;
                }
            }
        }

        row.add(lbl);

        JLabel lblCount = new JLabel("Count:");
        lblCount.setForeground(Color.WHITE);
        row.add(lblCount);

        row.add(txtCount);
        row.add(btnSet);

        parent.add(row);
        parent.add(itemsContainer);
    }

    private void addFixedRow(JPanel parent, String title, AssessmentType type, CourseRegistration reg,
            List<ComponentGroup> tracker, double fixedTotal) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        lbl.setPreferredSize(new Dimension(200, 30));

        JLabel lTotal = new JLabel("Total: " + fixedTotal + "   Obtained: ");
        lTotal.setForeground(new Color(200, 200, 220));
        lTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField tObt = createTextField(5);

        ComponentGroup cg = new ComponentGroup();
        cg.type = type;
        cg.label = title;
        cg.txtObtained = tObt;
        cg.fixedTotal = fixedTotal; // Used instead of text field

        // Pre-fill
        for (Mark m : reg.getMarks()) {
            if (m.getType() == type) {
                tObt.setText(String.valueOf(m.getObtained()));
                break;
            }
        }

        tracker.add(cg); // Add to tracker immediately as it's fixed

        row.add(lbl);
        row.add(lTotal);
        row.add(tObt);
        parent.add(row);
    }

    // --- Logic ---

    private void calculateGPA() {
        List<Mark> tempTheory = extractMarks(theoryInputs);
        List<Mark> tempLab = extractMarks(labInputs);

        double theoryTotal = calculator.calculateTheoryTotal(tempTheory);
        double labTotal = calculator.calculateLabTotal(tempLab);

        String msg = String.format("Theory Total (Weighted): %.2f / 100\n", theoryTotal);

        double finalScore = theoryTotal;
        String selected = (String) courseCombo.getSelectedItem();
        CourseRegistration reg = findRegistration(selected.split(" - ")[0]);

        if (reg != null && reg.getCourse().isHasLab()) {
            msg += String.format("Lab Total (Weighted): %.2f / 100\n", labTotal);
            finalScore = (theoryTotal * 0.75) + (labTotal * 0.25);
            msg += String.format("Final Score (75%% Th + 25%% Lab): %.2f\n", finalScore);
        } else {
            msg += String.format("Final Score: %.2f\n", finalScore);
        }

        String grade = calculator.calculateGrade(finalScore);
        double gpa = calculator.gradeToGPA(grade);

        // Open Custom Dialog
        new GpaResultDialog(this, msg, grade, gpa).setVisible(true);

        if (reg != null) {
            try {
                repository.updateGrade(reg.getId(), grade, gpa);
                reg.setFinalGrade(grade);
                reg.setGpa(gpa);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveMarks() {
        String selected = (String) courseCombo.getSelectedItem();
        if (selected == null)
            return;
        String code = selected.split(" - ")[0];
        CourseRegistration reg = findRegistration(code);
        if (reg == null)
            return;

        List<Mark> allMarks = new ArrayList<>();
        allMarks.addAll(extractMarks(theoryInputs));
        allMarks.addAll(extractMarks(labInputs));

        try {
            repository.updateMarks(reg.getId(), allMarks);

            reg.getMarks().clear();
            reg.getMarks().addAll(allMarks);

            JOptionPane.showMessageDialog(this, "Marks Saved Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage());
        }
    }

    private List<Mark> extractMarks(List<ComponentGroup> inputs) {
        List<Mark> list = new ArrayList<>();
        for (ComponentGroup cg : inputs) {
            try {
                double obt = Double.parseDouble(cg.txtObtained.getText().trim());
                double tot = cg.txtTotal != null ? Double.parseDouble(cg.txtTotal.getText().trim()) : cg.fixedTotal;
                list.add(new Mark(cg.type, obt, tot, cg.label));
            } catch (Exception e) {
                // Ignore empty/invalid for calc preview, or treat as 0
            }
        }
        return list;
    }

    private CourseRegistration findRegistration(String code) {
        for (CourseRegistration r : student.getRegistrations()) {
            if (r.getCourse().getCourseCode().equals(code))
                return r;
        }
        return null;
    }

    private JButton createStyledButton(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBackground(c1);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(150, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTextField createTextField(int columns) {
        JTextField t = new JTextField(columns);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setBackground(Color.WHITE);
        t.setForeground(StyleUtils.NAVY_PRIMARY);
        t.setCaretColor(StyleUtils.NAVY_PRIMARY);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 255), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return t;
    }

    // Helper Class
    private class ComponentGroup {
        AssessmentType type;
        String label;
        JTextField txtTotal;
        JTextField txtObtained;
        double fixedTotal;
    }
}
