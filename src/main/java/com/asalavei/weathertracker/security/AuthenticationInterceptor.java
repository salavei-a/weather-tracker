package com.asalavei.weathertracker.security;

import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.service.SessionService;
import com.asalavei.weathertracker.util.CookieManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Set<String> AUTH_PAGES = Set.of("/auth/signin", "/auth/signup");

    private final SessionService sessionService;

    @Value("${weather-tracker.session-cookie-name}")
    private String sessionCookieName;

    @Value("${weather-tracker.session-cookie-max-age}")
    private int sessionCookieMaxAge;

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
            String encodedUrl = URLEncoder.encode(getFullUrl(request), StandardCharsets.UTF_8);
            response.sendRedirect("/auth/signin?redirect_to=" + encodedUrl);
            return false;
        }

        Session session = sessionOptional.get();
        String sessionId = session.getId();

        if (isSessionNearExpiration(session)) {
            sessionService.extendSession(sessionId);
            CookieManager.extendCookie(sessionCookieName, sessionCookieMaxAge, sessionId, response);
        }

        SecurityContext.setAuthenticatedUser(session.getUser());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContext.clear();
    }

    private Optional<String> getSessionIdFromCookies(Cookie[] cookies) {
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> sessionCookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private boolean isAuthPage(String uri) {
        return AUTH_PAGES.contains(uri);
    }

    private String getFullUrl(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        return (queryString == null) ?
                requestURL.toString() :
                requestURL.append('?').append(queryString).toString();
    }

    private boolean isSessionNearExpiration(Session session) {
        return LocalDateTime.now().isAfter(session.getExpiresAt().minusMinutes(5));
    }
}
