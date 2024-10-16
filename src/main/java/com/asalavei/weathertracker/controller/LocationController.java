package com.asalavei.weathertracker.controller;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.dto.LocationRequestDto;
import com.asalavei.weathertracker.dto.LocationResponseDto;
import com.asalavei.weathertracker.mapper.UserMapper;
import com.asalavei.weathertracker.security.SecurityContext;
import com.asalavei.weathertracker.service.LocationService;
import com.asalavei.weathertracker.service.WeatherService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
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
        if (bindingResult.hasErrors()) {
            return "locations";
        }

        User user = SecurityContext.getAuthenticatedUser();
        model.addAttribute("user", userMapper.toDto(user));

        List<LocationResponseDto> locations = weatherService.fetchLocationDetails(location.getName());

        model.addAttribute("locations", locations);

        return "locations";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("location") LocationRequestDto locationRequest, BindingResult bindingResult) {
        User user = SecurityContext.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            logBindingResultErrors("add", bindingResult, user.getUsername());
            return "redirect:/";
        }

        BigDecimal latitude = locationRequest.getLatitude();
        BigDecimal longitude = locationRequest.getLongitude();

        if (!weatherService.isWeatherDataAvailable(latitude, longitude)) {
            log.warn("Attempt to add non-existent location by user: {}. " +
                     "Potentially manipulated location details: name: {}, latitude: {}, longitude: {}",
                    user.getUsername(), locationRequest.getName(), latitude, longitude);
            return "redirect:/";
        }

        locationService.create(locationRequest, user);

        return "redirect:/";
    }

    @DeleteMapping("/delete")
    public String delete(@ModelAttribute("location") LocationRequestDto locationRequest, BindingResult bindingResult) {
        User user = SecurityContext.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            logBindingResultErrors("delete", bindingResult, user.getUsername());
            return "redirect:/";
        }

        locationService.delete(locationRequest, user.getId());

        return "redirect:/";
    }

    private void logBindingResultErrors(String action, BindingResult bindingResult, String username) {
        String errorMessages = bindingResult.getFieldErrors().stream()
                .map(fieldError -> String.format("Field: %s, Rejected value: %s, Message: %s",
                        fieldError.getField(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()))
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Unknown validation error");

        log.warn("Attempt to {} location failed due to potentially manipulated input by user: {}. Errors: {}",
                action, username, errorMessages);
    }
}
