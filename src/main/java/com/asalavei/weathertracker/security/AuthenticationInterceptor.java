package com.asalavei.weathertracker.security;

import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String SESSION_COOKIE_NAME = "sessionid";
    private static final Set<String> AUTH_PAGES = Set.of("/auth/signin", "/auth/signup");

    private final SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<Session> sessionOptional = getSessionIdFromCookies(request.getCookies())
                .flatMap(sessionService::getValidSession);

        if (isAuthPage(request.getRequestURI())) {
            if (sessionOptional.isPresent()) {
                response.sendRedirect("/");
                return false;
            } else {
                return true;
            }
        }

        if (sessionOptional.isEmpty()) {
            response.sendRedirect("/auth/signin");
            return false;
        }

        Session session = sessionOptional.get();
        String sessionId = session.getId();

        if (isSessionNearExpiration(session)) {
            sessionService.extendSession(sessionId);
            extendCookie(sessionId, response);
        }

        SecurityContext.setAuthenticatedUser(session.getUser());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContext.clear();
    }

    private Optional<String> getSessionIdFromCookies(Cookie[] cookies) {
        return Arrays.stream(cookies)
                .filter(cookie -> SESSION_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private boolean isAuthPage(String uri) {
        return AUTH_PAGES.contains(uri);
    }

    private boolean isSessionNearExpiration(Session session) {
        return LocalDateTime.now().isAfter(session.getExpiresAt().minusMinutes(5));
    }

    private void extendCookie(String sessionId, HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);
        response.addCookie(cookie);
    }
}
