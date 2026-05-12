package app.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SGPAMiniBar extends JPanel {
    private final double sgpa;
    // New Colors
    private final Color NEON_CYAN = new Color(0, 229, 255); // #00E5FF
    private final Color DARK_BG = new Color(30, 42, 69); // #1e2a45

    public SGPAMiniBar(double sgpa) {
        this.sgpa = sgpa;
        setOpaque(false);
        // Fixed preferred size as requested
        setPreferredSize(new Dimension(150, 35));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background Track (Dark Shade)
        g2.setColor(DARK_BG);
        g2.fillRoundRect(0, 0, w, h, 15, 15);

        // Fill Logic
        double fillPct = sgpa / 4.0;
        if (fillPct > 1.0)
            fillPct = 1.0;
        if (fillPct < 0.0)
            fillPct = 0.0;
        int fillW = (int) (w * fillPct);

        // Filled Color (Neon Cyan)
        g2.setColor(NEON_CYAN);

        // Clip and Fill
        if (fillW > 0) {
            Shape savedClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, 15, 15));
            g2.fillRect(0, 0, fillW, h);
            g2.setClip(savedClip);
        }

        // Text Overlay (Value)
        String valueText = String.format("%.2f", sgpa);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(valueText);
        int textX = (w - textW) / 2;
        int textY = (h + fm.getAscent()) / 2 - 2;

        // Use Dark Navy for text text brightness dependent on fill?
        // Or just Black/White? Neon Cyan is bright. White text on Neon Cyan might
        // correspond to poor contrast or good?
        // Let's use Dark Navy for text color to contrast with Cyan, but what if fill is
        // small?
        // Check contrast. Cyan #00E5FF is light. Dark BG #1e2a45 is dark.
        // Text should probably be Black or Dark Navy for high contrast against Cyan,
        // but white against Dark BG.
        // Simple heuristic: Text color depends on if it's over the filled part or not?
        // For simplicity user asked "Update the label to show the actual value".
        // Let's stick to White text relative to the track, but maybe use a shadow?
        // Or better: Just use White. The Cyan is bright enough that White might be hard
        // to read?
        // Actually #00E5FF is very bright. Dark text is better.
        // But the user didn't specify text color. Let's use Dark Navy for text if bar
        // is mostly filled?
        // Let's just use White with a shadow for safety, or just Dark Navy if user
        // didn't specify text color.
        // Let's try Dark Navy text (0, 18, 51) as it matches the theme.
        g2.setColor(new Color(0, 18, 51));
        if (fillPct < 0.5)
            g2.setColor(Color.WHITE); // Switch to white if bar is low and text is on dark bg

        g2.drawString(valueText, textX, textY);
    }
}
