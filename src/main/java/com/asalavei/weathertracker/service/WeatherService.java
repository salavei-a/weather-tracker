package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.dto.LocationResponseDto;

import java.util.List;

public interface WeatherService {
    List<LocationResponseDto> getLocationDetails(String locationName);
}
