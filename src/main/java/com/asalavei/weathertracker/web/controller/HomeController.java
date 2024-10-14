package com.asalavei.weathertracker.web.controller;

import com.asalavei.weathertracker.dbaccess.entity.User;
import com.asalavei.weathertracker.mapper.UserMapper;
import com.asalavei.weathertracker.security.SecurityContext;
import com.asalavei.weathertracker.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    private final WeatherService weatherService;
    private final UserMapper userMapper;

    @Autowired
    public HomeController(UserMapper userMapper, WeatherService weatherService) {
        this.userMapper = userMapper;
        this.weatherService = weatherService;
    }

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
