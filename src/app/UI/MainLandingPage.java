package app.UI;

import javax.swing.*;
import app.utils.StyleUtils;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class MainLandingPage extends JFrame {

    // Deep Navy Theme Palette
    private final Color BG_DARK = StyleUtils.NAVY_PRIMARY;
    private final Color BG_GRADIENT = StyleUtils.SLATE_BLUE;
    private final Color GLASS_PANEL = new Color(255, 255, 255, 10); // Very subtle frost
    private final Color GLASS_BORDER = new Color(255, 255, 255, 30);

    // Button Colors
    // Button Colors (Navy & Slate)
    private final Color BTN_ADMIN_1 = StyleUtils.SLATE_BLUE;
    private final Color BTN_ADMIN_2 = StyleUtils.SLATE_BLUE.darker();
    private final Color BTN_STUDENT_1 = StyleUtils.NAVY_PRIMARY;
    private final Color BTN_STUDENT_2 = StyleUtils.NAVY_PRIMARY.darker();

    @SuppressWarnings("this-escape")
    public MainLandingPage() {
        setTitle("Smart Semester Tracker - Welcome");
        setSize(950, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Background Content Pane
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Rich Diagonal Gradient
                GradientPaint gp = new GradientPaint(
                        0, 0, BG_DARK,
                        getWidth(), getHeight(), BG_GRADIENT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle Grid Pattern (Professional Tech feel)
                g2.setColor(new Color(255, 255, 255, 3));
                for (int i = 0; i < getWidth(); i += 50)
                    g2.drawLine(i, 0, i, getHeight());
                for (int i = 0; i < getHeight(); i += 50)
                    g2.drawLine(0, i, getWidth(), i);
            }
        };
        contentPane.setLayout(new GridBagLayout());
        setContentPane(contentPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // 2. The Main "Glass" Panel
        JPanel mainPanel = createGlassPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(500, 500)); // Fixed professional size

        // -- Title Section --
        mainPanel.add(Box.createVerticalStrut(40));

        JLabel lblTitle = new JLabel("SMART SEMESTER");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 38));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitle);

        mainPanel.add(Box.createVerticalStrut(5));

        JLabel lblSubtitle = new JLabel("STUDENT PROGRESS TRACKER");
        lblSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSubtitle.setForeground(StyleUtils.STEEL_GRAY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblSubtitle);

        mainPanel.add(Box.createVerticalStrut(60));

        // -- Buttons with Icons --
        // Use generic icons from ThemeIcons if specific ones aren't perfect
        JButton btnAdmin = createIconButton("ADMIN PANEL", ThemeIcons.getReportsIcon(20, Color.WHITE), BTN_ADMIN_1,
                BTN_ADMIN_2);
        JButton btnStudent = createIconButton("STUDENT PANEL", ThemeIcons.getProfileIcon(20, Color.WHITE),
                BTN_STUDENT_1, BTN_STUDENT_2);

        btnAdmin.addActionListener(e -> {
            dispose();
            new AdminLoginUI().setVisible(true);
        });
        btnStudent.addActionListener(e -> {
            dispose();
            new StudentLoginUI().setVisible(true);
        });

        mainPanel.add(btnAdmin);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(btnStudent);
        mainPanel.add(Box.createVerticalStrut(50));

        // -- Exit Link --
        JButton btnExit = new JButton("Exit Application");
        styleLinkButton(btnExit);
        btnExit.addActionListener(e -> System.exit(0));
        mainPanel.add(btnExit);

        mainPanel.add(Box.createVerticalGlue()); // Push footer to bottom

        // -- Footer (Inside Panel) --
        /* "note dovelped by ... in bottom center of panal in bold itelic" */
        JLabel lblFooter = new JLabel("Developed by: Maryam, Abdullah & Danish");
        lblFooter.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 12));
        lblFooter.setForeground(new Color(150, 150, 180)); // Muted text
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(lblFooter);
        mainPanel.add(Box.createVerticalStrut(30)); // Padding bottom

        // Add panel to frame
        contentPane.add(mainPanel, gbc);
    }

    private JPanel createGlassPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glass Fill (Border + Semi-transparent Fill)
                g2.setColor(GLASS_PANEL); // Very dark/subtle fill
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                // Border Ring
                g2.setColor(GLASS_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);

                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private JButton createIconButton(String text, Icon icon, Color c1, Color c2) {
        JButton btn = new JButton(text);
        btn.setIcon(icon);
        btn.setIconTextGap(15);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);

        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(350, 60)); // Wider nice buttons
        btn.setPreferredSize(new Dimension(350, 60));

        // Custom Paint
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient Background
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, c.getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);

                // Top Shine
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight() / 2, 15, 15);

                // Hover Light
                if (btn.getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);
                }

                g2.dispose();
                super.paint(g, c);
            }
        });

        return btn;
    }

    private void styleLinkButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(255, 100, 100)); // Red-ish accent
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setText("<html><u>" + "Exit Application" + "</u></html>");
            }

            public void mouseExited(MouseEvent e) {
                btn.setText("Exit Application");
            }
        });
    }
}
