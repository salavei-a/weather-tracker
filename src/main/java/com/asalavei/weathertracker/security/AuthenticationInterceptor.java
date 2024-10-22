package com.asalavei.weathertracker.security;

import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Set<String> AUTH_PAGES = Set.of("/auth/signin", "/auth/signup");

    private final SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        Cookie[] cookies = request.getCookies();
        Cookie sessionCookie = null;
        String sessionId = null;

        if (cookies != null) {
            sessionCookie = Arrays.stream(cookies)
                    .filter(cookie -> "sessionid".equals(cookie.getName()))
                    .findFirst()
                    .orElse(null);

            if (sessionCookie != null) {
                sessionId = sessionCookie.getValue();
            }
        }

        Optional<Session> session = sessionService.getValidSession(sessionId);

        if (AUTH_PAGES.contains(requestUri)) {
            if (session.isPresent()) {
                response.sendRedirect("/");
                return false;
            } else {
                return true;
            }
        }

        if (session.isEmpty()) {
            response.sendRedirect("/auth/signin");
            return false;
        }

        sessionService.extendSession(sessionId);
        extendCookie(sessionCookie, response);

        SecurityContext.setAuthenticatedUser(session.get().getUser());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContext.clear();
    }

    private void extendCookie(Cookie cookie, HttpServletResponse response) {
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);
        response.addCookie(cookie);
    }
}
