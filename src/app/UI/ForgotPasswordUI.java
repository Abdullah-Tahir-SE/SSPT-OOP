package app.UI;

import app.utils.StyleUtils;
import repository.DatabaseStudentRepository;
import service.AuthService;

import javax.swing.*;
import java.awt.*;

public class ForgotPasswordUI extends JFrame {

    public ForgotPasswordUI() {
        setTitle("Password Recovery");
        setSize(900, 600);
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

        JLabel welcomeLabel = new JLabel("Password Recovery");
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
        JLabel greetLabel = new JLabel("Securely reset your account access.");
        greetLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        greetLabel.setForeground(StyleUtils.STEEL_GRAY);
        leftPanel.add(greetLabel, gbcLeft);

        contentPane.add(leftPanel);

        // 3. Right Panel: Recovery Form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        StyleUtils.GlassPanel card = new StyleUtils.GlassPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(360, 500));
        card.setBorder(new javax.swing.border.EmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("RESET PASSWORD");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(StyleUtils.NAVY_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        JLabel subtitle = new JLabel("Enter details to recover access");
        subtitle.setFont(StyleUtils.FONT_SUBTITLE);
        subtitle.setForeground(StyleUtils.NAVY_PRIMARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitle);

        card.add(Box.createVerticalStrut(15));

        JTextField regField = new JTextField(15);
        regField.setOpaque(true);
        regField.setBackground(Color.WHITE);
        regField.setForeground(new Color(0, 18, 51));
        regField.setCaretColor(new Color(0, 18, 51));
        regField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JTextField keyField = new JTextField(15);
        keyField.setOpaque(true);
        keyField.setBackground(Color.WHITE);
        keyField.setForeground(new Color(0, 18, 51));
        keyField.setCaretColor(new Color(0, 18, 51));
        keyField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JPasswordField passField = new JPasswordField(15);
        passField.setOpaque(true);
        passField.setBackground(Color.WHITE);
        passField.setForeground(new Color(0, 18, 51));
        passField.setCaretColor(new Color(0, 18, 51));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JPasswordField confField = new JPasswordField(15);
        confField.setOpaque(true);
        confField.setBackground(Color.WHITE);
        confField.setForeground(new Color(0, 18, 51));
        confField.setCaretColor(new Color(0, 18, 51));
        confField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        addCenteredField(card, "Registration No", regField);
        card.add(Box.createVerticalStrut(5));

        addCenteredField(card, "Security Key", keyField);
        card.add(Box.createVerticalStrut(5));

        addCenteredField(card, "New Password", passField);
        card.add(Box.createVerticalStrut(5));

        addCenteredField(card, "Confirm Password", confField);

        card.add(Box.createVerticalStrut(15));

        JButton btnReset = StyleUtils.createRoundButton("RESET PASSWORD");
        btnReset.setMaximumSize(new Dimension(250, 40));
        btnReset.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(btnReset);

        card.add(Box.createVerticalStrut(10));

        JButton btnCancel = StyleUtils.createTextButton("Back to Login");
        btnCancel.setForeground(StyleUtils.NAVY_PRIMARY);
        btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(btnCancel);

        rightPanel.add(card);
        contentPane.add(rightPanel);

        // Listeners
        btnReset.addActionListener(e -> {
            String reg = regField.getText().trim();
            String key = keyField.getText().trim();
            String p1 = new String(passField.getPassword());
            String p2 = new String(confField.getPassword());

            if (reg.isEmpty() || key.isEmpty() || p1.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Missing Info",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!p1.equals(p2)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AuthService auth = new AuthService(new DatabaseStudentRepository());
            boolean success = auth.resetPassword(reg, key, p1);

            if (success) {
                JOptionPane.showMessageDialog(this, "Password Reset Successful!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new StudentLoginUI().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Reset Failed.\nInvalid Registration No or Security Key.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            dispose();
            new StudentLoginUI().setVisible(true);
        });
    }

    private void addCenteredField(JPanel p, String labelText, JComponent f) {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setOpaque(false);

        JLabel l = new JLabel(labelText);
        l.setFont(StyleUtils.FONT_BOLD);
        l.setForeground(StyleUtils.NAVY_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(300, 35));

        wrap.add(l);
        wrap.add(Box.createVerticalStrut(2));
        wrap.add(f);
        wrap.setMaximumSize(new Dimension(300, 55));
        wrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(wrap);
    }
}
