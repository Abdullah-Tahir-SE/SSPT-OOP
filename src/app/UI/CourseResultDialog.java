package app.UI;

import app.utils.StyleUtils;
import model.CourseResult;
import model.Mark;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class CourseResultDialog extends JDialog {

    // Theme Colors
    private final Color SIDEBAR_COLOR = new Color(0, 18, 51, 220);
    private final Color ACCENT_GRADIENT_1 = StyleUtils.SLATE_BLUE;
    private final Color ACCENT_GRADIENT_2 = StyleUtils.NAVY_PRIMARY;
    private final Color TABLE_HEAD_BG = StyleUtils.NAVY_PRIMARY;
    private final Color CARD_BG = new Color(255, 255, 255, 240);
    private final Color NEON_CYAN = new Color(0, 229, 255);

    private final JPanel contentCardPanel;
    private final CardLayout cardLayout;
    private JButton btnTheory, btnLab;

    public CourseResultDialog(Window owner, CourseResult result) {
        super(owner, "Detailed Result: " + result.getCourseCode(), ModalityType.APPLICATION_MODAL);
        setSize(850, 650);
        setLocationRelativeTo(owner);

        // Main Logic
        // Background Gradient
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
        header.setBorder(new EmptyBorder(20, 30, 10, 30));

        JLabel title = new JLabel(result.getCourseCode() + ": " + result.getCourseTitle());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);

        // Grade Badge
        JLabel badge = new JLabel(result.getGrade() != null ? "Grade: " + result.getGrade() : "Grade: N/A");
        badge.setForeground(NEON_CYAN);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 18));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_CYAN, 1, true),
                new EmptyBorder(5, 15, 5, 15)));
        header.add(badge, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // --- Center Content (Custom Tabs) ---
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. Tab Bar
        JPanel tabBar = new JPanel();
        tabBar.setLayout(new BoxLayout(tabBar, BoxLayout.X_AXIS));
        tabBar.setOpaque(false);

        btnTheory = createTabButton("Theory Result", true);
        tabBar.add(btnTheory);
        tabBar.add(Box.createHorizontalStrut(10));

        if (result.isHasLab()) {
            btnLab = createTabButton("Lab Result", false);
            tabBar.add(btnLab);
        }

        tabBar.add(Box.createHorizontalGlue()); // Left align tabs
        centerContainer.add(tabBar, BorderLayout.NORTH);

        // 2. Cards (Tables)
        cardLayout = new CardLayout();
        contentCardPanel = new JPanel(cardLayout);
        contentCardPanel.setOpaque(false);
        contentCardPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); // Spacing between tabs and table

        contentCardPanel.add(
                createTablePanel(result.getTheoryAssessments(), result.getTheoryMid(), result.getTheoryFinal()),
                "Theory");
        if (result.isHasLab()) {
            contentCardPanel.add(createTablePanel(result.getLabAssessments(), result.getLabMid(), result.getLabFinal()),
                    "Lab");
        }

        centerContainer.add(contentCardPanel, BorderLayout.CENTER);
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        // --- Footer (Summary) ---
        JPanel footer = createGlassPanel();
        footer.setLayout(new BorderLayout());
        footer.setBorder(new EmptyBorder(15, 30, 15, 30));
        footer.setPreferredSize(new Dimension(getWidth(), 100)); // Fixed height

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);

        // Data
        String gpaStr = String.format("%.2f", result.getGpa());
        String totalMarksStr = String.format("%.1f", result.getTotalObtained());
        String pctStr = result.getTotalMax() > 0
                ? String.format("%.1f%%", (result.getTotalObtained() / result.getTotalMax()) * 100)
                : "0%";

        statsPanel.add(createStatItem("Final GPA", gpaStr));
        statsPanel.add(createStatItem("Total Marks", totalMarksStr));
        statsPanel.add(createStatItem("Percentage", pctStr));

        // Close Button
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.setOpaque(false);
        JButton closeBtn = createStyledButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonContainer.add(closeBtn);

        footer.add(statsPanel, BorderLayout.CENTER);
        footer.add(buttonContainer, BorderLayout.EAST);

        mainPanel.add(footer, BorderLayout.SOUTH);

        // Wiring Tabs
        btnTheory.addActionListener(e -> switchTab("Theory", btnTheory));
        if (btnLab != null) {
            btnLab.addActionListener(e -> switchTab("Lab", btnLab));
        }
    }

    private void switchTab(String cardName, JButton activeBtn) {
        cardLayout.show(contentCardPanel, cardName);
        updateTabStyles(activeBtn);
    }

    private void updateTabStyles(JButton active) {
        // Reset all
        styleTabButton(btnTheory, false);
        if (btnLab != null)
            styleTabButton(btnLab, false);
        // Set active
        styleTabButton(active, true);
    }

    private void styleTabButton(JButton btn, boolean active) {
        if (active) {
            btn.setForeground(NEON_CYAN);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, NEON_CYAN),
                    new EmptyBorder(8, 20, 8, 20)));
        } else {
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(0, 0, 2, 0), // Placeholder for alignment
                    new EmptyBorder(8, 20, 8, 20)));
        }
    }

    private JButton createTabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        styleTabButton(btn, active);
        return btn;
    }

    private JPanel createTablePanel(List<Mark> assessments, Mark mid, Mark fin) {
        JPanel p = createGlassPanel(); // Use glass panel for table container
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = { "Assessment", "Obtained", "Total", "Percentage" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add Data
        for (Mark m : assessments) {
            addMarkRow(model, m.getLabel() != null ? m.getLabel() : m.getType().toString(), m);
        }
        if (mid != null)
            addMarkRow(model, "Mid Term", mid);
        if (fin != null)
            addMarkRow(model, "Final Exam", fin);

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Colors
        table.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        table.setSelectionBackground(new Color(220, 230, 255));
        table.setSelectionForeground(StyleUtils.NAVY_PRIMARY);
        table.setGridColor(new Color(230, 230, 230));

        // Header Styling
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 14));
        th.setBackground(TABLE_HEAD_BG);
        th.setForeground(Color.WHITE);
        th.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Center Cells
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);

        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void addMarkRow(DefaultTableModel model, String label, Mark m) {
        double pct = m.getTotalMarks() > 0 ? (m.getObtained() / m.getTotalMarks()) * 100 : 0;
        model.addRow(new Object[] {
                label,
                String.format("%.1f", m.getObtained()),
                String.format("%.1f", m.getTotalMarks()),
                String.format("%.1f%%", pct)
        });
    }

    private JPanel createStatItem(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);

        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setForeground(new Color(200, 200, 220));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setForeground(NEON_CYAN); // Neon for values
        v.setFont(new Font("Segoe UI", Font.BOLD, 22));

        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JPanel createGlassPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_COLOR);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_GRADIENT_1, getWidth(), 0, ACCENT_GRADIENT_2);
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
        btn.setPreferredSize(new Dimension(100, 40));
        return btn;
    }
}
