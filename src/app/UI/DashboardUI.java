package app.UI;

import app.utils.StyleUtils;
import model.Student;
import repository.DatabaseStudentRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DashboardUI extends JFrame {

    private model.Student student;

    // Premium Color Palette
    // Color Palette (Deep Navy Slate)
    private final Color SIDEBAR_COLOR = new Color(0, 18, 51, 220); // Deep Navy with slight transparency
    private final Color ACCENT_GRADIENT_1 = StyleUtils.SLATE_BLUE;
    private final Color ACCENT_GRADIENT_2 = StyleUtils.NAVY_PRIMARY;
    private final Color CARD_BG = new Color(255, 255, 255, 240); // Keep readable white cards for now or switch to light
                                                                 // slate
    private final Color TEXT_PRIMARY = StyleUtils.NAVY_PRIMARY;
    private final Color NEON_CYAN = new Color(0, 229, 255);

    public DashboardUI(model.Student s) {
        this.student = s;

        setTitle("Smart Semester - Student Dashboard");
        setSize(1280, 800); // Wider for modern feel
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. MAIN BACKGROUND: Rich Gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                // Deep Navy Slate Gradient
                GradientPaint gp = new GradientPaint(0, 0, StyleUtils.NAVY_PRIMARY, getWidth(), getHeight(),
                        StyleUtils.SLATE_BLUE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Add subtle glowing orbs for "atmosphere"
                // Add subtle glowing orbs for "atmosphere" (Slate/Steel tones)
                g2.setColor(new Color(92, 103, 125, 40)); // Steel Gray glow
                g2.fillOval(-100, -100, 600, 600);
                g2.setColor(new Color(51, 65, 92, 30)); // Slate Blue glow
                g2.fillOval(getWidth() - 400, getHeight() - 400, 800, 800);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // ---------------- HEADER -----------------
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(getWidth(), 90));
        header.setBorder(new EmptyBorder(20, 40, 10, 40));

        // Logo
        ImageIcon originalIcon = new ImageIcon("src/resources/images/logo.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setBorder(new EmptyBorder(0, 0, 0, 15)); // Spacing between logo and text

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(logoLabel);

        JLabel title = new JLabel("SMART SEMESTER PROGRESS TRACKER", SwingConstants.LEFT);
        title.setForeground(Color.WHITE);
        titlePanel.add(title);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        // Stylish User Badge
        JPanel userBadge = createUserBadge();

        header.add(titlePanel, BorderLayout.WEST);
        header.add(userBadge, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ---------------- CONTAINER FOR COLUMNS -----------------
        JPanel contentContainer = new JPanel(new GridBagLayout());
        contentContainer.setOpaque(false);
        add(contentContainer, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 30, 20);
        gbc.weighty = 1.0;

        // --- LEFT SIDEBAR (Navigation) ---
        gbc.gridx = 0;
        gbc.weightx = 0.22;
        JPanel leftPanel = createGlassPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setBorder(new EmptyBorder(30, 20, 30, 20));

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.fill = GridBagConstraints.HORIZONTAL;
        gbcLeft.weightx = 1.0;
        gbcLeft.insets = new Insets(0, 0, 20, 0); // Gap after label

        JLabel menuLabel = new JLabel("MENU");
        menuLabel.setForeground(new Color(150, 150, 180));
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(menuLabel, gbcLeft);

        gbcLeft.gridy++;
        gbcLeft.insets = new Insets(0, 0, 15, 0); // Gap between buttons

        addNavButton(leftPanel, "Profile", ThemeIcons.getProfileIcon(18, Color.WHITE), e -> openProfile(), gbcLeft);
        addNavButton(leftPanel, "Attendance", ThemeIcons.getAttendanceIcon(18, Color.WHITE), e -> openAttendance(),
                gbcLeft);
        addNavButton(leftPanel, "Marks Entry", ThemeIcons.getMarksIcon(18, Color.WHITE),
                e -> new MarksEntryUI(student).setVisible(true), gbcLeft);
        addNavButton(leftPanel, "Calculate SGPA", ThemeIcons.getCalculatorIcon(18, Color.WHITE),
                e -> openSGPACalculator(), gbcLeft);
        addNavButton(leftPanel, "My Reports", ThemeIcons.getReportsIcon(18, Color.WHITE),
                e -> new ReportGenerationUI(student).setVisible(true), gbcLeft);

        // Spacer to push Logout to bottom
        GridBagConstraints gbcSpacer = new GridBagConstraints();
        gbcSpacer.gridx = 0;
        gbcSpacer.gridy = gbcLeft.gridy; // Next row
        gbcSpacer.weighty = 1.0;
        gbcSpacer.fill = GridBagConstraints.VERTICAL;
        leftPanel.add(Box.createGlue(), gbcSpacer);
        gbcLeft.gridy++; // Increment for next item

        // Logout Button
        JButton logoutBtn = createStyledButton("Logout", new Color(255, 80, 80), new Color(200, 50, 50));
        logoutBtn.addActionListener(e -> logout());
        logoutBtn.setPreferredSize(new Dimension(0, 45));
        gbcLeft.insets = new Insets(0, 0, 0, 0); // No bottom margin for last
        gbcLeft.weighty = 0; // Reset weight
        leftPanel.add(logoutBtn, gbcLeft);

        contentContainer.add(leftPanel, gbc);

        // --- CENTER (Stats & Overview) ---
        gbc.gridx = 1;
        gbc.weightx = 0.56;
        JPanel centerPanel = createGlassPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Welcome Text
        JLabel welcomeTitle = new JLabel("Dashboard Overview");
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        centerPanel.add(welcomeTitle, BorderLayout.NORTH);

        // Computing Stats
        String coursesVal = String.valueOf(student.getRegistrations().size());
        String attendanceVal = "Active";
        double sgpaVal = student.getCurrentSgpa();

        // Stats Grid
        JPanel statsGrid = new JPanel(new GridLayout(3, 1, 0, 20));
        statsGrid.setOpaque(false);

        statsGrid.add(createDetailCard("Courses Registered", coursesVal, NEON_CYAN));
        statsGrid.add(createDetailCard("Attendance Status", attendanceVal, NEON_CYAN));
        statsGrid.add(createSGPACardWithBar(sgpaVal));

        centerPanel.add(statsGrid, BorderLayout.CENTER);
        contentContainer.add(centerPanel, gbc);

        // --- RIGHT SIDEBAR (Actions) ---
        gbc.gridx = 2;
        gbc.weightx = 0.22;
        JPanel rightPanel = createGlassPanel();
        rightPanel.setLayout(new GridBagLayout()); // Using GridBagLayout for cloned behavior
        rightPanel.setBorder(new EmptyBorder(30, 20, 30, 20));

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.fill = GridBagConstraints.HORIZONTAL;
        gbcRight.weightx = 1.0;
        gbcRight.insets = new Insets(0, 0, 20, 0); // Label gap

        JLabel actionsLabel = new JLabel("QUICK ACTIONS");
        actionsLabel.setForeground(new Color(150, 150, 180));
        actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        actionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(actionsLabel, gbcRight);

        gbcRight.gridy++;
        gbcRight.insets = new Insets(0, 0, 15, 0); // Button gap

        addNavButton(rightPanel, "Add Course", ThemeIcons.getAddIcon(18, Color.WHITE),
                e -> new ManualCourseRegistrationUI(student).setVisible(true), gbcRight);

        addNavButton(rightPanel, "Update Course", ThemeIcons.getUpdateIcon(18, Color.WHITE),
                e -> new UpdateCourseDialog(this, student).setVisible(true), gbcRight);

        addNavButton(rightPanel, "Delete Course", ThemeIcons.getDeleteIcon(18, Color.WHITE),
                e -> new DeleteCourseDialog(this, student).setVisible(true), gbcRight);

        addNavButton(rightPanel, "Refresh Data", ThemeIcons.getRefreshIcon(18, Color.WHITE),
                e -> refreshDashboard(), gbcRight);

        addNavButton(rightPanel, "Reset Semester", ThemeIcons.getResetIcon(18, Color.WHITE),
                e -> resetSemester(), gbcRight);

        // Spacer
        GridBagConstraints gbcSpacerR = new GridBagConstraints();
        gbcSpacerR.gridx = 0;
        gbcSpacerR.gridy = gbcRight.gridy;
        gbcSpacerR.weighty = 1.0;
        gbcSpacerR.fill = GridBagConstraints.VERTICAL;
        rightPanel.add(Box.createGlue(), gbcSpacerR);

        contentContainer.add(rightPanel, gbc);
    }

    // --- Modern Component Creators ---

    private JPanel createUserBadge() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        JLabel name = new JLabel(student != null ? student.getFullName() : "Student Name");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel role = new JLabel(" | Student");
        role.setForeground(new Color(200, 200, 255));
        role.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        p.add(name);
        p.add(role);
        return p;
    }

    private JPanel createGlassPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_COLOR);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private void addNavButton(JPanel parent, String text, Icon icon, java.awt.event.ActionListener action,
            GridBagConstraints gbc) {
        // UNIFIED DESIGN: Using the same gradient style as Action Buttons
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Use the vibrant gradient (ACCENT_GRADIENT_1 to ACCENT_GRADIENT_2)
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_GRADIENT_1, getWidth(), 0, ACCENT_GRADIENT_2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();

                super.paintComponent(g); // Paints text and icon
            }
        };

        btn.setIcon(icon);
        btn.setIconTextGap(15);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold font like actions
        btn.setForeground(Color.WHITE);

        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20)); // Interior padding

        // Allow layout manager to determine width, but respect height
        btn.setPreferredSize(new Dimension(0, 45));

        btn.addActionListener(action);
        parent.add(btn, gbc);
        gbc.gridy++; // Increment gridy for the next component
    }

    private void addActionButton(JPanel parent, String text, java.awt.event.ActionListener action) {
        JButton btn = createStyledButton(text, ACCENT_GRADIENT_1, ACCENT_GRADIENT_2);
        btn.addActionListener(action);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        parent.add(btn);
        parent.add(Box.createVerticalStrut(10));
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

                super.paintComponent(g); // Paint text
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Ensure alignment in BoxLayout
        return btn;
    }

    private JPanel createDetailCard(String title, String value, Color accentInfo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);

        // Custom background for card
        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Side accent strip
                g2.setColor(accentInfo);
                g2.fillRoundRect(0, 0, 8, getHeight(), 20, 20);
                g2.fillRect(5, 0, 10, getHeight()); // flatten inner edge
            }
        };
        bg.setOpaque(false);
        bg.setLayout(new BorderLayout());
        bg.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        t.setForeground(new Color(100, 100, 120));

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 32));
        v.setForeground(TEXT_PRIMARY);

        bg.add(t, BorderLayout.NORTH);
        bg.add(v, BorderLayout.CENTER);

        return bg;
    }

    private JPanel createSGPACardWithBar(double sgpa) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false); // Wrapper

        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(NEON_CYAN); // Unified Neon Cyan Accent
                g2.fillRoundRect(0, 0, 8, getHeight(), 20, 20);
                g2.fillRect(5, 0, 10, getHeight());
            }
        };
        bg.setOpaque(false);
        bg.setLayout(new GridBagLayout()); // Centered Alignment
        bg.setBorder(new EmptyBorder(10, 30, 10, 20));

        JLabel t = new JLabel("Current SGPA");
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        t.setForeground(new Color(100, 100, 120));

        SGPAMiniBar bar = new SGPAMiniBar(sgpa);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        bg.add(t, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        bg.add(bar, gbc);

        return bg;
    }

    // --- Actions ---

    private void openProfile() {
        DatabaseStudentRepository repo = new DatabaseStudentRepository();
        Student freshStudent = repo.findByRegistrationNo(student.getRegistrationNo());
        new StudentDetailsDialog(this, freshStudent != null ? freshStudent : student).setVisible(true);
    }

    private void openAttendance() {
        if (student.getRegistrations().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses registered.");
            return;
        }
        new AttendanceEntryDialog(this, student).setVisible(true);
    }

    private void openSGPACalculator() {
        new SGPACalculatorDialog(this, student).setVisible(true);
        // Auto-refresh to show new SGPA
        refreshDashboard();
    }

    private void refreshDashboard() {
        DatabaseStudentRepository repo = new DatabaseStudentRepository();
        Student fresh = repo.findByRegistrationNo(student.getRegistrationNo());
        if (fresh != null) {
            this.student = fresh;
            JOptionPane.showMessageDialog(this, "Dashboard Refreshed.");
            dispose();
            new DashboardUI(fresh).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Refresh Failed.");
        }
    }

    private void resetSemester() {
        ResetDialog dialog = new ResetDialog(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            DatabaseStudentRepository repo = new DatabaseStudentRepository();
            if (repo.deleteStudentAccount(student.getUserId())) {
                JOptionPane.showMessageDialog(this, "Account Deleted.", "Reset Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                logout();
            } else {
                JOptionPane.showMessageDialog(this, "Reset Failed. Error:\n" + repo.getLastError(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        dispose();
        new MainLandingPage().setVisible(true);
    }
}
