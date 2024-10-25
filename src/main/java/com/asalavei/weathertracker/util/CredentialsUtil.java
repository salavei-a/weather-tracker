package com.asalavei.weathertracker.util;

import org.mindrot.jbcrypt.BCrypt;

public class CredentialsUtil {

    private CredentialsUtil() {
    }

    public static String normalizeUsername(String username) {
        return username.trim().toLowerCase();
    }

    public static String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
