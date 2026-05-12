package app.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class ThemeIcons {

    public static Icon getProfileIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                // Head
                g2.fillOval(width / 3, 2, width / 3, width / 3);
                // Body (Shoulders)
                g2.fillArc(width / 6, width / 3 + 2, width * 2 / 3, height, 0, 180);
            }
        };
    }

    public static Icon getAttendanceIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                g2.setStroke(new BasicStroke(2));
                // Calendar Outline
                g2.drawRoundRect(2, 4, width - 4, height - 6, 4, 4);
                // Top Line
                g2.drawLine(2, 9, width - 2, 9);
                // Checks
                g2.drawLine(width / 3, 4, width / 3, 0);
                g2.drawLine(width * 2 / 3, 4, width * 2 / 3, 0);
            }
        };
    }

    public static Icon getMarksIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                // Bar Chart
                int barW = width / 4;
                g2.fillRect(0, height - (height / 3), barW, height / 3);
                g2.fillRect(barW + 2, height - (height / 2), barW, height / 2);
                g2.fillRect((barW + 2) * 2, height - (height / 4), barW, height / 4);
            }
        };
    }

    public static Icon getReportsIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                // Document shape
                Path2D p = new Path2D.Double();
                p.moveTo(2, 0);
                p.lineTo(width - 6, 0);
                p.lineTo(width, 6);
                p.lineTo(width, height);
                p.lineTo(2, height);
                p.closePath();
                g2.draw(p);
                // Lines
                g2.drawLine(5, 5, width - 8, 5);
                g2.drawLine(5, 9, width - 5, 9);
                g2.drawLine(5, 13, width - 5, 13);
            }
        };
    }

    public static Icon getRefreshIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                g2.setStroke(new BasicStroke(2));
                g2.drawArc(2, 2, width - 4, height - 4, 45, 270);
                // Arrow head
                Path2D arrow = new Path2D.Double();
                arrow.moveTo(width - 4, height / 2);
                arrow.lineTo(width, height / 2 - 3);
                arrow.lineTo(width - 4, height / 2 - 6);
                g2.fill(arrow);
            }
        };
    }

    public static Icon getLogoutIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                g2.setStroke(new BasicStroke(2));
                // Circle with gap
                g2.drawArc(2, 2, width - 4, height - 4, 270 + 20, 360 - 40);
                // Line
                g2.drawLine(width / 2, 0, width / 2, height / 2);
            }
        };
    }

    public static Icon getAddIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(width / 2, 2, width / 2, height - 2);
                g2.drawLine(2, height / 2, width - 2, height / 2);
            }
        };
    }

    public static Icon getUpdateIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                // Pencil
                AffineTransform at = new AffineTransform();
                at.rotate(Math.toRadians(45), width / 2, height / 2);
                g2.transform(at);

                g2.fillRoundRect(width / 2 - 2, 0, 4, height - 4, 2, 2);
                g2.fillPolygon(new int[] { width / 2 - 2, width / 2 + 2, width / 2 },
                        new int[] { height - 4, height - 4, height }, 3);
            }
        };
    }

    public static Icon getDeleteIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                g2.fillRect(width / 4, 4, width / 2, height - 4);
                g2.fillRect(width / 6, 0, width * 2 / 3, 2);
                g2.fillRect(width / 2 - 1, 0, 2, 4); // handle
            }
        };
    }

    public static Icon getResetIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                // Warning Triangle
                int[] x = { width / 2, width, 0 };
                int[] y = { 0, height, height };
                g2.fillPolygon(x, y, 3);

                // Exclamation mark - clear 'bg' color or just draw white line?
                // Since this is a single color icon, we can simulate 'cutout' by compositing or
                // just not checking it.
                // Simpler: Draw Exclamation mark separate?
                // Actually, let's just make it a "Reset" circle arrow if strictly "Reset".
                // User said "Warning' or Reset icon".
                // Let's stick to a Warning-like triangle but maybe with a clear mark?
                // Since we rely on one color, we can't easily draw 'white' on top if color is
                // dynamic.
                // Let's draw an outline triangle with exclamation?
                g2.setStroke(new BasicStroke(2));
                g2.fillPolygon(x, y, 3);
            }
        };
    }

    public static Icon getCalculatorIcon(int size, Color color) {
        return new VectorIcon(size, color) {
            @Override
            protected void drawIcon(Graphics2D g2, int width, int height) {
                g2.setStroke(new BasicStroke(2));
                // Calc body
                g2.drawRoundRect(2, 2, width - 4, height - 4, 4, 4);
                // Screen
                g2.drawRect(5, 5, width - 10, 5);
                // Buttons
                g2.fillRect(5, 14, 3, 3);
                g2.fillRect(10, 14, 3, 3);
                g2.fillRect(15, 14, 3, 3);

                g2.fillRect(5, 19, 3, 3);
                g2.fillRect(10, 19, 3, 3);
                g2.fillRect(15, 19, 3, 3);
            }
        };
    }

    // --- Base Class ---
    private static abstract class VectorIcon implements Icon {
        private final int size;
        private final Color color;

        VectorIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            g2.setColor(color);
            drawIcon(g2, size, size);
            g2.dispose();
        }

        protected abstract void drawIcon(Graphics2D g2, int width, int height);

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
