package app.UI;

import app.utils.StyleUtils;
import model.AttendanceRecord;
import model.CourseRegistration;
import model.Student;
import repository.DatabaseStudentRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;

public class AttendanceEntryDialog extends JDialog {

    private Student student;
    private CourseRegistration currentRegistration;
    private JComboBox<String> courseCombo;

    private JTextField txtTheoryConducted;
    private JTextField txtTheoryAttended;
    private JTextField txtLabConducted;
    private JTextField txtLabAttended;
    private DonutChartPanel theoryChart;
    private DonutChartPanel labChart;
    private JButton btnSave;

    // Panel references to toggle lab visibility
    private JPanel labPanel;
    private JPanel resultPanel;

    // Theme Colors
    private final Color SIDEBAR_COLOR = new Color(0, 18, 51, 220); // Deep Navy
    private final Color ACCENT_GRADIENT_1 = StyleUtils.SLATE_BLUE;
    private final Color ACCENT_GRADIENT_2 = StyleUtils.NAVY_PRIMARY;
    private final Color NEON_CYAN = new Color(0, 229, 255);

    public AttendanceEntryDialog(JFrame parent, Student student) {
        super(parent, "Attendance Entry", true);
        this.student = student;

        setSize(1000, 700);
        setLocationRelativeTo(parent);

        // Main Background with Gradient
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
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(getWidth(), 90));

        JLabel title = new JLabel("Attendance For:");
        title.setForeground(Color.WHITE); // White text
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title);

        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        courseCombo.setPreferredSize(new Dimension(350, 40));
        courseCombo.setBackground(Color.WHITE);
        courseCombo.setForeground(StyleUtils.NAVY_PRIMARY);

        // Populate Combo
        for (CourseRegistration reg : student.getRegistrations()) {
            courseCombo.addItem(reg.getCourse().getTitle());
        }

        courseCombo.addActionListener(e -> loadCourseData());
        header.add(courseCombo);

        mainPanel.add(header, BorderLayout.NORTH);

        // --- Main Content ---
        JPanel content = new JPanel(new GridLayout(1, 2, 40, 0));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 40, 40, 40));
        mainPanel.add(content, BorderLayout.CENTER);

        // --- Left: Input Section (Glass Panel) ---
        JPanel leftContainer = createGlassPanel();
        leftContainer.setLayout(new BoxLayout(leftContainer, BoxLayout.Y_AXIS));
        leftContainer.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Theory
        leftContainer.add(createSectionLabel("Theory Classes (Max 32)"));
        leftContainer.add(Box.createVerticalStrut(15));
        txtTheoryConducted = createTextField();
        txtTheoryAttended = createTextField();
        leftContainer.add(createInputRow("Conducted:", txtTheoryConducted));
        leftContainer.add(Box.createVerticalStrut(10));
        leftContainer.add(createInputRow("Attended:", txtTheoryAttended));

        leftContainer.add(Box.createVerticalStrut(30));

        // Lab
        labPanel = new JPanel();
        labPanel.setLayout(new BoxLayout(labPanel, BoxLayout.Y_AXIS));
        labPanel.setOpaque(false);

        labPanel.add(createSectionLabel("Lab Classes (Max 16)"));
        labPanel.add(Box.createVerticalStrut(15));
        txtLabConducted = createTextField();
        txtLabAttended = createTextField();
        labPanel.add(createInputRow("Conducted:", txtLabConducted));
        labPanel.add(Box.createVerticalStrut(10));
        labPanel.add(createInputRow("Attended:", txtLabAttended));

        leftContainer.add(labPanel);
        leftContainer.add(Box.createVerticalStrut(30));
        leftContainer.add(Box.createVerticalGlue());

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);

        JButton btnCalculate = createStyledButton("Calculate", ACCENT_GRADIENT_1, ACCENT_GRADIENT_2);
        btnCalculate.addActionListener(e -> calculate());

        btnSave = createStyledButton("Save Changes", new Color(0, 180, 100), new Color(0, 140, 80));
        btnSave.setEnabled(false);
        btnSave.addActionListener(e -> save());

        btnPanel.add(btnCalculate);
        btnPanel.add(btnSave);

        leftContainer.add(btnPanel);
        content.add(leftContainer);

        // --- Right: Result Section (Visuals) ---
        resultPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        resultPanel.setOpaque(false);

        theoryChart = new DonutChartPanel("Theory Status");
        labChart = new DonutChartPanel("Lab Status");

        resultPanel.add(theoryChart);
        resultPanel.add(labChart);

        content.add(resultPanel);

        // Initial Load
        loadCourseData();
    }

    private JPanel createGlassPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_COLOR); // Semi-transparent Navy
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
            }
        };
        p.setOpaque(false);
        return p;
    }

    private void loadCourseData() {
        String selectedTitle = (String) courseCombo.getSelectedItem();
        if (selectedTitle == null)
            return;

        currentRegistration = student.getRegistrations().stream()
                .filter(r -> r.getCourse().getTitle().equals(selectedTitle))
                .findFirst()
                .orElse(null);

        if (currentRegistration == null)
            return;

        AttendanceRecord rec = currentRegistration.getAttendanceRecord();
        if (rec != null) {
            txtTheoryConducted.setText(String.valueOf(rec.getTheoryConducted()));
            txtTheoryAttended.setText(String.valueOf(rec.getTheoryAttended()));
            theoryChart.updateProgress(rec.getTheoryPercent());
        } else {
            txtTheoryConducted.setText("0");
            txtTheoryAttended.setText("0");
            theoryChart.updateProgress(0);
        }

        boolean hasLab = currentRegistration.getCourse().isHasLab();
        labPanel.setVisible(hasLab);
        labChart.setVisible(hasLab);

        if (hasLab) {
            if (rec != null) {
                txtLabConducted.setText(String.valueOf(rec.getLabConducted()));
                txtLabAttended.setText(String.valueOf(rec.getLabAttended()));
                labChart.updateProgress(rec.getLabPercent());
            } else {
                txtLabConducted.setText("0");
                txtLabAttended.setText("0");
                labChart.updateProgress(0);
            }
        }

        btnSave.setEnabled(false);
        revalidate();
        repaint();
    }

    private void calculate() {
        if (currentRegistration == null)
            return;

        try {
            int tc = Integer.parseInt(txtTheoryConducted.getText());
            int ta = Integer.parseInt(txtTheoryAttended.getText());

            if (tc < 0 || tc > 32) {
                JOptionPane.showMessageDialog(this, "Theory Conducted must be 0-32");
                return;
            }
            if (ta < 0 || ta > tc) {
                JOptionPane.showMessageDialog(this, "Theory Attended cannot exceed Conducted");
                return;
            }

            double theoryPct = (tc == 0) ? 0 : (double) ta / tc * 100;
            theoryChart.updateProgress(theoryPct);

            if (currentRegistration.getCourse().isHasLab()) {
                int lc = Integer.parseInt(txtLabConducted.getText());
                int la = Integer.parseInt(txtLabAttended.getText());

                if (lc < 0 || lc > 16) {
                    JOptionPane.showMessageDialog(this, "Lab Conducted must be 0-16");
                    return;
                }
                if (la < 0 || la > lc) {
                    JOptionPane.showMessageDialog(this, "Lab Attended cannot exceed Conducted");
                    return;
                }

                double labPct = (lc == 0) ? 0 : (double) la / lc * 100;
                labChart.updateProgress(labPct);
            }

            btnSave.setEnabled(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
        }
    }

    private void save() {
        if (currentRegistration == null)
            return;

        try {
            int tc = Integer.parseInt(txtTheoryConducted.getText());
            int ta = Integer.parseInt(txtTheoryAttended.getText());

            AttendanceRecord record = currentRegistration.getAttendanceRecord();
            if (record == null) {
                record = new AttendanceRecord();
                currentRegistration.setAttendanceRecord(record);
            }

            record.setTheoryConducted(tc);
            record.setTheoryAttended(ta);

            if (currentRegistration.getCourse().isHasLab()) {
                int lc = Integer.parseInt(txtLabConducted.getText());
                int la = Integer.parseInt(txtLabAttended.getText());
                record.setLabConducted(lc);
                record.setLabAttended(la);
            } else {
                record.setLabConducted(0);
                record.setLabAttended(0);
            }

            DatabaseStudentRepository repo = new DatabaseStudentRepository();
            repo.updateAttendance(currentRegistration.getId(), record);

            JOptionPane.showMessageDialog(this, "Attendance Saved Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data.");
        }
    }

    // --- Components ---
    private JLabel createSectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(NEON_CYAN); // Neon Cyan for section headers
        return l;
    }

    private JPanel createInputRow(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(100, 40));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(Color.WHITE); // White label text

        p.add(l, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField createTextField() {
        JTextField t = new JTextField();
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setBackground(Color.WHITE);
        t.setForeground(StyleUtils.NAVY_PRIMARY);
        t.setCaretColor(StyleUtils.NAVY_PRIMARY);
        // Manual rounded border
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 255), 1, true), // Rounded-ish line border
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return t;
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
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 45));
        return btn;
    }

    // --- Chart ---
    private class DonutChartPanel extends JPanel {
        private String title;
        private double percentage = 0;

        public DonutChartPanel(String title) {
            this.title = title;
            setOpaque(false);
            setPreferredSize(new Dimension(250, 250));
        }

        public void updateProgress(double pct) {
            this.percentage = pct;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!isVisible())
                return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Glass Background for Chart
            g2.setColor(SIDEBAR_COLOR);
            g2.fillRoundRect(0, 0, w, h, 20, 20);

            // Chart Logic
            int diameter = Math.min(w, h) - 60;
            int x = (w - diameter) / 2;
            int y = (h - diameter) / 2 + 10;
            int thickness = 22;

            // Track
            g2.setColor(new Color(255, 255, 255, 30)); // White transparent track
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(x + thickness / 2, y + thickness / 2, diameter - thickness, diameter - thickness);

            // Progress - PRESERVING LOGIC: < 80 is Short (Red), else Green/Cyan
            Color progressColor = (percentage < 80) ? new Color(255, 80, 80) // Red
                    : new Color(0, 229, 255); // Neon Cyan for Safe
            g2.setColor(progressColor);

            int angle = (int) -(percentage * 3.6);
            g2.draw(new Arc2D.Double(x + thickness / 2, y + thickness / 2, diameter - thickness, diameter - thickness,
                    90, angle, Arc2D.OPEN));

            // Text inside
            String pctText = String.format("%.1f%%", percentage);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            FontMetrics fm = g2.getFontMetrics();

            g2.setColor(Color.WHITE);
            g2.drawString(pctText, w / 2 - fm.stringWidth(pctText) / 2, h / 2 + 5);

            // Status Logic: < 80 is Short, else Safe
            String status = (percentage < 80) ? "Short Attendance" : "Safe";
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fm = g2.getFontMetrics();
            g2.setColor(new Color(200, 200, 220));
            g2.drawString(status, w / 2 - fm.stringWidth(status) / 2, h / 2 + 30);

            // Title
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(NEON_CYAN); // Neon Cyan Title
            fm = g2.getFontMetrics();
            g2.drawString(title, w / 2 - fm.stringWidth(title) / 2, 35);
        }
    }
}
