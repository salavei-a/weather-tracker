package com.asalavei.weathertracker.controller;

import com.asalavei.weathertracker.dto.SignInRequestDto;
import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.service.AuthenticationService;
import com.asalavei.weathertracker.service.SessionService;
import com.asalavei.weathertracker.service.UserService;
import com.asalavei.weathertracker.dto.SignUpRequestDto;
import com.asalavei.weathertracker.util.CookieManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final SessionService sessionService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Value("${weather-tracker.session-cookie-name}")
    private String sessionCookieName;

    @Value("${weather-tracker.session-cookie-max-age}")
    private int sessionCookieMaxAge;

    @GetMapping("/signin")
    public String signInForm(@ModelAttribute("user") SignInRequestDto signInRequest) {
        return "auth/signin";
    }

    @PostMapping("/signin")
    public String signIn(@Valid @ModelAttribute("user") SignInRequestDto signInRequest, BindingResult bindingResult,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "auth/signin";
        }

        Session session = authenticationService.authenticate(signInRequest);
        CookieManager.createCookie(sessionCookieName, sessionCookieMaxAge, session.getId(), response);

        String redirectTo = request.getParameter("redirect_to");
        if (!StringUtils.isBlank(redirectTo)) {
            return "redirect:" + redirectTo;
        }

        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signUpForm(@ModelAttribute("user") SignUpRequestDto signUpRequest) {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signUp(@Valid @ModelAttribute("user") SignUpRequestDto signUpRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        userService.register(signUpRequest);
        return "redirect:/auth/signin";
    }

    @PostMapping("/signout")
    public String signOut(@CookieValue(value = "${weather-tracker.session-cookie-name}", defaultValue = "") String sessionId, HttpServletResponse response) {
        sessionService.invalidate(sessionId);
        CookieManager.invalidateCookie(sessionCookieName, response);

        return "redirect:/auth/signin";
    }
}
