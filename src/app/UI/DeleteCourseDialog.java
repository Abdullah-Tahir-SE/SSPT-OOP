package app.UI;

import app.utils.StyleUtils;
import model.CourseRegistration;
import model.Student;
import repository.DatabaseStudentRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DeleteCourseDialog extends JDialog {

    private Student student;
    private JComboBox<String> courseCombo;
    private JLabel lblDetails;
    private JButton btnDelete;

    // Theme
    private final Color NEON_CYAN = new Color(0, 229, 255);
    private final Color TEXT_COLOR = Color.WHITE;

    public DeleteCourseDialog(JFrame parent, Student student) {
        super(parent, "Delete Course", true);
        this.student = student;

        setSize(500, 380); // Title increased height slightly for wrapped text
        setLocationRelativeTo(parent);

        // Main Background Panel
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

        // Header Panel
        JLabel title = new JLabel("Delete Course", SwingConstants.CENTER);
        title.setForeground(NEON_CYAN);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(25, 0, 15, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Content
        JPanel content = createGlassPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(10, 20, 30, 20)); // Padding around glass panel
        wrapper.add(content);
        mainPanel.add(wrapper, BorderLayout.CENTER);

        // Content Components
        content.add(createLabel("Select Course to Permanently Delete:"));
        content.add(Box.createVerticalStrut(10));

        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        courseCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        for (CourseRegistration reg : student.getRegistrations()) {
            courseCombo.addItem(reg.getCourse().getTitle());
        }
        courseCombo.addActionListener(e -> updateDetails());
        content.add(courseCombo);
        content.add(Box.createVerticalStrut(20));

        // Details Warning (Wrapped in Panel for Alignment)
        lblDetails = new JLabel(" ");
        lblDetails.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblDetails.setForeground(new Color(255, 100, 100)); // Light Red for Warning

        // Wrap label in a panel to ensure it doesn't get weirdly stretched
        JPanel lblPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lblPanel.setOpaque(false);
        lblPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPanel.add(lblDetails);
        content.add(lblPanel);

        content.add(Box.createVerticalGlue());

        // Delete Button Wrapper for Center Alignment
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Panel aligns left, but content flows center
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        btnDelete = new JButton("DELETE COURSE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Red Gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 50, 50), getWidth(), 0, new Color(180, 0, 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setFocusPainted(false);
        btnDelete.setBorderPainted(false);
        btnDelete.setContentAreaFilled(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setPreferredSize(new Dimension(200, 45));
        btnDelete.addActionListener(e -> deleteCourse());

        btnPanel.add(btnDelete);
        content.add(btnPanel);

        if (courseCombo.getItemCount() > 0) {
            courseCombo.setSelectedIndex(0);
            updateDetails();
        } else {
            btnDelete.setEnabled(false);
            lblDetails.setText("No courses available to delete.");
        }
    }

    private void updateDetails() {
        String selected = (String) courseCombo.getSelectedItem();
        if (selected != null) {
            // HTML Wrap for full visibility
            lblDetails.setText(
                    "<html><body style='width: 300px; text-align: left;'>Warning: This will unregister you and delete YOUR data for '"
                            + selected + "'.</body></html>");
        }
    }

    private void deleteCourse() {
        String selectedTitle = (String) courseCombo.getSelectedItem();
        if (selectedTitle == null)
            return;

        CourseRegistration reg = student.getRegistrations().stream()
                .filter(r -> r.getCourse().getTitle().equals(selectedTitle))
                .findFirst().orElse(null);

        if (reg == null)
            return;

        String courseCode = reg.getCourse().getCourseCode();

        // Confirmation
        int confirm = JOptionPane.showConfirmDialog(this,
                "ARE YOU SURE?\n\nThis will DELETE your registration for '" + selectedTitle
                        + "'\nand ALL your attendance/marks data.\n\nThis cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseStudentRepository repo = new DatabaseStudentRepository();
                repo.deleteStudentRegistration(student.getUserId(), courseCode);

                JOptionPane.showMessageDialog(this, "Course Unregistered Successfully.");
                dispose();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting course: " + e.getMessage());
            }
        }
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(TEXT_COLOR);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel createGlassPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
    }
}
