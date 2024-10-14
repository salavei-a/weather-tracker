package com.asalavei.weathertracker.web.filter;

import com.asalavei.weathertracker.dbaccess.entity.User;
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

@Component("authenticationFilter")
public class AuthenticationFilter extends HttpFilter {

    private final SessionService sessionService;

    @Autowired
    public AuthenticationFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req.getRequestURI().startsWith("/auth/")) {
            chain.doFilter(req, res);
            return;
        }

        Cookie[] cookies = req.getCookies();

        if (cookies == null) {
            res.sendRedirect("/auth/signin");
            return;
        }

        String sessionId = Arrays.stream(cookies)
                .filter(cookie -> "sessionid".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (sessionId == null || !sessionService.isSessionValid(sessionId)) {
            res.sendRedirect("/auth/signin");
            return;
        }

        User user = sessionService.getUserById(sessionId);

        if (user != null) {
            req.setAttribute("authenticatedUser", user);
        }

        chain.doFilter(req, res);
    }
}
