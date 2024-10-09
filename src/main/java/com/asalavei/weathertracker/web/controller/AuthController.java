package com.asalavei.weathertracker.web.controller;

import com.asalavei.weathertracker.dbaccess.entity.Session;
import com.asalavei.weathertracker.dbaccess.entity.User;
import com.asalavei.weathertracker.service.AuthenticationService;
import com.asalavei.weathertracker.service.SessionService;
import com.asalavei.weathertracker.service.UserService;
import com.asalavei.weathertracker.dto.UserRequestDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final SessionService sessionService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(SessionService sessionService, UserService userService, AuthenticationService authenticationService) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/signin")
    public String signInPage(@ModelAttribute("user") UserRequestDto userRequestDto) {
        return "auth/signin";
    }

    @PostMapping("/signin")
    public String processSignIn(@ModelAttribute("user") UserRequestDto userRequestDto, HttpServletResponse response) {
        User user = authenticationService.authenticate(userRequestDto);
        Session session = sessionService.create(user);
        Cookie cookie = new Cookie("sessionid", session.getId());

        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);

        response.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signUpPage(@ModelAttribute("user") UserRequestDto userRequestDto) {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String processSignUp(@ModelAttribute("user") UserRequestDto userRequestDto) {
        userService.create(userRequestDto);
        return "redirect:/auth/signin";
    }

    @PostMapping("/logout")
    public String processLogOut(@CookieValue(value = "sessionid", defaultValue = "") String sessionId, HttpServletResponse response) {
        Cookie cookie = new Cookie("sessionid", null);

        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        sessionService.invalidate(sessionId);

        return "redirect:/auth/signin";
    }
}
