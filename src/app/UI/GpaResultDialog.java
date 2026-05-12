package app.UI;

import app.utils.StyleUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GpaResultDialog extends JDialog {

    private final Color SIDEBAR_COLOR = new Color(0, 18, 51, 220); // Deep Navy
    private final Color NEON_CYAN = new Color(0, 229, 255);
    private final Color ACCENT_GRADIENT_1 = StyleUtils.SLATE_BLUE;
    private final Color ACCENT_GRADIENT_2 = StyleUtils.NAVY_PRIMARY;

    public GpaResultDialog(Window owner, String breakdown, String grade, double gpa) {
        super(owner, "GPA Calculation Result", ModalityType.APPLICATION_MODAL);
        setSize(500, 550);
        setLocationRelativeTo(owner);

        // Main Gradient Panel
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

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel title = new JLabel("Course Result", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(title, BorderLayout.CENTER);
        mainPanel.add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Glass Panel for Breakdown
        JPanel glassBreakdown = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_COLOR);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };
        glassBreakdown.setOpaque(false);
        glassBreakdown.setLayout(new BorderLayout());
        glassBreakdown.setBorder(new EmptyBorder(20, 20, 20, 20));
        glassBreakdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JTextArea txtBreakdown = new JTextArea(breakdown);
        txtBreakdown.setEditable(false);
        txtBreakdown.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtBreakdown.setLineWrap(true);
        txtBreakdown.setWrapStyleWord(true);
        txtBreakdown.setOpaque(false);
        txtBreakdown.setForeground(Color.WHITE);
        txtBreakdown.setBorder(null);

        glassBreakdown.add(txtBreakdown, BorderLayout.CENTER);
        content.add(glassBreakdown);
        content.add(Box.createVerticalStrut(30));

        // Grade/GPA Highlight
        JLabel lblGrade = new JLabel("Grade: " + grade, SwingConstants.CENTER);
        lblGrade.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblGrade.setForeground(NEON_CYAN);
        lblGrade.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblGpa = new JLabel(String.format("GPA: %.2f", gpa), SwingConstants.CENTER);
        lblGpa.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblGpa.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Use brighter colors for visibility on dark background
        Color gpaColor = (gpa >= 3.0) ? new Color(50, 255, 120) : // Bright Green
                (gpa >= 2.0) ? new Color(255, 230, 50) : // Bright Yellow
                        new Color(255, 100, 100); // Bright Red
        lblGpa.setForeground(gpaColor);

        content.add(lblGrade);
        content.add(Box.createVerticalStrut(10));
        content.add(lblGpa);

        mainPanel.add(content, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
        footer.setOpaque(false);

        JButton btnClose = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient for Button
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_GRADIENT_1, getWidth(), 0, ACCENT_GRADIENT_2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // More rounded
                g2.dispose();

                super.paintComponent(g); // Paints text
            }
        };
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setPreferredSize(new Dimension(140, 45));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnClose.addActionListener(e -> dispose());

        footer.add(btnClose);
        mainPanel.add(footer, BorderLayout.SOUTH);
    }
}
