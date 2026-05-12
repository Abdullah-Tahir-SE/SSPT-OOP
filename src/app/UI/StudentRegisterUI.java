package app.UI;

import app.utils.StyleUtils;
import model.Student;
import repository.DatabaseStudentRepository;
import service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StudentRegisterUI extends JFrame {

    private JComboBox<String> levelCombo;
    private JComboBox<String> degreeCombo;
    private JSpinner semesterSpinner;

    private final Map<String, String[]> degreeMap;

    public StudentRegisterUI() {
        // Data Setup
        degreeMap = new HashMap<>();
        degreeMap.put("Bachelor", new String[] {
                "BS Computer Science (BSCS)", "BS Software Engineering (BSSE)",
                "BS Information Technology (BSIT)", "BS Artificial Intelligence (BSAI)",
                "BS Data Science (BSDS)", "BS Cyber Security",
                "BS Computer Engineering", "BS Internet of Things (IoT)",
                "BS Bioinformatics", "BS Robotics"
        });
        degreeMap.put("MPhil", new String[] {
                "MPhil Computer Science", "MPhil Software Engineering",
                "MPhil Artificial Intelligence", "MPhil Data Science",
                "MPhil Information Security"
        });
        degreeMap.put("PhD", new String[] {
                "PhD Computer Science", "PhD Software Engineering",
                "PhD Artificial Intelligence", "PhD Data Science",
                "PhD Deep Learning"
        });

        setTitle("Student Registration");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 1. Unified Background Content Pane
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, StyleUtils.NAVY_PRIMARY,
                        getWidth(), getHeight(), StyleUtils.SLATE_BLUE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 3));
                for (int i = 0; i < getWidth(); i += 50)
                    g2.drawLine(i, 0, i, getHeight());
                for (int i = 0; i < getHeight(); i += 50)
                    g2.drawLine(0, i, getWidth(), i);
            }
        };
        contentPane.setLayout(new GridLayout(1, 2));
        setContentPane(contentPane);

        // 2. Left Panel: Branding
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.anchor = GridBagConstraints.CENTER;

        ImageIcon originalIcon = new ImageIcon("src/resources/images/logo.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        leftPanel.add(logoLabel, gbcLeft);

        gbcLeft.gridy++;
        gbcLeft.insets = new Insets(30, 0, 10, 0);

        JLabel welcomeLabel = new JLabel("Welcome Future Student");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        leftPanel.add(welcomeLabel, gbcLeft);

        gbcLeft.gridy++;
        gbcLeft.insets = new Insets(5, 0, 5, 0);
        JLabel projectLabel = new JLabel("Smart Semester Tracker");
        projectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        projectLabel.setForeground(new Color(200, 220, 255));
        leftPanel.add(projectLabel, gbcLeft);

        gbcLeft.gridy++;
        gbcLeft.insets = new Insets(10, 0, 0, 0);
        JLabel greetLabel = new JLabel("Join us to track your academic journey.");
        greetLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        greetLabel.setForeground(StyleUtils.STEEL_GRAY);
        leftPanel.add(greetLabel, gbcLeft);

        contentPane.add(leftPanel);

        // 3. Right Panel: Form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        StyleUtils.GlassPanel card = new StyleUtils.GlassPanel();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(480, 520)); // More compact
        card.setBorder(new javax.swing.border.EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8); // Tighter insets
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(StyleUtils.NAVY_PRIMARY);

        JLabel subtitle = new JLabel("Student Registration", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(StyleUtils.STEEL_GRAY);

        // Fields - with compact sizing
        JTextField nameField = createCompactTextField();
        JTextField regField = createCompactTextField();
        JPasswordField passField = createCompactPasswordField();
        JPasswordField confField = createCompactPasswordField();
        JTextField keyField = createCompactTextField();

        String[] levels = { "Select Level", "Bachelor", "MPhil", "PhD" };
        levelCombo = new JComboBox<>(levels);
        styleComboBox(levelCombo);

        degreeCombo = new JComboBox<>();
        styleComboBox(degreeCombo);
        degreeCombo.setEnabled(false);

        semesterSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        styleSpinner(semesterSpinner);

        JButton btnRegister = StyleUtils.createRoundButton("REGISTER");
        btnRegister.setPreferredSize(new Dimension(200, 40));

        JButton btnCancel = StyleUtils.createTextButton("Already have an account? Login");
        btnCancel.setForeground(StyleUtils.NAVY_PRIMARY);

        // Add to Card - Compact layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 8, 0, 8);
        card.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 8, 15, 8);
        card.add(subtitle, gbc);

        gbc.gridwidth = 1;
        gbc.weightx = 0.5;

        // Row 2: Full Name | Registration No
        addCompactField(card, "Full Name", nameField, gbc, 0, 2);
        addCompactField(card, "Registration No", regField, gbc, 1, 2);

        // Row 3: Password | Confirm Password
        addCompactField(card, "Password", passField, gbc, 0, 3);
        addCompactField(card, "Confirm Password", confField, gbc, 1, 3);

        // Row 4: Program Level | Degree Program
        addCompactField(card, "Program Level", levelCombo, gbc, 0, 4);
        addCompactField(card, "Degree Program", degreeCombo, gbc, 1, 4);

        // Row 5: Security Key | Semester
        addCompactField(card, "Security Key", keyField, gbc, 0, 5);
        addCompactField(card, "Semester", semesterSpinner, gbc, 1, 5);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 60, 8, 60);
        card.add(btnRegister, gbc);

        gbc.gridy = 13;
        gbc.insets = new Insets(0, 8, 5, 8);
        card.add(btnCancel, gbc);

        rightPanel.add(card);
        contentPane.add(rightPanel);

        // Logic - UNCHANGED
        levelCombo.addActionListener(e -> {
            String selected = (String) levelCombo.getSelectedItem();
            degreeCombo.removeAllItems();
            if (selected != null && !selected.equals("Select Level")) {
                degreeCombo.setEnabled(true);
                String[] degrees = degreeMap.get(selected);
                if (degrees != null)
                    for (String d : degrees)
                        degreeCombo.addItem(d);
            } else {
                degreeCombo.setEnabled(false);
            }
        });

        btnRegister.addActionListener(e -> {
            String name = nameField.getText().trim();
            String reg = regField.getText().trim();
            String pass = new String(passField.getPassword());
            String conf = new String(confField.getPassword());
            String key = keyField.getText().trim();
            String level = (String) levelCombo.getSelectedItem();
            String degree = (String) degreeCombo.getSelectedItem();
            int sem = (Integer) semesterSpinner.getValue();

            if (name.isEmpty() || reg.isEmpty() || pass.isEmpty() || key.isEmpty() || level.equals("Select Level")) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Info",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!pass.equals(conf)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!reg.matches("^[A-Z]{2}\\d{2}-[A-Z]{3}-\\d{3}$")) {
                JOptionPane.showMessageDialog(this, "Invalid Format! Use FA24-BSE-001", "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            AuthService auth = new AuthService(new DatabaseStudentRepository());
            Student s = auth.register(name, reg, pass, key, level, degree, sem);

            if (s != null) {
                JOptionPane.showMessageDialog(this, "Registration Successful!\nPlease Login.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new StudentLoginUI().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed or ID exists.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            dispose();
            new StudentLoginUI().setVisible(true);
        });
    }

    private JTextField createCompactTextField() {
        JTextField f = new JTextField(12);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setOpaque(true); // Force solid
        f.setBackground(Color.WHITE);
        f.setForeground(new Color(0, 18, 51)); // Dark Navy
        f.setCaretColor(new Color(0, 18, 51)); // Dark Cursor
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setPreferredSize(new Dimension(180, 35));
        return f;
    }

    private JPasswordField createCompactPasswordField() {
        JPasswordField f = new JPasswordField(12);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setOpaque(true); // Force solid
        f.setBackground(Color.WHITE);
        f.setForeground(new Color(0, 18, 51));
        f.setCaretColor(new Color(0, 18, 51));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setPreferredSize(new Dimension(180, 35));
        return f;
    }

    // 1. UPDATED FIXED COMBOBOX STYLE
    private void styleComboBox(JComboBox<?> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Explicitly set colors for the box itself
        box.setForeground(new Color(0, 18, 51));
        box.setBackground(Color.WHITE);

        // FORCE RENDERER: This makes the list items visible
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // Always Dark Text
                setForeground(new Color(0, 18, 51));

                // Background logic
                if (isSelected) {
                    setBackground(new Color(200, 220, 255)); // Light Blue highlight
                } else {
                    setBackground(Color.WHITE);
                }
                return this;
            }
        });

        // Border styling
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        box.setPreferredSize(new Dimension(180, 35));
    }

    // 2. UPDATED FIXED SPINNER STYLE
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // FORCE EDITOR STYLE: This fixes the number visibility
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setOpaque(true);
            tf.setBackground(Color.WHITE);
            tf.setForeground(new Color(0, 18, 51)); // Dark Navy Text
            tf.setCaretColor(new Color(0, 18, 51));
            tf.setHorizontalAlignment(JTextField.CENTER);
        }

        // Border styling
        spinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        spinner.setPreferredSize(new Dimension(180, 35));
    }

    private void addCompactField(JPanel p, String labelText, JComponent field, GridBagConstraints gbc, int x, int row) {
        gbc.gridx = x;
        gbc.gridy = row * 2;
        gbc.insets = new Insets(4, 8, 0, 8);
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(StyleUtils.NAVY_PRIMARY);
        p.add(l, gbc);

        gbc.gridy = (row * 2) + 1;
        gbc.insets = new Insets(2, 8, 6, 8);
        p.add(field, gbc);
    }
}
