package app.UI;

import model.Student;
import javax.swing.*;

public class ManualCourseRegistrationUI extends JFrame {

    public ManualCourseRegistrationUI(Student student) {
        setTitle("Manual Course Registration");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Ensure background matches if resized beyond panel bounds (unlikely but good
        // practice)
        getContentPane().setBackground(app.utils.StyleUtils.NAVY_PRIMARY);

        // Add the panel
        CourseRegistrationPanel panel = new CourseRegistrationPanel(student);
        add(panel);
    }
}
