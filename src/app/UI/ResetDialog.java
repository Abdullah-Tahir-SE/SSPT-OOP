package app.UI;

import app.utils.StyleUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class ResetDialog extends JDialog {

    private boolean confirmed = false;
    private final Color NEON_CYAN = new Color(0, 229, 255);
    private final Color WARNING_RED = new Color(255, 80, 80);

    public ResetDialog(JFrame parent) {
        super(parent, "Confirm Reset", true);
        setSize(500, 350);
        setLocationRelativeTo(parent);
        setUndecorated(true); // Custom look without window borders if we wanted, but let's keep native border
                              // for consistency or go full custom.
        // User asked for "popup ko thora acha kr do", usually keep native border is
        // safer but let's stick to the Dialog style we used in DeleteCourseDialog which
        // kept native border.

        // Main Panel
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

        // Content
        JPanel content = createGlassPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        wrapper.add(content);
        mainPanel.add(wrapper, BorderLayout.CENTER);

        // 1. Warning Icon/Header
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw Warning Triangle
                int w = getWidth();
                Path2D path = new Path2D.Double();
                path.moveTo(w / 2.0, 0);
                path.lineTo(w / 2.0 + 30, 50);
                path.lineTo(w / 2.0 - 30, 50);
                path.closePath();

                g2.setColor(Color.ORANGE);
                g2.fill(path);

                // Exclamation Mark
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
                g2.drawString("!", w / 2 - 5, 40);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setMaximumSize(new Dimension(100, 60));
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(iconPanel);

        // 2. Critical Warning Text
        JLabel lblHeader = new JLabel("CRITICAL WARNING");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(WARNING_RED);
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblHeader);
        content.add(Box.createVerticalStrut(20));

        // 3. Message Body
        JLabel lblMsg = new JLabel(
                "<html><div style='text-align: center; width: 300px; color: white; font-size: 14px;'>" +
                        "Are you sure you want to <b>RESET</b> your semester?<br><br>" +
                        "This will <b>PERMANENTLY DELETE</b> your account and all associated data.</div></html>");
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblMsg);
        content.add(Box.createVerticalGlue());

        // 4. Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnYes = new JButton("YES, RESET EVERYTHING") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(220, 20, 60), getWidth(), 0, new Color(139, 0, 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btnYes);
        btnYes.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnYes);
        btnPanel.add(btnCancel);
        content.add(btnPanel);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void styleButton(JButton btn) {
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 40));
    }

    private JPanel createGlassPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
    }
}
