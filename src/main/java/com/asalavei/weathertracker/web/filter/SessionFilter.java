package com.asalavei.weathertracker.web.filter;

import com.asalavei.weathertracker.service.SessionService;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component("sessionFilter")
public class SessionFilter extends HttpFilter {

    private final SessionService sessionService;

    @Autowired
    public SessionFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            String sessionId = Arrays.stream(cookies)
                    .filter(cookie -> "sessionid".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (sessionId == null || !sessionService.isSessionValid(sessionId)) {
                res.sendRedirect("/auth/signin");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
