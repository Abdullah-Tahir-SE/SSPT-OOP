package app.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class StyleUtils {

    // Deep Navy, Slate, Steel Palette 2025
    public static final Color NAVY_PRIMARY = new Color(0, 18, 51); // #001233 - Deepest Navy
    public static final Color SLATE_BLUE = new Color(51, 65, 92); // #33415c - Dark Slate
    public static final Color STEEL_GRAY = new Color(92, 103, 125); // #5c677d - Steel Gray
    public static final Color LIGHT_TEXT = new Color(225, 228, 235); // #e1e4eb - Off-White (Generated for contrast)

    // Mapped Constants for Compatibility
    public static final Color PRIMARY_COLOR = NAVY_PRIMARY;
    public static final Color ACCENT_COLOR = STEEL_GRAY;
    public static final Color HOVER_COLOR = SLATE_BLUE;
    public static final Color BACKGROUND_GRADIENT_1 = NAVY_PRIMARY;
    public static final Color BACKGROUND_GRADIENT_2 = SLATE_BLUE;

    public static final Color TEXT_COLOR = LIGHT_TEXT;
    public static final Color TEXT_SECONDARY = new Color(160, 170, 190);
    public static final Color INPUT_BG = new Color(245, 248, 255);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);

    public static void modernizeTextField(JTextField field) {
        field.setFont(FONT_PLAIN);
        field.setForeground(NAVY_PRIMARY); // Default to Navy for consistency on Glass
        field.setBackground(INPUT_BG);
        // Rounded Border with 20px radius
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20),
                BorderFactory.createEmptyBorder(10, 15, 10, 15) // Inner padding
        ));
    }

    // Custom Rounded Border
    public static class RoundedBorder implements javax.swing.border.Border {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    public static JButton createRoundButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(HOVER_COLOR);
                } else {
                    g2.setColor(PRIMARY_COLOR);
                }

                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();

                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 45));
        return btn;
    }

    public static JButton createTextButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_PLAIN);
        btn.setForeground(PRIMARY_COLOR);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(HOVER_COLOR);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Slight bold on hover
            }

            public void mouseExited(MouseEvent e) {
                btn.setForeground(PRIMARY_COLOR);
                btn.setFont(FONT_PLAIN);
            }
        });
        return btn;
    }

    // Glassmorphism Panel
    @SuppressWarnings("serial")
    public static class GlassPanel extends JPanel {
        public GlassPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Semi-transparent white background
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 40, 40));

            // Subtle border
            g2.setColor(new Color(255, 255, 255, 100));
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 40, 40));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Gradient Background
    @SuppressWarnings("serial")
    public static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, NAVY_PRIMARY, w, h, SLATE_BLUE);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }
}
