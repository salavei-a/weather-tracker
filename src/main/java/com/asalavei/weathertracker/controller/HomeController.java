package com.asalavei.weathertracker.controller;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.mapper.UserMapper;
import com.asalavei.weathertracker.security.SecurityContext;
import com.asalavei.weathertracker.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final WeatherService weatherService;
    private final UserMapper userMapper;

    @GetMapping
    public String homePage(Model model) {
        User user = SecurityContext.getAuthenticatedUser();

        if (user != null) {
            model.addAttribute("user", userMapper.toDto(user));
            model.addAttribute("locations", weatherService.getUserLocationsWeather(user.getId()));
        }

        return "home";
    }
}
