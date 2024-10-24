package com.asalavei.weathertracker.controller;

import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.service.AuthenticationService;
import com.asalavei.weathertracker.service.SessionService;
import com.asalavei.weathertracker.service.UserService;
import com.asalavei.weathertracker.dto.UserRequestDto;
import com.asalavei.weathertracker.util.SessionCookieManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    @GetMapping("/signin")
    public String signInForm(@ModelAttribute("user") UserRequestDto userRequestDto) {
        return "auth/signin";
    }

    @PostMapping("/signin")
    public String signIn(@Valid @ModelAttribute("user") UserRequestDto userRequestDto, BindingResult bindingResult,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "auth/signin";
        }

        Session session = authenticationService.authenticate(userRequestDto);
        SessionCookieManager.createSessionCookie(response, session.getId());

        String redirectTo = request.getParameter("redirect_to");
        if (!StringUtils.isBlank(redirectTo)) {
            return "redirect:" + redirectTo;
        }

        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signUpForm(@ModelAttribute("user") UserRequestDto userRequestDto) {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signUp(@Valid @ModelAttribute("user") UserRequestDto userRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        userService.register(userRequestDto);
        return "redirect:/auth/signin";
    }

    @PostMapping("/signout")
    public String signOut(@CookieValue(value = SessionCookieManager.SESSION_COOKIE_NAME, defaultValue = "") String sessionId, HttpServletResponse response) {
        sessionService.invalidate(sessionId);
        SessionCookieManager.invalidateSessionCookie(response);

        return "redirect:/auth/signin";
    }
}
