package app.UI;

import model.Course;
import model.Student;
import repository.CourseRepository;
import repository.StudentRepository;
import repository.DatabaseCourseRepository;
import repository.DatabaseStudentRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourseRegistrationUI extends JFrame {

    private final Student student;
    private final CourseRepository courseRepo;
    private final StudentRepository studentRepo;
    private JTable table;
    private DefaultTableModel model;

    public CourseRegistrationUI(Student student) {
        this.student = student;
        this.courseRepo = new DatabaseCourseRepository();
        this.studentRepo = new DatabaseStudentRepository();

        setTitle("Course Registration - " + student.getFullName());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(60, 100, 180));
        JLabel title = new JLabel("Available Courses");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = { "Select", "Course Code", "Course Title", "Credits" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox is editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setMaxWidth(60); // Checkbox column narrow
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton btnRegister = new JButton("Register Selected Courses");
        btnRegister.setBackground(new Color(50, 150, 50));
        btnRegister.setForeground(Color.WHITE);

        btnPanel.add(btnRegister);
        add(btnPanel, BorderLayout.SOUTH);

        // Load Data
        loadCourses();

        // Register Logic
        btnRegister.addActionListener(e -> registerSelectedCourses());
    }

    private void loadCourses() {
        model.setRowCount(0);
        List<Course> courses = courseRepo.getAllCourses();
        for (Course c : courses) {
            model.addRow(new Object[] { false, c.getCourseCode(), c.getTitle(), c.getCreditHours() });
        }
    }

    private void registerSelectedCourses() {
        int rowCount = table.getRowCount();
        int successCount = 0;
        int failCount = 0;
        int duplicateCount = 0;

        for (int i = 0; i < rowCount; i++) {
            Boolean selected = (Boolean) model.getValueAt(i, 0);
            if (selected != null && selected) {
                String courseCode = (String) model.getValueAt(i, 1);

                // Duplicate Check (Logic 3)
                if (studentRepo.isRegistered(student.getUserId(), courseCode)) {
                    duplicateCount++;
                    continue;
                }

                // Insert Record (Logic 2)
                if (studentRepo.registerCourse(student.getUserId(), courseCode)) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
        }

        String message = "Registration Complete!\n\n" +
                "Registered: " + successCount + "\n" +
                "Duplicates (Skipped): " + duplicateCount + "\n" +
                "Failed: " + failCount;

        JOptionPane.showMessageDialog(this, message);
        if (successCount > 0) {
            // refresh or close?
            // Maybe refresh to show updated status if we added visual indicators
            // For now, just close as it's a transactional window
            dispose();
        }
    }
}
