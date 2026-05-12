package service;

import model.Student;
import repository.StudentRepository;
import util.HashUtil;

public class AuthService {
    private StudentRepository repo;

    public AuthService(StudentRepository repo) {
        this.repo = repo;
    }

    public Student register(String fullName, String regNo, String password, String keyPass, String programLevel,
            String degree, int semester) {
        if (repo.findByRegistrationNo(regNo) != null)
            return null;

        String salt = HashUtil.generateSalt();
        String hash = HashUtil.hashPassword(password, salt);

        Student s = new Student();
        s.setRegistrationNo(regNo);
        s.setPasswordHash(hash);
        s.setSalt(salt);
        s.setKeyPass(keyPass);
        s.setFullName(fullName);
        s.setProgramLevel(programLevel == null ? "" : programLevel);
        s.setDegree(degree);
        s.setSemester(semester);

        // Save and return the stored instance
        Student stored = repo.save(s);
        return stored != null ? stored : s;
    }

    // add near other methods in AuthService
    public repository.StudentRepository getRepo() {
        return this.repo;
    }

    public Student login(String regNo, String password) {
        Student s = repo.findByRegistrationNo(regNo);
        if (s == null)
            return null;
        String hash = HashUtil.hashPassword(password, s.getSalt());
        if (hash.equals(s.getPasswordHash())) {
            // return the live repo instance to ensure later updates are visible
            return repo.findByRegistrationNo(regNo);
        }
        return null;
    }

    public boolean resetPassword(String regNo, String keyPass, String newPassword) {
        Student s = repo.findByRegistrationNo(regNo);
        if (s == null)
            return false;
        if (s.getKeyPass() == null || !s.getKeyPass().equals(keyPass))
            return false;

        String salt = HashUtil.generateSalt();
        String hash = HashUtil.hashPassword(newPassword, salt);
        s.setSalt(salt);
        s.setPasswordHash(hash);
        repo.save(s);
        return true;
    }
}
