package app.UI;

import app.utils.StyleUtils;
import model.Student;
import repository.DatabaseStudentRepository;
import repository.StudentRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class AdminDashboardUI extends JFrame {

    private final StudentRepository repo;
    private JTable table;
    private DefaultTableModel model;

    // Premium Color Palette (Matching Student Dashboard)
    private final Color SIDEBAR_COLOR = new Color(0, 18, 51, 220); // Deep Navy with transparency
    private final Color ACCENT_GRADIENT_1 = StyleUtils.SLATE_BLUE;
    private final Color ACCENT_GRADIENT_2 = StyleUtils.NAVY_PRIMARY;
    private final Color GLASS_BG = new Color(255, 255, 255, 25); // Subtle glass tint
    private final Color TABLE_HEAD_BG = StyleUtils.NAVY_PRIMARY;

    public AdminDashboardUI() {
        this.repo = new DatabaseStudentRepository();

        setTitle("Admin Dashboard - Smart Semester");
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 1. MAIN BACKGROUND: Rich Gradient with Orbs
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Deep Navy Slate Gradient
                GradientPaint gp = new GradientPaint(0, 0, StyleUtils.NAVY_PRIMARY, getWidth(), getHeight(),
                        StyleUtils.SLATE_BLUE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle glowing orbs
                g2.setColor(new Color(92, 103, 125, 40)); // Steel Gray glow
                g2.fillOval(-100, -100, 600, 600);
                g2.setColor(new Color(51, 65, 92, 30)); // Slate Blue glow
                g2.fillOval(getWidth() - 400, getHeight() - 400, 800, 800);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // ---------------- HEADER -----------------
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(getWidth(), 80));
        header.setBorder(new EmptyBorder(15, 30, 0, 30));

        JLabel title = new JLabel("ADMIN CONTROL PANEL", SwingConstants.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        // Use a badge or icon for Admin
        JLabel badge = new JLabel("Administrator Mode");
        badge.setForeground(new Color(200, 200, 255));
        badge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        badge.setBorder(BorderFactory.createCompoundBorder(
                new StyleUtils.RoundedBorder(20),
                new EmptyBorder(5, 15, 5, 15)));

        header.add(title, BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // ---------------- CONTAINER -----------------
        JPanel contentContainer = new JPanel(new GridBagLayout());
        contentContainer.setOpaque(false);
        mainPanel.add(contentContainer, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- LEFT SIDEBAR (Navigation) ---
        gbc.gridx = 0;
        gbc.weightx = 0.20; // 20% width
        JPanel sidebar = createGlassPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel menuLabel = new JLabel("MANAGEMENT");
        menuLabel.setForeground(new Color(150, 150, 180));
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(20));

        // Navigation Buttons
        addNavButton(sidebar, "Dashboard Overview", ThemeIcons.getMarksIcon(20, Color.WHITE), e -> refreshTable());
        addNavButton(sidebar, "View Student Details", ThemeIcons.getProfileIcon(20, Color.WHITE), e -> viewDetails());
        addNavButton(sidebar, "Delete Student", ThemeIcons.getDeleteIcon(20, Color.WHITE), e -> deleteStudent());
        addNavButton(sidebar, "Refresh List", ThemeIcons.getRefreshIcon(20, Color.WHITE), e -> refreshTable());

        sidebar.add(Box.createVerticalGlue());

        // Logout
        JButton btnLogout = createStyledButton("Logout", new Color(255, 80, 80), new Color(200, 50, 50));
        btnLogout.setIcon(ThemeIcons.getLogoutIcon(20, Color.WHITE));
        btnLogout.setIconTextGap(15);
        btnLogout.addActionListener(e -> {
            dispose();
            new MainLandingPage().setVisible(true);
        });
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        sidebar.add(btnLogout);

        contentContainer.add(sidebar, gbc);

        // --- RIGHT CONTENT (Table Area) ---
        gbc.gridx = 1;
        gbc.weightx = 0.80; // 80% width
        JPanel contentPanel = createGlassPanel();
        contentPanel.setLayout(new BorderLayout(0, 15));
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel tableTitle = new JLabel("Registered Students");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tableTitle.setForeground(Color.WHITE);
        contentPanel.add(tableTitle, BorderLayout.NORTH);

        // Initialize Table
        setupTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        // Style the ScrollPane corner
        scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, null);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        contentContainer.add(contentPanel, gbc);

        // Load Data
        refreshTable();
    }

    private void setupTable() {
        String[] cols = { "User ID", "Name", "Reg No", "Program", "Degree", "Semester" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40); // Taller rows for modern look
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(220, 230, 255));
        table.setSelectionForeground(StyleUtils.NAVY_PRIMARY);
        table.setGridColor(new Color(230, 230, 230));

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEAD_BG);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Cell Centering
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // --- Actions ---

    private void viewDetails() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to view details.");
            return;
        }
        String regNo = (String) model.getValueAt(row, 2);
        Student s = repo.findByRegistrationNo(regNo);
        if (s != null) {
            new StudentDetailsDialog(this, s).setVisible(true);
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
            return;
        }
        String regNo = (String) model.getValueAt(row, 2);
        int conf = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + regNo + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            repo.delete(regNo);
            refreshTable();
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Student> list = repo.findAll();
        for (Student s : list) {
            model.addRow(new Object[] {
                    s.getUserId(),
                    s.getFullName(),
                    s.getRegistrationNo(),
                    s.getProgramLevel(),
                    s.getDegree(),
                    s.getSemester()
            });
        }
    }

    // --- UI Helpers (Glassmorphism & Gradients) ---

    private JPanel createGlassPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_COLOR); // Use Navy Tint
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private void addNavButton(JPanel parent, String text, Icon icon, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Use the vibrant gradient
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_GRADIENT_1, getWidth(), 0, ACCENT_GRADIENT_2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setIcon(icon);
        btn.setIconTextGap(15);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);

        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 20, 12, 20)); // Padding

        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.addActionListener(action);

        parent.add(btn);
        parent.add(Box.createVerticalStrut(15));
    }

    private JButton createStyledButton(String text, Color c1, Color c2) {
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
        return btn;
    }
}
