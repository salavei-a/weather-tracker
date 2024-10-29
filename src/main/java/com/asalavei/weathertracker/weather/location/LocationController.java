package com.asalavei.weathertracker.weather.location;

import com.asalavei.weathertracker.auth.User;
import com.asalavei.weathertracker.auth.UserMapper;
import com.asalavei.weathertracker.auth.AuthenticatedUserContext;
import com.asalavei.weathertracker.weather.weatherapi.WeatherService;
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
    public String search(@Valid @ModelAttribute("location") LocationSearchRequestDto locationSearchRequest, BindingResult bindingResult, Model model) {
        User user = AuthenticatedUserContext.getAuthenticatedUser();
        model.addAttribute("user", userMapper.toDto(user));

        if (bindingResult.hasErrors()) {
            logBindingResultErrors(SEARCH, bindingResult, user.getUsername());
            return "locations";
        }

        List<LocationResponseDto> locations = weatherService.fetchLocationDetails(locationSearchRequest.getName());
        model.addAttribute("locations", locations);

        return "locations";
    }

    @PostMapping
    public String add(@Valid @ModelAttribute("location") LocationRequestDto locationRequest, BindingResult bindingResult) {
        User user = AuthenticatedUserContext.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            logBindingResultErrors(ADD, bindingResult, user.getUsername());
            return "redirect:/";
        }

        if (!weatherService.locationExists(locationRequest)) {
            log.warn("Attempt to add non-existent location by user={} with potentially manipulated location details. " +
                     "name={}, latitude={}, longitude={}",
                    user.getUsername(), locationRequest.getName(), locationRequest.getLatitude(), locationRequest.getLongitude());
            return "redirect:/";
        }

        locationService.createUserLocation(locationRequest, user);

        return "redirect:/";
    }

    @DeleteMapping
    public String delete(@Valid @ModelAttribute("location") LocationRequestDto locationRequest, BindingResult bindingResult) {
        User user = AuthenticatedUserContext.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            logBindingResultErrors(DELETE, bindingResult, user.getUsername());
            return "redirect:/";
        }

        locationService.deleteUserLocation(locationRequest, user.getId());

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
