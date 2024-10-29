package com.asalavei.weathertracker.system;

import com.asalavei.weathertracker.auth.User;
import com.asalavei.weathertracker.auth.UserMapper;
import com.asalavei.weathertracker.auth.AuthenticatedUserContext;
import com.asalavei.weathertracker.weather.UserWeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.asalavei.weathertracker.common.Constants.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final UserWeatherService userWeatherService;
    private final UserMapper userMapper;

    @GetMapping
    public String homePage(Model model) {
        User user = AuthenticatedUserContext.getAuthenticatedUser();

        if (user != null) {
            model.addAttribute(USER_ATTRIBUTE, userMapper.toDto(user));
            model.addAttribute(LOCATIONS_ATTRIBUTE, userWeatherService.getUserLocationsWeather(user.getId()));
        }

        return HOME_VIEW;
    }
}
