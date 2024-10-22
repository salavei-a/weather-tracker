package com.asalavei.weathertracker.controller;

import com.asalavei.weathertracker.dto.LocationSearchRequestDto;
import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.dto.LocationRequestDto;
import com.asalavei.weathertracker.dto.LocationResponseDto;
import com.asalavei.weathertracker.mapper.UserMapper;
import com.asalavei.weathertracker.security.SecurityContext;
import com.asalavei.weathertracker.service.LocationService;
import com.asalavei.weathertracker.service.WeatherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private static final String SEARCH = "search";
    private static final String ADD = "add";
    private static final String DELETE = "delete";

    private final WeatherService weatherService;
    private final LocationService locationService;
    private final UserMapper userMapper;

    @GetMapping
    public String search(@Valid @ModelAttribute("location") LocationSearchRequestDto location, BindingResult bindingResult, Model model) {
        User user = SecurityContext.getAuthenticatedUser();
        model.addAttribute("user", userMapper.toDto(user));

        if (bindingResult.hasErrors()) {
            logBindingResultErrors(SEARCH, bindingResult, user.getUsername());
            return "locations";
        }

        List<LocationResponseDto> locations = weatherService.fetchLocationDetails(location.getName());
        model.addAttribute("locations", locations);

        return "locations";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("location") LocationRequestDto location, BindingResult bindingResult) {
        User user = SecurityContext.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            logBindingResultErrors(ADD, bindingResult, user.getUsername());
            return "redirect:/";
        }

        if (!weatherService.locationExists(location)) {
            log.warn("Attempt to add non-existent location by user={} with potentially manipulated location details. " +
                     "name={}, latitude={}, longitude={}",
                    user.getUsername(), location.getName(), location.getLatitude(), location.getLongitude());
            return "redirect:/";
        }

        locationService.create(location, user);

        return "redirect:/";
    }

    @DeleteMapping("/delete")
    public String delete(@Valid @ModelAttribute("location") LocationRequestDto location, BindingResult bindingResult) {
        User user = SecurityContext.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            logBindingResultErrors(DELETE, bindingResult, user.getUsername());
            return "redirect:/";
        }

        locationService.deleteUserLocation(location, user.getId());

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
