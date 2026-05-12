package model;

/**
 * Abstract user class: basic auth fields.
 */
public abstract class User {
    protected int userId;
    protected String registrationNo; // username
    protected String passwordHash;
    protected String salt;
    protected String keyPass; // for forget password
    protected String role; // "STUDENT", "ADMIN", etc.

    public User() {}

    public User(String registrationNo, String passwordHash, String salt, String keyPass, String role) {
        this.registrationNo = registrationNo;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.keyPass = keyPass;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getKeyPass() { return keyPass; }
    public void setKeyPass(String keyPass) { this.keyPass = keyPass; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
