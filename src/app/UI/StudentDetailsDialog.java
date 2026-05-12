package app.UI;

import model.Student;
import model.CourseRegistration;
import model.CourseResult;
import repository.DatabaseStudentRepository; // Explicit import
import app.utils.StyleUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

@SuppressWarnings("serial")
public class StudentDetailsDialog extends JFrame {

    // Theme Colors
    private final Color NEON_CYAN = new Color(0, 229, 255);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color NAVY_TEXT = new Color(0, 18, 51);

    private Student student;

    public StudentDetailsDialog(Frame parent, Student s) {
        super("My Profile: " + s.getFullName());
        this.student = s;
        setSize(950, 750);
        setLocationRelativeTo(parent);
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

        // --- Custom Header ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setOpaque(false);

        JLabel title = new JLabel(s.getFullName() + " - Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(NEON_CYAN);
        header.add(title);
        mainPanel.add(header, BorderLayout.NORTH);

        // --- Content Area (Replaces TabbedPane since we had only 1 tab) ---
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(0, 20, 20, 20));

        contentArea.add(createProfilePanel(s), BorderLayout.CENTER);
        mainPanel.add(contentArea, BorderLayout.CENTER);

        // --- Footer ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footer.setOpaque(false);

        JButton close = createNoHoverButton("Close", new Color(200, 50, 50), new Color(150, 0, 0)); // Red Gradient
        close.addActionListener(e -> dispose());
        footer.add(close);
        mainPanel.add(footer, BorderLayout.SOUTH);
    }

    private JPanel createProfilePanel(Student s) {
        // Main Container for Profile + Courses
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();

        // 1. Personal Info Card (Glass)
        JPanel infoCard = createGlassPanel();
        infoCard.setLayout(new GridLayout(2, 2, 10, 5));
        infoCard.setBorder(new EmptyBorder(20, 25, 20, 25));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font valFont = new Font("Segoe UI", Font.BOLD, 16);

        addInfoField(infoCard, "Full Name:", s.getFullName(), labelFont, valFont);
        addInfoField(infoCard, "Registration No:", s.getRegistrationNo(), labelFont, valFont);
        addInfoField(infoCard, "Program:", s.getProgramLevel() + " " + s.getDegree(), labelFont, valFont);
        addInfoField(infoCard, "Semester:", String.valueOf(s.getSemester()), labelFont, valFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 20, 0);
        container.add(infoCard, gbc);

        // 2. Courses Grid
        JPanel coursesContainer = new JPanel(new GridLayout(0, 2, 15, 15));
        coursesContainer.setOpaque(false);

        List<CourseRegistration> regs = s.getRegistrations();
        if (regs.isEmpty()) {
            JLabel noCourses = new JLabel("No registered courses found.", SwingConstants.CENTER);
            noCourses.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noCourses.setForeground(new Color(200, 200, 255));

            gbc.gridy = 1;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            container.add(noCourses, gbc);
        } else {
            for (CourseRegistration reg : regs) {
                coursesContainer.add(new CourseProgressCard(reg));
            }

            // Wrapper for alignment
            JPanel scrollWrapper = new JPanel(new BorderLayout());
            scrollWrapper.setOpaque(false);
            scrollWrapper.add(coursesContainer, BorderLayout.NORTH);

            JScrollPane scroll = new JScrollPane(scrollWrapper);
            scroll.setBorder(null);
            scroll.getViewport().setOpaque(false);
            scroll.setOpaque(false);
            scroll.getVerticalScrollBar().setUnitIncrement(16);

            gbc.gridy = 1;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            container.add(scroll, gbc);
        }

        return container;
    }

    private void addInfoField(JPanel p, String label, String value, Font lf, Font vf) {
        JPanel field = new JPanel(new BorderLayout());
        field.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(lf);
        l.setForeground(new Color(200, 220, 255)); // Light Blue Label

        JLabel v = new JLabel(value);
        v.setFont(vf);
        v.setForeground(NEON_CYAN); // Neon Value

        field.add(l, BorderLayout.NORTH);
        field.add(v, BorderLayout.CENTER);
        p.add(field);
    }

    private void openCourseResult(String courseCode) {
        if (student == null)
            return;
        DatabaseStudentRepository repo = new DatabaseStudentRepository();
        CourseResult res = repo.getCourseResult(student.getUserId(), courseCode);
        if (res != null) {
            new CourseResultDialog(this, res).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "No result data found for this course.");
        }
    }

    // --- Helper Methods ---

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
        btn.setPreferredSize(new Dimension(100, 40));
        return btn;
    }

    @SuppressWarnings("serial")
    private class CourseProgressCard extends JPanel {
        private final CourseRegistration reg;

        public CourseProgressCard(CourseRegistration reg) {
            this.reg = reg;
            setPreferredSize(new Dimension(300, 100)); // Fixed height
            setOpaque(false); // Transparent to show paintComponent
            this.setToolTipText("Click to view detailed results for " + reg.getCourse().getTitle());
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));

            this.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    openCourseResult(reg.getCourse().getCourseCode());
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. Background Card (Semi-transparent white)
            g2.setColor(new Color(255, 255, 255, 25)); // Glass Effect
            g2.fillRoundRect(2, 2, w - 5, h - 5, 20, 20);

            // Border
            g2.setColor(new Color(255, 255, 255, 40));
            g2.drawRoundRect(2, 2, w - 5, h - 5, 20, 20);

            // 2. Text
            g2.setColor(NEON_CYAN);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.drawString(reg.getCourse().getCourseCode(), 15, 25);

            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            String title = reg.getCourse().getTitle();
            g2.drawString(title, 15, 42);

            // 3. Bars Logic
            int cardX = 0;
            int cardY = 0;

            if (reg.getCourse().isHasLab()) {
                // Dual progress bars
                double theoryPct = 0;
                double labPct = 0;
                if (reg.getAttendanceRecord() != null) {
                    theoryPct = reg.getAttendanceRecord().getTheoryPercent();
                    labPct = reg.getAttendanceRecord().getLabPercent();
                }

                int yStats = cardY + 55; // Adjusted Y position
                drawProgressBar(g2, "Theory Attendance", theoryPct, cardX + 15, yStats, w - 30, 6,
                        new Color(0, 229, 255)); // Cyan
                drawProgressBar(g2, "Lab Attendance", labPct, cardX + 15, yStats + 25, w - 30, 6,
                        new Color(100, 100, 255)); // Purple-ish

            } else {
                // Single progress bar
                double pct = 0;
                if (reg.getAttendanceRecord() != null) {
                    pct = reg.getAttendanceRecord().getTheoryPercent();
                }
                drawProgressBar(g2, "Attendance", pct, cardX + 15, cardY + 60, w - 30, 8, new Color(0, 229, 255));
            }
        }

        private void drawProgressBar(Graphics2D g2, String label, double percent, int x, int y, int w, int h,
                Color barColor) {
            // Track Background
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(x, y, w, h, 10, 10);

            // Fill Bar
            int fillW = (int) ((percent / 100.0) * w);
            if (fillW > w)
                fillW = w;

            g2.setColor(barColor);
            g2.fillRoundRect(x, y, fillW, h, 10, 10);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(new Color(200, 200, 200));
            g2.drawString(label, x, y - 3); // Above left

            String pText = String.format("%.0f%%", percent);
            int pW = g2.getFontMetrics().stringWidth(pText);
            g2.setColor(Color.WHITE);
            g2.drawString(pText, x + w - pW, y - 3); // Above right
        }
    }
}
