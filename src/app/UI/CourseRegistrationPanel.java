package app.UI;

import app.utils.StyleUtils;
import model.Course;
import model.CourseRegistration;
import model.Student;
import util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CourseRegistrationPanel extends JPanel {
    private JTextField txtCourseCode;
    private JTextField txtCourseTitle;
    private JTextField txtCreditHours;
    private JCheckBox chkHasLab;
    private JLabel lblTotalCredits;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private JButton btnFinalize;

    private List<Course> tempCourses; // Courses being added
    private Student currentStudent; // Context for registration
    private int totalCredits = 0; // Credits in current session
    private int dbCredits = 0; // Credits already in database
    private static final int MAX_CREDITS = 21;

    // Theme Colors
    private final Color NEON_CYAN = new Color(0, 229, 255);
    private final Color TEXT_COLOR = Color.WHITE;

    public CourseRegistrationPanel(Student student) {
        this.currentStudent = student;
        this.tempCourses = new ArrayList<>();

        if (student != null) {
            this.dbCredits = getExistingCredits(student.getUserId());
        }

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Input Panel (Glass Style) ---
        JPanel inputPanel = createGlassPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Header
        JLabel headerLabel = new JLabel("Add New Course");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(NEON_CYAN);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        inputPanel.add(headerLabel, gbc);

        // Reset
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Fields
        gbc.gridy = 1;
        addLabel(inputPanel, "Course Code:", gbc);
        gbc.gridx = 1;
        txtCourseCode = createStyledTextField();
        inputPanel.add(txtCourseCode, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addLabel(inputPanel, "Course Title:", gbc);
        gbc.gridx = 1;
        txtCourseTitle = createStyledTextField();
        inputPanel.add(txtCourseTitle, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        addLabel(inputPanel, "Credit Hours:", gbc);
        gbc.gridx = 1;
        txtCreditHours = createStyledTextField();
        inputPanel.add(txtCreditHours, gbc);

        // Lab Checkbox
        gbc.gridx = 1;
        gbc.gridy = 4;
        chkHasLab = new JCheckBox("Includes Lab?");
        chkHasLab.setOpaque(false);
        chkHasLab.setForeground(TEXT_COLOR);
        chkHasLab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkHasLab.setFocusPainted(false);
        inputPanel.add(chkHasLab, gbc);

        // Add Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 50, 0, 50);
        btnAdd = createGradientButton("Add to List", NEON_CYAN, new Color(0, 150, 200));
        btnAdd.setForeground(StyleUtils.NAVY_PRIMARY); // Dark text on bright button
        inputPanel.add(btnAdd, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        String[] columns = { "Course Code", "Course Title", "Credits" };
        tableModel = new DefaultTableModel(columns, 0);
        courseTable = new JTable(tableModel);
        styleTable(courseTable);

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 1));
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblTotalCredits = new JLabel("Total Credits: " + dbCredits + "/" + MAX_CREDITS);
        lblTotalCredits.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalCredits.setForeground(TEXT_COLOR);
        bottomPanel.add(lblTotalCredits, BorderLayout.WEST);

        btnFinalize = createGradientButton("Finalize Registration", new Color(50, 205, 50), new Color(34, 139, 34));
        bottomPanel.add(btnFinalize, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        btnAdd.addActionListener(e -> addCourseToList());
        btnFinalize.addActionListener(e -> finalizeRegistration());
    }

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

    private JPanel createGlassPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 20)); // Subtle glass
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(new Color(255, 255, 255, 50)); // Border
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
    }

    private void addLabel(JPanel p, String text, GridBagConstraints gbc) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(TEXT_COLOR);
        p.add(l, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(200, 35));

        // Explicit Visibility Fix
        tf.setOpaque(true);
        tf.setBackground(Color.WHITE);
        tf.setForeground(new Color(0, 18, 51)); // Dark Navy
        tf.setCaretColor(new Color(0, 18, 51));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        return tf;
    }

    private JButton createGradientButton(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
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
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(StyleUtils.NAVY_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // --- Logic Preserved ---

    private int getExistingCredits(int studentId) {
        int credits = 0;
        String sql = "SELECT SUM(c.theory_credits + c.lab_credits) FROM course_registrations cr JOIN courses c ON cr.course_code = c.course_code WHERE cr.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    credits = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return credits;
    }

    private void addCourseToList() {
        String code = txtCourseCode.getText().trim();
        String title = txtCourseTitle.getText().trim();
        String creditsStr = txtCreditHours.getText().trim();

        if (code.isEmpty() || title.isEmpty() || creditsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int newCredits = Integer.parseInt(creditsStr);
            int projectedTotal = dbCredits + totalCredits + newCredits;

            if (projectedTotal > MAX_CREDITS) {
                JOptionPane.showMessageDialog(this,
                        "Limit Exceeded! You have " + (dbCredits + totalCredits) + " credits.", "Limit Exceeded",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Course newCourse = new Course(code, title, newCredits);
            newCourse.setHasLab(chkHasLab.isSelected());
            tempCourses.add(newCourse);
            tableModel.addRow(new Object[] { code, title, newCredits });
            totalCredits += newCredits;
            lblTotalCredits.setText("Total Credits: " + (dbCredits + totalCredits) + "/" + MAX_CREDITS);

            txtCourseCode.setText("");
            txtCourseTitle.setText("");
            txtCreditHours.setText("");
            chkHasLab.setSelected(false);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid credits.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizeRegistration() {
        if (tempCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses to register.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<CourseRegistration> registrations = new ArrayList<>();
        for (Course c : tempCourses) {
            CourseRegistration reg = new CourseRegistration();
            reg.setCourse(c);
            reg.setRegisteredAt(LocalDateTime.now());
            registrations.add(reg);
        }

        if (saveRegistrationToDB(registrations)) {
            JOptionPane.showMessageDialog(this, "Registration Finalized!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dbCredits += totalCredits;
            totalCredits = 0;
            tempCourses.clear();
            tableModel.setRowCount(0);
            lblTotalCredits.setText("Total Credits: " + dbCredits + "/" + MAX_CREDITS);
        } else {
            JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean saveRegistrationToDB(List<CourseRegistration> registrations) {
        Connection conn = null;
        PreparedStatement pstmtCourse = null;
        PreparedStatement pstmtReg = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String insertCourseSQL = "INSERT INTO courses (course_code, title, theory_credits, lab_credits, has_lab, is_fyp) VALUES (?, ?, ?, 0, ?, 0) "
                    +
                    "ON DUPLICATE KEY UPDATE title=VALUES(title), theory_credits=VALUES(theory_credits), has_lab=VALUES(has_lab)";
            String insertRegSQL = "INSERT INTO course_registrations (student_id, course_code, registered_at) VALUES (?, ?, ?)";

            pstmtCourse = conn.prepareStatement(insertCourseSQL);
            pstmtReg = conn.prepareStatement(insertRegSQL);

            for (CourseRegistration reg : registrations) {
                Course c = reg.getCourse();
                pstmtCourse.setString(1, c.getCourseCode());
                pstmtCourse.setString(2, c.getTitle());
                pstmtCourse.setInt(3, c.getTheoryCredits());
                pstmtCourse.setBoolean(4, c.isHasLab());
                pstmtCourse.executeUpdate();

                if (currentStudent != null) {
                    pstmtReg.setInt(1, currentStudent.getUserId());
                    pstmtReg.setString(2, c.getCourseCode());
                    pstmtReg.setTimestamp(3, Timestamp.valueOf(reg.getRegisteredAt()));
                    pstmtReg.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            return false;
        } finally {
            if (pstmtCourse != null)
                try {
                    pstmtCourse.close();
                } catch (SQLException e) {
                }
            if (pstmtReg != null)
                try {
                    pstmtReg.close();
                } catch (SQLException e) {
                }
            if (conn != null)
                DatabaseConnection.closeConnection(conn);
        }
    }
}
