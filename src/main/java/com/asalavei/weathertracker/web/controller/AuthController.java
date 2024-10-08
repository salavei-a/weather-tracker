package com.asalavei.weathertracker.web.controller;

import com.asalavei.weathertracker.web.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/signin")
    public String signInPage(@ModelAttribute("user") UserDto userDto) {
        return "auth/signin";
    }

    @PostMapping("/signin")
    public String processSignIn(@ModelAttribute("user") UserDto userDto) {
        // TODO: process sign in

        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signUpPage(@ModelAttribute("user") UserDto userDto) {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String processSignUp(@ModelAttribute("user") UserDto userDto) {
        // TODO: registrationService.register()

        return "redirect:/auth/signin";
    }
}
