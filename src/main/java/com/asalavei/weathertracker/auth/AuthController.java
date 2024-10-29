package com.asalavei.weathertracker.auth;

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

import static com.asalavei.weathertracker.common.Constants.*;

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
    public String signInForm(@ModelAttribute(USER_ATTRIBUTE) SignInRequestDto signInRequest) {
        return SIGNIN_VIEW;
    }

    @PostMapping("/signin")
    public String signIn(@Valid @ModelAttribute(USER_ATTRIBUTE) SignInRequestDto signInRequest, BindingResult bindingResult,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return SIGNIN_VIEW;
        }

        Session session = authenticationService.authenticate(signInRequest);
        CookieManager.createCookie(sessionCookieName, sessionCookieMaxAge, session.getId(), response);

        String redirectTo = request.getParameter(REDIRECT_TO_PARAM);
        if (!StringUtils.isBlank(redirectTo)) {
            return "redirect:" + redirectTo;
        }

        return REDIRECT_HOME;
    }

    @GetMapping("/signup")
    public String signUpForm(@ModelAttribute(USER_ATTRIBUTE) SignUpRequestDto signUpRequest) {
        return SIGNUP_VIEW;
    }

    @PostMapping("/signup")
    public String signUp(@Valid @ModelAttribute(USER_ATTRIBUTE) SignUpRequestDto signUpRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return SIGNUP_VIEW;
        }

        userService.register(signUpRequest);
        return REDIRECT_SIGNIN;
    }

    @PostMapping("/signout")
    public String signOut(@CookieValue(value = "${weather-tracker.session-cookie-name}", defaultValue = "") String sessionId, HttpServletResponse response) {
        sessionService.invalidate(sessionId);
        CookieManager.invalidateCookie(sessionCookieName, response);

        return REDIRECT_SIGNIN;
    }
}
