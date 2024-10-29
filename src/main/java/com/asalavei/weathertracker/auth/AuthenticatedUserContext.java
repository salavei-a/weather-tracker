package com.asalavei.weathertracker.auth;

/**
 * Stored authenticated user for the current request
 */
public class AuthenticatedUserContext {

    private static final ThreadLocal<User> context = new ThreadLocal<>();

    private AuthenticatedUserContext() {
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
