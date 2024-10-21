package com.asalavei.weathertracker.security;

import com.asalavei.weathertracker.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
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
        String sessionId = null;

        if (cookies != null) {
            sessionId = Arrays.stream(cookies)
                    .filter(cookie -> "sessionid".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        boolean isSessionValid = sessionId != null && sessionService.isSessionValid(sessionId);

        if (AUTH_PAGES.contains(requestUri)) {
            if (isSessionValid) {
                response.sendRedirect("/");
                return false;
            } else {
                return true;
            }
        }

        if (!isSessionValid) {
            response.sendRedirect("/auth/signin");
            return false;
        }

        // TODO: avoid User with password
        // TODO: add logging?
        SecurityContext.setAuthenticatedUser(sessionService.getUserById(sessionId));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContext.clear();
    }
}
