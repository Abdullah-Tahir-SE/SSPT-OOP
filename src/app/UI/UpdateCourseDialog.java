package app.UI;

import app.utils.StyleUtils;
import model.Course;
import model.CourseRegistration;
import model.Student;
import repository.DatabaseStudentRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class UpdateCourseDialog extends JDialog {

    private Student student;
    private JComboBox<String> courseCombo;
    private JTextField txtCourseName;
    private JTextField txtCourseCode;
    private JTextField txtCredits;
    private JCheckBox chkHasLab;
    private JButton btnUpdate;

    private Course selectedCourse;
    private String originalCourseCode;

    // Theme
    private final Color NEON_CYAN = new Color(0, 229, 255);
    private final Color TEXT_COLOR = Color.WHITE;

    public UpdateCourseDialog(JFrame parent, Student student) {
        super(parent, "Update Course Details", true);
        this.student = student;

        setSize(550, 500);
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

        // Header
        JLabel title = new JLabel("Update Course Details", SwingConstants.CENTER);
        title.setForeground(NEON_CYAN);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Content
        JPanel content = createGlassPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel container = new JPanel(new GridBagLayout()); // Use GridBag for centering content in the glass panel if
                                                            // needed, or stick to Box
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(10, 10, 10, 10)); // Outer margin
        // Actually, let's stick to the previous layout logic but inside the glass panel
        // Wrapping content in a container for the glass effect margins

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(10, 20, 20, 20)); // Padding around glass panel
        wrapper.add(content);
        mainPanel.add(wrapper, BorderLayout.CENTER);

        // Course Selection
        content.add(createLabel("Select Course to Update:"));
        content.add(Box.createVerticalStrut(5));

        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        courseCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        for (CourseRegistration reg : student.getRegistrations()) {
            courseCombo.addItem(reg.getCourse().getTitle());
        }
        courseCombo.addActionListener(e -> loadCourseDetails());
        content.add(courseCombo);
        content.add(Box.createVerticalStrut(20));

        // Fields
        content.add(createLabel("Course Name:"));
        txtCourseName = createStyledTextField();
        content.add(txtCourseName);
        content.add(Box.createVerticalStrut(10));

        content.add(createLabel("Course Code:"));
        txtCourseCode = createStyledTextField();
        content.add(txtCourseCode);
        content.add(Box.createVerticalStrut(10));

        // Credits Panel
        JPanel creditsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        creditsPanel.setOpaque(false);
        creditsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        creditsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.setOpaque(false);
        p1.add(createLabel("Theory Credits:"));
        txtCredits = createStyledTextField();
        p1.add(txtCredits);

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.setOpaque(false);
        p2.add(createLabel("Has Lab?"));
        chkHasLab = new JCheckBox("Lab Included");
        chkHasLab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkHasLab.setForeground(TEXT_COLOR);
        chkHasLab.setOpaque(false);
        chkHasLab.setFocusPainted(false);
        chkHasLab.setAlignmentX(Component.LEFT_ALIGNMENT);
        p2.add(Box.createVerticalStrut(5)); // Align with text field
        p2.add(chkHasLab);

        creditsPanel.add(p1);
        creditsPanel.add(p2);
        content.add(creditsPanel);

        content.add(Box.createVerticalGlue()); // Push button to bottom

        // Update Button Wrapper for Center Alignment
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        btnUpdate = new JButton("Update Course") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, NEON_CYAN, getWidth(), 0, new Color(0, 150, 200));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnUpdate.setForeground(StyleUtils.NAVY_PRIMARY);
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorderPainted(false);
        btnUpdate.setContentAreaFilled(false);
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdate.setPreferredSize(new Dimension(200, 40));
        btnUpdate.addActionListener(e -> updateCourse());

        btnPanel.add(btnUpdate);
        content.add(btnPanel);

        // Initial Load
        if (courseCombo.getItemCount() > 0) {
            courseCombo.setSelectedIndex(0);
            loadCourseDetails();
        }
    }

    private void loadCourseDetails() {
        String selectedTitle = (String) courseCombo.getSelectedItem();
        if (selectedTitle == null)
            return;

        CourseRegistration reg = student.getRegistrations().stream()
                .filter(r -> r.getCourse().getTitle().equals(selectedTitle))
                .findFirst().orElse(null);

        if (reg != null) {
            selectedCourse = reg.getCourse();
            originalCourseCode = selectedCourse.getCourseCode();

            txtCourseName.setText(selectedCourse.getTitle());
            txtCourseCode.setText(selectedCourse.getCourseCode());
            txtCredits.setText(String.valueOf(selectedCourse.getTheoryCredits()));
            chkHasLab.setSelected(selectedCourse.isHasLab());
        }
    }

    private void updateCourse() {
        if (selectedCourse == null)
            return;

        try {
            String newTitle = txtCourseName.getText().trim();
            String newCode = txtCourseCode.getText().trim();
            int theoryCr = Integer.parseInt(txtCredits.getText().trim());
            boolean hasLab = chkHasLab.isSelected();
            int labCr = hasLab ? 1 : 0;

            if (newTitle.isEmpty() || newCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty.");
                return;
            }

            // Update local object
            selectedCourse.setTitle(newTitle);
            selectedCourse.setCourseCode(newCode);
            selectedCourse.setTheoryCredits(theoryCr);
            selectedCourse.setLabCredits(labCr);
            selectedCourse.setHasLab(hasLab);

            // Update Database
            DatabaseStudentRepository repo = new DatabaseStudentRepository();
            repo.updateCourse(selectedCourse, originalCourseCode);

            JOptionPane.showMessageDialog(this, "Course details updated successfully!");
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Credits must be valid numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating course: " + e.getMessage());
        }
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(TEXT_COLOR);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        tf.setPreferredSize(new Dimension(200, 35));

        // High visibility style
        tf.setOpaque(true);
        tf.setBackground(Color.WHITE);
        tf.setForeground(new Color(0, 18, 51));
        tf.setCaretColor(new Color(0, 18, 51));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
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
