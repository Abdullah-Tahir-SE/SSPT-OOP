package app.UI;

import app.utils.StyleUtils;
import model.Student;
import repository.DatabaseStudentRepository;
import service.AuthService;

import javax.swing.*;
import java.awt.*;

public class StudentLoginUI extends JFrame {

    public StudentLoginUI() {
        setTitle("Student Portal Access");
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

                // Full Window Gradient (Deep Navy -> Slight Lighter Navy/Slate)
                GradientPaint gp = new GradientPaint(
                        0, 0, StyleUtils.NAVY_PRIMARY,
                        getWidth(), getHeight(), StyleUtils.SLATE_BLUE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle Grid Pattern
                g2.setColor(new Color(255, 255, 255, 3));
                for (int i = 0; i < getWidth(); i += 50)
                    g2.drawLine(i, 0, i, getHeight());
                for (int i = 0; i < getHeight(); i += 50)
                    g2.drawLine(0, i, getWidth(), i);
            }
        };
        contentPane.setLayout(new GridLayout(1, 2)); // Seamless Split
        setContentPane(contentPane);

        // 2. Left Panel: Branding & Welcome
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false); // Transparent to show main gradient

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.anchor = GridBagConstraints.CENTER;

        // Logo
        ImageIcon originalIcon = new ImageIcon("src/resources/images/logo.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        leftPanel.add(logoLabel, gbcLeft);

        // Text Stack
        gbcLeft.gridy++;
        gbcLeft.insets = new Insets(30, 0, 10, 0);

        JLabel welcomeLabel = new JLabel("Welcome Student");
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
        JLabel greetLabel = new JLabel("Log in to view your progress & grades.");
        greetLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        greetLabel.setForeground(StyleUtils.STEEL_GRAY);
        leftPanel.add(greetLabel, gbcLeft);

        contentPane.add(leftPanel);

        // 3. Right Panel: Login Form Container
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false); // Transparent to show main gradient

        StyleUtils.GlassPanel card = new StyleUtils.GlassPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(360, 520)); // Taller for extra buttons
        card.setBorder(new javax.swing.border.EmptyBorder(30, 40, 30, 40));

        // -- Right Side Content --
        JLabel title = new JLabel("STUDENT LOGIN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(StyleUtils.NAVY_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        JLabel subtitle = new JLabel("Access your dashboard");
        subtitle.setFont(StyleUtils.FONT_SUBTITLE);
        subtitle.setForeground(StyleUtils.NAVY_PRIMARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitle);

        card.add(Box.createVerticalStrut(30));

        JLabel uLabel = new JLabel("Registration No");
        uLabel.setFont(StyleUtils.FONT_BOLD);
        uLabel.setForeground(StyleUtils.NAVY_PRIMARY);

        JTextField uField = new JTextField(15);
        // Manual Styling for Visibility
        uField.setOpaque(true);
        uField.setBackground(Color.WHITE);
        uField.setForeground(new Color(0, 18, 51));
        uField.setCaretColor(new Color(0, 18, 51));
        uField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        uField.setMaximumSize(new Dimension(300, 40));

        JLabel pLabel = new JLabel("Password");
        pLabel.setFont(StyleUtils.FONT_BOLD);
        pLabel.setForeground(StyleUtils.NAVY_PRIMARY);

        JPasswordField pField = new JPasswordField(15);
        // Manual Styling for Visibility
        pField.setOpaque(true);
        pField.setBackground(Color.WHITE);
        pField.setForeground(new Color(0, 18, 51));
        pField.setCaretColor(new Color(0, 18, 51));
        pField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        pField.setMaximumSize(new Dimension(300, 40));

        addCenteredField(card, uLabel, uField);
        card.add(Box.createVerticalStrut(10));
        addCenteredField(card, pLabel, pField);

        card.add(Box.createVerticalStrut(30));

        JButton loginBtn = StyleUtils.createRoundButton("LOGIN");
        loginBtn.setMaximumSize(new Dimension(200, 45));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(loginBtn);

        card.add(Box.createVerticalStrut(15));

        // Secondary Actions Row
        JPanel actionRow = new JPanel();
        actionRow.setOpaque(false);
        actionRow.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton regBtn = StyleUtils.createTextButton("Sign Up");
        JButton forgotBtn = StyleUtils.createTextButton("Forgot Password?");
        // Style these to look good on dark bg
        regBtn.setForeground(StyleUtils.NAVY_PRIMARY);
        forgotBtn.setForeground(StyleUtils.NAVY_PRIMARY);

        actionRow.add(regBtn);
        actionRow.add(new JLabel(" | ")).setForeground(StyleUtils.NAVY_PRIMARY);
        actionRow.add(forgotBtn);
        actionRow.setMaximumSize(new Dimension(300, 30));
        card.add(actionRow);

        card.add(Box.createVerticalGlue());

        JButton backBtn = StyleUtils.createTextButton("← Return to Main");
        backBtn.setForeground(StyleUtils.NAVY_PRIMARY);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(backBtn);

        rightPanel.add(card);
        contentPane.add(rightPanel);

        // Actions
        backBtn.addActionListener(e -> {
            dispose();
            new MainLandingPage().setVisible(true);
        });

        loginBtn.addActionListener(e -> {
            String regNo = uField.getText().trim();
            String pwd = new String(pField.getPassword());
            AuthService auth = new AuthService(new DatabaseStudentRepository());
            Student s = auth.login(regNo, pwd);
            if (s != null) {
                dispose();
                SwingUtilities.invokeLater(() -> new DashboardUI(s).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Registration No or Password", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        regBtn.addActionListener(e -> {
            dispose();
            new StudentRegisterUI().setVisible(true);
        });

        forgotBtn.addActionListener(e -> {
            dispose();
            new ForgotPasswordUI().setVisible(true);
        });
    }

    private void addCenteredField(JPanel p, JLabel l, JComponent f) {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setOpaque(false);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(l);
        wrap.add(Box.createVerticalStrut(5));
        wrap.add(f);
        wrap.setMaximumSize(new Dimension(300, 65));
        wrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(wrap);
    }
}
