package com.asalavei.weathertracker.controller;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.dto.LocationRequestDto;
import com.asalavei.weathertracker.dto.LocationResponseDto;
import com.asalavei.weathertracker.mapper.UserMapper;
import com.asalavei.weathertracker.security.SecurityContext;
import com.asalavei.weathertracker.service.LocationService;
import com.asalavei.weathertracker.service.WeatherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/locations")
public class LocationController {

    private final WeatherService weatherService;
    private final LocationService locationService;
    private final UserMapper userMapper;

    @Autowired
    public LocationController(WeatherService weatherService, LocationService locationService, UserMapper userMapper) {
        this.weatherService = weatherService;
        this.locationService = locationService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String search(@Valid @ModelAttribute("location") LocationRequestDto location, BindingResult bindingResult, Model model) {
        User user = SecurityContext.getAuthenticatedUser();
        model.addAttribute("user", userMapper.toDto(user));

        if (bindingResult.hasErrors()) {
            return "locations";
        }

        List<LocationResponseDto> locations = weatherService.fetchLocationDetails(location.getName());

        model.addAttribute("locations", locations);

        return "locations";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("location") LocationRequestDto locationRequest) {
        User user = SecurityContext.getAuthenticatedUser();

        locationService.create(locationRequest, user);

        return "redirect:/";
    }

    @DeleteMapping("/delete")
    public String delete(@ModelAttribute("location") LocationRequestDto locationRequest) {
        User user = SecurityContext.getAuthenticatedUser();

        locationService.deleteByNameAndUserId(locationRequest.getName(), user.getId());

        return "redirect:/";
    }
}