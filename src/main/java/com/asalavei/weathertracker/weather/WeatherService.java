package com.asalavei.weathertracker.weather;

import com.asalavei.weathertracker.weather.location.LocationResponseDto;
import com.asalavei.weathertracker.weather.openweather.CurrentWeatherDto;

import java.math.BigDecimal;
import java.util.List;

public interface WeatherService {
    List<LocationResponseDto> fetchLocationDetails(String locationName);

    CurrentWeatherDto fetchWeatherByCoordinates(BigDecimal latitude, BigDecimal longitude);
}
