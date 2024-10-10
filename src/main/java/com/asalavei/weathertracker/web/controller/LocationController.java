package com.asalavei.weathertracker.web.controller;

import com.asalavei.weathertracker.dbaccess.entity.User;
import com.asalavei.weathertracker.dto.LocationRequestDto;
import com.asalavei.weathertracker.dto.LocationResponseDto;
import com.asalavei.weathertracker.service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/locations")
public class LocationController {

    private final WeatherService weatherService;

    @Autowired
    public LocationController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public String search(@ModelAttribute("location") LocationRequestDto locationRequestDto, HttpServletRequest req, Model model) {
        User user = (User) req.getAttribute("authenticatedUser");

        if (user == null) {
            return "redirect:/auth/signin";
        }

        if (locationRequestDto.getName() == null) {
            return "locations";
        }

        List<LocationResponseDto> locations = weatherService.getLocationDetails(locationRequestDto.getName());

        model.addAttribute("user", user);
        model.addAttribute("locations", locations);

        return "locations";
    }
}
