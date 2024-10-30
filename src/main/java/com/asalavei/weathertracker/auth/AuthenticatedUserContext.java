package com.asalavei.weathertracker.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Stored authenticated user for the current request
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticatedUserContext {

    private static final ThreadLocal<User> context = new ThreadLocal<>();

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
