package com.Employeemanagementsystem.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Simple PBKDF2 password hashing utility.
 * Stores hashes in the format: iterations:saltBase64:hashBase64
 */
public final class PasswordUtils {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LENGTH = 16; // bytes
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // bits

    private PasswordUtils() {}

    public static String hashPassword(char[] password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(salt);

            byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);

            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hash);
            return ITERATIONS + ":" + saltB64 + ":" + hashB64;
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public static boolean verifyPassword(char[] password, String stored) {
        if (stored == null) return false;
        // Backward compatibility: if stored looks like a bcrypt hash ($2a$... or $2b$...), try BCrypt via reflection
        if (stored.startsWith("$2")) {
            try {
                Class<?> bc = Class.forName("org.mindrot.jbcrypt.BCrypt");
                java.lang.reflect.Method checkpw = bc.getMethod("checkpw", String.class, String.class);
                String plain = password == null ? "" : new String(password);
                Object result = checkpw.invoke(null, plain, stored);
                if (result instanceof Boolean) return (Boolean) result;
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException ex) {
                // BCrypt not available or invocation failed; fall through to PBKDF2 check which will return false
            }
        }
        String[] parts = stored.split(":");
        if (parts.length != 3) return false;
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] hash = Base64.getDecoder().decode(parts[2]);
        try {
            byte[] testHash = pbkdf2(password, salt, iterations, hash.length * 8);
            return slowEquals(hash, testHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    private static boolean slowEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) diff |= a[i] ^ b[i];
        return diff == 0;
    }

    // Convenience overloads that take String
    public static String hashPassword(String plain) {
        return hashPassword(plain == null ? new char[0] : plain.toCharArray());
    }

    public static boolean verifyPassword(String plain, String stored) {
        return verifyPassword(plain == null ? new char[0] : plain.toCharArray(), stored);
    }
}
