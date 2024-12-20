package com.asalavei.weathertracker.auth;

import com.asalavei.weathertracker.auth.user.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Stored authenticated user for the current request
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticatedUserThreadLocal {

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
