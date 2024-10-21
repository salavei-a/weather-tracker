package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.dto.CurrentWeatherDto;
import com.asalavei.weathertracker.dto.LocationRequestDto;
import com.asalavei.weathertracker.dto.LocationResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface WeatherService {
    boolean locationExists(LocationRequestDto location);

    List<CurrentWeatherDto> getUserLocationsWeather(Long userId);

    List<LocationResponseDto> fetchLocationDetails(String locationName);

    CurrentWeatherDto fetchWeatherByCoordinates(BigDecimal latitude, BigDecimal longitude);
}
