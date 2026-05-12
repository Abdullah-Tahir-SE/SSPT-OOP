package app.UI;

import app.utils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class AdminLoginUI extends JFrame {

    public AdminLoginUI() {
        setTitle("Administrative Access");
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

        JLabel welcomeLabel = new JLabel("Welcome Administrator");
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
        JLabel greetLabel = new JLabel("secure login for system management.");
        greetLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        greetLabel.setForeground(StyleUtils.STEEL_GRAY);
        leftPanel.add(greetLabel, gbcLeft);

        contentPane.add(leftPanel);

        // 3. Right Panel: Login Form Container
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false); // Transparent to show main gradient

        // Glass Card for inputs
        StyleUtils.GlassPanel card = new StyleUtils.GlassPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(360, 450));
        card.setBorder(new javax.swing.border.EmptyBorder(30, 40, 30, 40));

        // -- Right Side Content --
        JLabel title = new JLabel("ADMINISTRATOR");
        title.setFont(StyleUtils.FONT_TITLE);
        title.setForeground(StyleUtils.NAVY_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        card.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("System Login");
        subtitle.setFont(StyleUtils.FONT_SUBTITLE);
        subtitle.setForeground(StyleUtils.NAVY_PRIMARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitle);

        card.add(Box.createVerticalStrut(30));

        // --- FIXED USERNAME FIELD ---
        JLabel uLabel = new JLabel("Username");
        uLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        uLabel.setForeground(new Color(0, 18, 51)); // Dark Navy

        JTextField uField = new JTextField(15);
        uField.setOpaque(true); // FORCE SOLID BACKGROUND
        uField.setBackground(Color.WHITE); // White Background
        uField.setForeground(new Color(0, 18, 51)); // Dark Blue Text
        uField.setCaretColor(new Color(0, 18, 51)); // Dark Cursor
        // Manual Border with Padding
        uField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        uField.setMaximumSize(new Dimension(300, 40));

        // --- FIXED PASSWORD FIELD ---
        JLabel pLabel = new JLabel("Password");
        pLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pLabel.setForeground(new Color(0, 18, 51));

        JPasswordField pField = new JPasswordField(15);
        pField.setOpaque(true); // FORCE SOLID BACKGROUND
        pField.setBackground(Color.WHITE);
        pField.setForeground(new Color(0, 18, 51));
        pField.setCaretColor(new Color(0, 18, 51));
        // Manual Border with Padding
        pField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        pField.setMaximumSize(new Dimension(300, 40));

        addCenteredField(card, uLabel, uField);
        card.add(Box.createVerticalStrut(15));
        addCenteredField(card, pLabel, pField);

        card.add(Box.createVerticalStrut(30));

        JButton loginBtn = StyleUtils.createRoundButton("LOGIN");
        loginBtn.setMaximumSize(new Dimension(200, 45));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(loginBtn);

        card.add(Box.createVerticalGlue());

        JButton backBtn = StyleUtils.createTextButton("← Return to Main");
        backBtn.setForeground(StyleUtils.NAVY_PRIMARY);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(backBtn);

        rightPanel.add(card);
        contentPane.add(rightPanel);

        // Actions
        loginBtn.addActionListener(e -> {
            String u = uField.getText();
            String p = new String(pField.getPassword());
            if ("abii".equals(u) && "abii@0827".equals(p)) {
                dispose();
                SwingUtilities.invokeLater(() -> new AdminDashboardUI().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new MainLandingPage().setVisible(true);
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
