package com.asalavei.weathertracker.auth;

import com.asalavei.weathertracker.auth.session.Session;
import com.asalavei.weathertracker.auth.session.SessionService;
import com.asalavei.weathertracker.util.CookieManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.asalavei.weathertracker.common.Constants.AUTH_PAGES;
import static com.asalavei.weathertracker.common.Constants.HOME_URL;
import static com.asalavei.weathertracker.common.Constants.SIGNIN_URL_WITH_REDIRECT;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    @Value("${weather-tracker.session-cookie-name}")
    private String sessionCookieName;

    @Value("${weather-tracker.session-cookie-max-age}")
    private int sessionCookieMaxAge;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<Session> sessionOptional = CookieManager.getValueFromCookies(request.getCookies(), sessionCookieName)
                .flatMap(sessionService::getValidSession);

        if (isAuthPage(request.getRequestURI())) {
            if (sessionOptional.isPresent()) {
                response.sendRedirect(HOME_URL);
                return false;
            }

            return true;
        }

        if (sessionOptional.isEmpty()) {
            String encodedUrl = URLEncoder.encode(getFullUrl(request), StandardCharsets.UTF_8);
            response.sendRedirect(SIGNIN_URL_WITH_REDIRECT + encodedUrl);
            return false;
        }

        Session session = sessionOptional.get();
        String sessionId = session.getId();

        if (sessionService.isSessionNearExpiration(session)) {
            sessionService.extendSession(sessionId);
            CookieManager.extendCookie(sessionCookieName, sessionCookieMaxAge, sessionId, response);
        }

        AuthenticatedUserThreadLocal.setAuthenticatedUser(session.getUser());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthenticatedUserThreadLocal.clear();
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
}
