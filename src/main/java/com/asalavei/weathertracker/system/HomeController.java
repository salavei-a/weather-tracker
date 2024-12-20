package com.asalavei.weathertracker.system;

import com.asalavei.weathertracker.auth.user.User;
import com.asalavei.weathertracker.auth.user.UserMapper;
import com.asalavei.weathertracker.auth.AuthenticatedUserThreadLocal;
import com.asalavei.weathertracker.weather.UserWeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.asalavei.weathertracker.common.Constants.HOME_VIEW;
import static com.asalavei.weathertracker.common.Constants.LOCATIONS_ATTRIBUTE;
import static com.asalavei.weathertracker.common.Constants.USER_ATTRIBUTE;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final UserWeatherService userWeatherService;
    private final UserMapper userMapper;

    @GetMapping
    public String homePage(Model model) {
        User user = AuthenticatedUserThreadLocal.getAuthenticatedUser();

        if (user != null) {
            model.addAttribute(USER_ATTRIBUTE, userMapper.toDto(user));
            model.addAttribute(LOCATIONS_ATTRIBUTE, userWeatherService.getUserLocationsWeather(user.getId()));
        }

        return HOME_VIEW;
    }
}
