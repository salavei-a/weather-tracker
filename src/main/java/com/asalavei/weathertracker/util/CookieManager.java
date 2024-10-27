package com.asalavei.weathertracker.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieManager {

    private CookieManager() {
    }

    public static void createCookie(String cookieName, int cookieMaxAge, String cookieValue, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        response.addCookie(cookie);
    }

    public static void invalidateCookie(String cookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static void extendCookie(String cookieName, int cookieMaxAge, String cookieValue, HttpServletResponse response) {
        createCookie(cookieName, cookieMaxAge, cookieValue, response);
    }
}
