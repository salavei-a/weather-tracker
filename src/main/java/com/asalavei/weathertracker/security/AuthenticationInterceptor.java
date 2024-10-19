package com.asalavei.weathertracker.security;

import com.asalavei.weathertracker.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            response.sendRedirect("/auth/signin");
            return false;
        }

        String sessionId = Arrays.stream(cookies)
                .filter(cookie -> "sessionid".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (sessionId == null || !sessionService.isSessionValid(sessionId)) {
            response.sendRedirect("/auth/signin");
            return false;
        }

        SecurityContext.setAuthenticatedUser(sessionService.getUserById(sessionId));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContext.clear();
    }
}
