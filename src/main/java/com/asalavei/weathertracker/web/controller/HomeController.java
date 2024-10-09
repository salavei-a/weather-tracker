package com.asalavei.weathertracker.web.controller;

import com.asalavei.weathertracker.dbaccess.entity.User;
import com.asalavei.weathertracker.mapper.UserMapper;
import com.asalavei.weathertracker.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    private final SessionService sessionService;
    private final UserMapper userMapper;

    @Autowired
    public HomeController(SessionService sessionService, UserMapper userMapper) {
        this.sessionService = sessionService;
        this.userMapper = userMapper;
    }


    @GetMapping
    public String homePage(@CookieValue(value = "sessionid", defaultValue = "") String sessionId, Model model) {
        if (sessionId.isEmpty()) {
            model.addAttribute("authenticated", false);
        } else {
            User user = sessionService.getUserById(sessionId);

            if (user != null) {
                model.addAttribute("authenticated", true);
                model.addAttribute("user", userMapper.toDto(user));
//                model.addAttribute("locations", locationService.getByUserId(user.getId()));
            } else {
                model.addAttribute("authenticated", false);
            }
        }

        return "home";
    }
}
