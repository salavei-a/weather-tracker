package com.asalavei.weathertracker.weather.location;

import com.asalavei.weathertracker.auth.user.User;
import com.asalavei.weathertracker.auth.user.UserMapper;
import com.asalavei.weathertracker.auth.AuthenticatedUserThreadLocal;
import com.asalavei.weathertracker.weather.WeatherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.asalavei.weathertracker.common.Constants.*;

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
    public String search(@Valid @ModelAttribute(LOCATION_ATTRIBUTE) LocationSearchRequestDto locationSearchRequest, BindingResult bindingResult, Model model) {
        User user = AuthenticatedUserThreadLocal.getAuthenticatedUser();
        model.addAttribute(USER_ATTRIBUTE, userMapper.toDto(user));

        if (bindingResult.hasErrors()) {
            logBindingResultErrors(SEARCH, bindingResult, user.getUsername());
            return LOCATIONS_VIEW;
        }

        model.addAttribute(LOCATIONS_ATTRIBUTE, weatherService.fetchLocationDetails(locationSearchRequest.getName()));

        return LOCATIONS_VIEW;
    }

    @PostMapping
    public String add(@Valid @ModelAttribute(LOCATION_ATTRIBUTE) LocationRequestDto locationRequest, BindingResult bindingResult) {
        User user = AuthenticatedUserThreadLocal.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            logBindingResultErrors(ADD, bindingResult, user.getUsername());
            return REDIRECT_HOME;
        }

        if (!locationService.locationExists(locationRequest)) {
            log.warn("Attempt to add non-existent location by user={} with potentially manipulated location details. " +
                     "name={}, latitude={}, longitude={}",
                    user.getUsername(), locationRequest.getName(), locationRequest.getLatitude(), locationRequest.getLongitude());
            return REDIRECT_HOME;
        }

        locationService.saveUserLocation(locationRequest, user);

        return REDIRECT_HOME;
    }

    @DeleteMapping
    public String delete(@Valid @ModelAttribute(LOCATION_ATTRIBUTE) LocationRequestDto locationRequest, BindingResult bindingResult) {
        User user = AuthenticatedUserThreadLocal.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            logBindingResultErrors(DELETE, bindingResult, user.getUsername());
            return REDIRECT_HOME;
        }

        locationService.deleteUserLocation(locationRequest, user.getId());

        return REDIRECT_HOME;
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
