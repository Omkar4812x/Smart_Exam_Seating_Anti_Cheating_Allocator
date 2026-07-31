package com.examseating.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PasswordUtil - SHA-256 password hashing utility.
 * Used for storing and verifying admin user passwords.
 */
public class PasswordUtil {

    /**
     * Hashes a plain-text password using SHA-256.
     * 
     * @param password the plain-text password
     * @return the SHA-256 hex digest (64 characters)
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            // Pad with leading zeros to ensure 64 characters
            while (hexString.length() < 64) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies a plain-text password against a stored hash.
     * 
     * @param password the plain-text password to verify
     * @param storedHash the stored SHA-256 hash
     * @return true if the password matches
     */
    public static boolean verifyPassword(String password, String storedHash) {
        String hash = hashPassword(password);
        return hash.equals(storedHash);
    }
}
