package repository;

import model.Student;
import model.Course;
import model.CourseRegistration;
import model.AttendanceRecord;
import model.Mark;
import model.AssessmentType;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseStudentRepository implements StudentRepository {

    private String lastError = "";

    public String getLastError() {
        return lastError;
    }

    @Override
    public Student save(Student s) {
        if (s == null)
            return null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            Student existing = findByRegistrationNo(s.getRegistrationNo());

            if (existing == null) {
                // Insert new student
                String sql = "INSERT INTO students (full_name, registration_no, password_hash, salt, key_pass, program_level, degree, semester) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, s.getFullName());
                    pstmt.setString(2, s.getRegistrationNo());
                    pstmt.setString(3, s.getPasswordHash());
                    pstmt.setString(4, s.getSalt());
                    pstmt.setString(5, s.getKeyPass());
                    pstmt.setString(6, s.getProgramLevel());
                    pstmt.setString(7, s.getDegree());
                    pstmt.setInt(8, s.getSemester());

                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        try (ResultSet rs = pstmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                s.setUserId(rs.getInt(1));
                            }
                        }
                    }
                }
            } else {
                // Update existing student
                String sql = "UPDATE students SET full_name=?, password_hash=?, salt=?, key_pass=?, " +
                        "program_level=?, degree=?, semester=? WHERE user_id=?";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, s.getFullName());
                    pstmt.setString(2, s.getPasswordHash());
                    pstmt.setString(3, s.getSalt());
                    pstmt.setString(4, s.getKeyPass());
                    pstmt.setString(5, s.getProgramLevel());
                    pstmt.setString(6, s.getDegree());
                    pstmt.setInt(7, s.getSemester());
                    pstmt.setInt(8, existing.getUserId());

                    pstmt.executeUpdate();
                    s.setUserId(existing.getUserId());
                }
            }

            // Save course registrations
            saveRegistrations(conn, s);

            return s;
        } catch (SQLException e) {
            System.err.println("Error saving student: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void saveRegistrations(Connection conn, Student student) throws SQLException {
        // Delete existing registrations
        String deleteSql = "DELETE FROM course_registrations WHERE student_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, student.getUserId());
            pstmt.executeUpdate();
        }

        // Insert new registrations
        for (CourseRegistration reg : student.getRegistrations()) {
            if (reg == null || reg.getCourse() == null)
                continue;

            String insertRegSql = "INSERT INTO course_registrations (student_id, course_code, registered_at) " +
                    "VALUES (?, ?, ?)";
            int registrationId = -1;

            try (PreparedStatement pstmt = conn.prepareStatement(insertRegSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, student.getUserId());
                pstmt.setString(2, reg.getCourse().getCourseCode());
                pstmt.setTimestamp(3, reg.getRegisteredAt() != null ? Timestamp.valueOf(reg.getRegisteredAt())
                        : Timestamp.valueOf(LocalDateTime.now()));

                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        registrationId = rs.getInt(1);
                    }
                }
            }

            if (registrationId > 0) {
                // Save attendance record
                saveAttendanceRecord(conn, registrationId, reg.getAttendanceRecord());

                // Save marks
                saveMarks(conn, registrationId, reg.getMarks());
            }
        }
    }

    public void updateAttendance(int registrationId, AttendanceRecord att) {
        try (Connection conn = util.DatabaseConnection.getConnection()) {
            saveAttendanceRecord(conn, registrationId, att);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveAttendanceRecord(Connection conn, int registrationId, AttendanceRecord att) throws SQLException {
        if (att == null)
            return;

        String deleteSql = "DELETE FROM attendance_records WHERE registration_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, registrationId);
            pstmt.executeUpdate();
        }

        String insertSql = "INSERT INTO attendance_records (registration_id, theory_conducted, theory_attended, " +
                "lab_conducted, lab_attended) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, registrationId);
            pstmt.setInt(2, att.getTheoryConducted());
            pstmt.setInt(3, att.getTheoryAttended());
            pstmt.setInt(4, att.getLabConducted());
            pstmt.setInt(5, att.getLabAttended());
            pstmt.executeUpdate();
        }
    }

    private void saveMarks(Connection conn, int registrationId, List<Mark> marks) throws SQLException {
        if (marks == null || marks.isEmpty())
            return;

        String deleteSql = "DELETE FROM marks WHERE registration_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, registrationId);
            pstmt.executeUpdate();
        }

        String insertSql = "INSERT INTO marks (registration_id, assessment_type, obtained, total_marks, assessment_label, graded_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Mark mark : marks) {
                if (mark == null)
                    continue;
                pstmt.setInt(1, registrationId);
                pstmt.setString(2, mark.getType().name());
                pstmt.setDouble(3, mark.getObtained());
                pstmt.setDouble(4, mark.getTotalMarks());
                pstmt.setString(5, mark.getLabel());
                pstmt.setTimestamp(6, mark.getGradedAt() != null ? Timestamp.valueOf(mark.getGradedAt())
                        : Timestamp.valueOf(LocalDateTime.now()));
                pstmt.executeUpdate();
            }
        }
    }

    public void updateMarks(int registrationId, List<Mark> marks) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            saveMarks(conn, registrationId, marks);
        }
    }

    public void updateGrade(int registrationId, String grade, double gpa) throws SQLException {
        String sql = "UPDATE course_registrations SET final_grade = ?, gpa = ? WHERE registration_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, grade);
            pstmt.setDouble(2, gpa);
            pstmt.setInt(3, registrationId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Student findByRegistrationNo(String regNo) {
        if (regNo == null)
            return null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM students WHERE registration_no = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, regNo);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Student student = mapStudent(rs);
                        loadRegistrations(conn, student);
                        return student;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding student: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setUserId(rs.getInt("user_id"));
        s.setFullName(rs.getString("full_name"));
        s.setRegistrationNo(rs.getString("registration_no"));
        s.setPasswordHash(rs.getString("password_hash"));
        s.setSalt(rs.getString("salt"));
        s.setKeyPass(rs.getString("key_pass"));
        s.setProgramLevel(rs.getString("program_level"));
        s.setDegree(rs.getString("degree"));
        s.setSemester(rs.getInt("semester"));
        s.setCurrentSgpa(rs.getDouble("current_sgpa"));

        // New Fields
        try {
            s.setTotalQp(rs.getDouble("total_qp"));
            s.setSemesterGrade(rs.getString("semester_grade"));
            s.setSemCredits(rs.getInt("sem_credits"));
        } catch (SQLException e) {
            // Ignore if columns missing (compatibility)
        }
        return s;
    }

    private void loadRegistrations(Connection conn, Student student) throws SQLException {
        String sql = "SELECT cr.registration_id, cr.course_code, cr.registered_at, cr.final_grade, cr.gpa, " +
                "c.title, c.theory_credits, c.lab_credits, c.has_lab, c.is_fyp " +
                "FROM course_registrations cr " +
                "JOIN courses c ON cr.course_code = c.course_code " +
                "WHERE cr.student_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student.getUserId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CourseRegistration reg = new CourseRegistration();

                    // Create course
                    Course course = new Course(
                            rs.getString("course_code"),
                            rs.getString("title"),
                            rs.getInt("theory_credits"),
                            rs.getInt("lab_credits"));
                    course.setHasLab(rs.getBoolean("has_lab"));
                    course.setFYP(rs.getBoolean("is_fyp"));
                    reg.setCourse(course);

                    reg.setFinalGrade(rs.getString("final_grade"));
                    reg.setGpa(rs.getDouble("gpa"));

                    // Set registration time
                    Timestamp regTime = rs.getTimestamp("registered_at");
                    if (regTime != null) {
                        reg.setRegisteredAt(regTime.toLocalDateTime());
                    }

                    int registrationId = rs.getInt("registration_id");
                    reg.setId(registrationId);

                    // Load attendance record
                    loadAttendanceRecord(conn, registrationId, reg);

                    // Load marks
                    loadMarks(conn, registrationId, reg);

                    student.addRegistration(reg);
                }
            }
        }
    }

    private void loadAttendanceRecord(Connection conn, int registrationId, CourseRegistration reg) throws SQLException {
        String sql = "SELECT * FROM attendance_records WHERE registration_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, registrationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AttendanceRecord att = new AttendanceRecord();
                    att.setTheoryConducted(rs.getInt("theory_conducted"));
                    att.setTheoryAttended(rs.getInt("theory_attended"));
                    att.setLabConducted(rs.getInt("lab_conducted"));
                    att.setLabAttended(rs.getInt("lab_attended"));
                    reg.setAttendanceRecord(att);
                }
            }
        }
    }

    private void loadMarks(Connection conn, int registrationId, CourseRegistration reg) throws SQLException {
        String sql = "SELECT * FROM marks WHERE registration_id = ? ORDER BY graded_at";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, registrationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AssessmentType type = AssessmentType.valueOf(rs.getString("assessment_type"));
                    double obtained = rs.getDouble("obtained");
                    double total = rs.getDouble("total_marks");
                    String label = rs.getString("assessment_label");

                    Mark mark = new Mark(type, obtained, total, label);
                    Timestamp gradedAt = rs.getTimestamp("graded_at");
                    if (gradedAt != null) {
                        mark.setGradedAt(gradedAt.toLocalDateTime());
                    }
                    reg.addMark(mark);
                }
            }
        }
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM students ORDER BY registration_no";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Student student = mapStudent(rs);
                        loadRegistrations(conn, student);
                        students.add(student);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public void delete(String regNo) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM students WHERE registration_no = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, regNo);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean registerCourse(int studentId, String courseCode) {
        if (isRegistered(studentId, courseCode))
            return false;

        String sql = "INSERT INTO course_registrations (student_id, course_code, registered_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setString(2, courseCode);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registering course: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isRegistered(int studentId, String courseCode) {
        String sql = "SELECT 1 FROM course_registrations WHERE student_id = ? AND course_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setString(2, courseCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void updateCourse(model.Course course, String originalCourseCode) throws java.sql.SQLException {
        String sql = "UPDATE courses SET course_code = ?, title = ?, theory_credits = ?, lab_credits = ?, has_lab = ? WHERE course_code = ?";

        try (java.sql.Connection conn = DatabaseConnection.getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getTitle());
            pstmt.setInt(3, course.getTheoryCredits());
            pstmt.setInt(4, course.getLabCredits());
            pstmt.setBoolean(5, course.isHasLab());
            pstmt.setString(6, originalCourseCode);

            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new java.sql.SQLException("Update failed, no course found with code: " + originalCourseCode);
            }
        }
    }

    public void deleteStudentRegistration(int studentId, String courseCode) throws java.sql.SQLException {
        // Find Registration ID first
        String findRegSql = "SELECT registration_id FROM course_registrations WHERE student_id = ? AND course_code = ?";
        int registrationId = -1;

        try (java.sql.Connection conn = DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(findRegSql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    registrationId = rs.getInt("registration_id");
                }
            }
        }

        if (registrationId == -1) {
            throw new java.sql.SQLException(
                    "Registration not found for student " + studentId + " and course " + courseCode);
        }

        // Cascading Delete for THIS STUDENT ONLY: Attendance -> Marks -> Registration
        String sqlAttendance = "DELETE FROM attendance_records WHERE registration_id = ?";
        String sqlMarks = "DELETE FROM marks WHERE registration_id = ?";
        String sqlRegs = "DELETE FROM course_registrations WHERE registration_id = ?";

        try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction

            try {
                // 1. Delete Attendance
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlAttendance)) {
                    ps.setInt(1, registrationId);
                    ps.executeUpdate();
                }

                // 2. Delete Marks
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlMarks)) {
                    ps.setInt(1, registrationId);
                    ps.executeUpdate();
                }

                // 3. Delete Registration
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRegs)) {
                    ps.setInt(1, registrationId);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (java.sql.SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    // --- Report Management ---

    public void saveReport(int studentId, String title, String content) {
        String sql = "INSERT INTO student_reports (student_id, report_title, report_content) VALUES (?, ?, ?)";
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteReport(int reportId) {
        String sql = "DELETE FROM student_reports WHERE report_id = ?";
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public java.util.List<ReportItem> getStudentReports(int studentId) {
        java.util.List<ReportItem> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM student_reports WHERE student_id = ? ORDER BY generated_at DESC";

        try (java.sql.Connection conn = DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ReportItem(
                            rs.getInt("report_id"),
                            rs.getString("report_title"),
                            rs.getString("report_content"),
                            rs.getTimestamp("generated_at").toLocalDateTime()));
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Inner class for simple data transfer
    public static class ReportItem {
        private int id;
        private String title;
        private String content;
        private java.time.LocalDateTime date;

        public ReportItem(int id, String title, String content, java.time.LocalDateTime date) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public java.time.LocalDateTime getDate() {
            return date;
        }

        @Override
        public String toString() {
            // Format for JList display
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return date.format(fmt) + " - " + title;
        }
    }

    public model.CourseResult getCourseResult(int studentId, String courseCode) {
        String sql = "SELECT cr.registration_id, cr.final_grade, cr.gpa, " +
                "c.title, c.has_lab " +
                "FROM course_registrations cr " +
                "JOIN courses c ON cr.course_code = c.course_code " +
                "WHERE cr.student_id = ? AND cr.course_code = ?";

        model.CourseResult result = null;

        try (java.sql.Connection conn = DatabaseConnection.getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setString(2, courseCode);

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    boolean hasLab = rs.getBoolean("has_lab");
                    result = new model.CourseResult(courseCode, title, hasLab);

                    result.setGrade(rs.getString("final_grade"));
                    result.setGpa(rs.getDouble("gpa"));

                    // Load Marks
                    int regId = rs.getInt("registration_id");
                    CourseRegistration tempReg = new CourseRegistration(); // Helper to reuse logic
                    tempReg.setId(regId); // Only ID needed for loadMarks
                    loadMarks(conn, regId, tempReg);

                    // Distribute Marks into Result
                    double totalObtained = 0;
                    double totalMax = 0;

                    for (Mark m : tempReg.getMarks()) {
                        totalObtained += m.getObtained();
                        totalMax += m.getTotalMarks();

                        switch (m.getType()) {
                            case ASSIGNMENT:
                            case QUIZ:
                                result.addTheoryAssessment(m);
                                break;
                            case MID:
                                result.setTheoryMid(m);
                                break;
                            case FINAL:
                                result.setTheoryFinal(m);
                                break;
                            case LAB_ASSIGNMENT:
                                result.addLabAssessment(m);
                                break;
                            case LAB_MID:
                                result.setLabMid(m);
                                break;
                            case LAB_FINAL:
                                result.setLabFinal(m);
                                break;
                        }
                    }
                    result.setTotalObtained(totalObtained);
                    result.setTotalMax(totalMax);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void updateStudentSGPA(int studentId, double sgpa) {
        updateSemesterResult(studentId, sgpa, 0, null, 0);
    }

    public void updateSemesterResult(int studentId, double sgpa, double totalQp, String grade, int credits) {
        String sql = "UPDATE students SET current_sgpa = ?, total_qp = ?, semester_grade = ?, sem_credits = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, sgpa);
            pstmt.setDouble(2, totalQp);
            pstmt.setString(3, grade);
            pstmt.setInt(4, credits);
            pstmt.setInt(5, studentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteStudentAccount(int studentId) {
        // Deletes all data related to the student: reports, attendance, marks,
        // registrations, and the user account itself.
        // Note: 'marks' and 'attendance_records' are linked to 'course_registrations'
        // via registration_id.
        String[] deleteQueries = {
                "DELETE FROM student_reports WHERE student_id = ?",
                // Delete marks for all registrations of this student
                "DELETE FROM marks WHERE registration_id IN (SELECT registration_id FROM course_registrations WHERE student_id = ?)",
                // Delete attendance for all registrations of this student
                "DELETE FROM attendance_records WHERE registration_id IN (SELECT registration_id FROM course_registrations WHERE student_id = ?)",
                // Delete the registrations
                "DELETE FROM course_registrations WHERE student_id = ?",
                // Delete the student
                "DELETE FROM students WHERE user_id = ?"
        };

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction
            try {
                for (String sql : deleteQueries) {
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, studentId);
                        pstmt.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                lastError = e.getMessage(); // Capture error
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage(); // Capture connection error
            return false;
        }
    }
}
