package com.asalavei.weathertracker.weather.weatherapi;

import com.asalavei.weathertracker.weather.location.LocationRequestDto;
import com.asalavei.weathertracker.weather.location.LocationResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface WeatherService {
    boolean locationExists(LocationRequestDto location);

    List<CurrentWeatherDto> getUserLocationsWeather(Long userId);

    List<LocationResponseDto> fetchLocationDetails(String locationName);

    CurrentWeatherDto fetchWeatherByCoordinates(BigDecimal latitude, BigDecimal longitude);
}
