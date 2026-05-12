package util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class HashUtil {
    public static String generateSalt() {
        SecureRandom rnd = new SecureRandom();
        byte[] b = new byte[16];
        rnd.nextBytes(b);
        return Base64.getEncoder().encodeToString(b);
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((salt + password).getBytes("UTF-8"));
            byte[] digest = md.digest();
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
