package app.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SGPABarPanel extends JPanel {
    private final double sgpa;
    private final Color DEEP_PURPLE = new Color(90, 0, 140);
    private final Color WARNING_RED = new Color(220, 50, 50);
    private final Color TRACK_COLOR = new Color(230, 230, 240);

    public SGPABarPanel(double sgpa) {
        this.sgpa = sgpa;
        setOpaque(false);
        // Height approx 30-40px as requested, width handled by layout
        setPreferredSize(new Dimension(200, 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Track (Empty part)
        g2.setColor(TRACK_COLOR);
        g2.fillRoundRect(0, 0, w, h, 20, 20);

        // Fill Logic
        double fillPct = sgpa / 4.0;
        if (fillPct > 1.0)
            fillPct = 1.0;
        if (fillPct < 0.0)
            fillPct = 0.0;

        int fillW = (int) (w * fillPct);

        // Color Logic
        if (sgpa <= 2.0) {
            g2.setColor(WARNING_RED);
        } else {
            g2.setColor(DEEP_PURPLE);
        }

        // Draw Fill
        if (fillW > 0) {
            // Use clipping or Shape intersection to prevent square corners if fill is
            // small?
            // Simple RoundRect is usually fine, but if fillW < corner radius it might look
            // odd.
            // Using intersection is cleaner for a progress bar inside a container.
            Shape savedClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));
            g2.fillRect(0, 0, fillW, h);
            g2.setClip(savedClip);
        }

        // Text Overlay
        String text = String.format("%.2f", sgpa);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);
        int textH = fm.getAscent();

        int textX = (w - textW) / 2;
        int textY = (h + textH) / 2 - 2;

        g2.setColor(Color.WHITE);
        g2.drawString(text, textX, textY);
    }
}
