package com.asalavei.weathertracker.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class SessionCookieManager {

    private static final int COOKIE_MAX_AGE = 30 * 60;
    public static final String SESSION_COOKIE_NAME = "sessionid";

    private SessionCookieManager() {
    }

    public static void createSessionCookie(HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    public static void invalidateSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static void extendSessionCookie(HttpServletResponse response, String sessionId) {
        createSessionCookie(response, sessionId);
    }
}
