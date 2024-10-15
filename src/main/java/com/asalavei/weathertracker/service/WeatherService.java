package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.dto.CurrentWeatherDto;
import com.asalavei.weathertracker.dto.LocationResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface WeatherService {
    List<CurrentWeatherDto> getUserLocationsWeather(Long userId);

    List<LocationResponseDto> fetchLocationDetails(String locationName);

    CurrentWeatherDto fetchWeatherByCoordinates(BigDecimal latitude, BigDecimal longitude);

    boolean isWeatherDataAvailable(BigDecimal latitude, BigDecimal longitude);
}
