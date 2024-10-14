package com.asalavei.weathertracker.security;

import com.asalavei.weathertracker.entity.User;

/**
 * Stored authenticated user for the current request
 */
public class SecurityContext {

    private static final ThreadLocal<User> context = new ThreadLocal<>();

    private SecurityContext() {
    }

    public static void setAuthenticatedUser(User user) {
        context.set(user);
    }

    public static User getAuthenticatedUser() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
