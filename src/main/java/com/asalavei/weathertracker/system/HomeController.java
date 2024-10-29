package com.asalavei.weathertracker.system;

import com.asalavei.weathertracker.auth.User;
import com.asalavei.weathertracker.auth.UserMapper;
import com.asalavei.weathertracker.auth.AuthenticatedUserContext;
import com.asalavei.weathertracker.weather.weatherapi.WeatherService;
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
        User user = AuthenticatedUserContext.getAuthenticatedUser();

        if (user != null) {
            model.addAttribute("user", userMapper.toDto(user));
            model.addAttribute("locations", weatherService.getUserLocationsWeather(user.getId()));
        }

        return "home";
    }
}
