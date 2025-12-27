package com.pend.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Simple salt + pepper hashing utility using SHA-256.
 *
 * Notes:
 * - Pepper should be stored OUTSIDE the database (environment variable recommended).
 * - This implementation uses per-user random salt and a pepper read from env var "PEND_PEPPER".
 */
public class PasswordUtil {

    // Fallback pepper (only used if env var is not set). In production, DO NOT rely on this.
    private static final String FALLBACK_PEPPER = "CHANGE_THIS_FALLBACK_PEPPER";

    public static String getPepper() {
        String p = System.getenv("PEND_PEPPER");
        if (p != null && !p.isBlank()) return p;
        return FALLBACK_PEPPER;
    }

    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes input (username or password) using: SHA256( value + pepper + salt )
     */
    public static String hash(String value, String salt) {
        try {
            String pepper = getPepper();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String combined = value + pepper + salt;
            byte[] digest = md.digest(combined.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
}
