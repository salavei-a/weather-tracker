package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.dbaccess.entity.Location;
import com.asalavei.weathertracker.dto.CurrentWeatherDto;
import com.asalavei.weathertracker.dto.LocationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class OpenWeatherService implements WeatherService {

    private static final String API_KEY = System.getenv("OPEN_WEATHER_API_KEY");
    private static final String WEATHER_API_PATH = "/data/2.5/weather";
    private static final String DIRECT_GEOCODING_API_PATH = "/geo/1.0/direct";
    public static final String UNITS_OF_MEASUREMENT = "metric";

    private final RestClient restClient;
    private final LocationService locationService;

    @Autowired
    public OpenWeatherService(RestClient restClient, LocationService locationService) {
        this.restClient = restClient;
        this.locationService = locationService;
    }

    public List<CurrentWeatherDto> getUserLocationsWeather(Long userId) {
        List<Location> userLocations = locationService.findAllByUserId(userId);

        return userLocations.stream()
                .map(location -> {
                    CurrentWeatherDto currentWeather = fetchWeatherByCoordinates(location.getLatitude(), location.getLongitude());
                    currentWeather.setName(location.getName());
                    return currentWeather;
                })
                .map(this::roundTemperatures)
                .toList();
    }

    public List<LocationResponseDto> fetchLocationDetails(String locationName) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DIRECT_GEOCODING_API_PATH)
                        .queryParam("q", locationName)
                        .queryParam("limit", 5)
                        .queryParam("appid", API_KEY)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public CurrentWeatherDto fetchWeatherByCoordinates(BigDecimal latitude, BigDecimal longitude) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(WEATHER_API_PATH)
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("appid", API_KEY)
                        .queryParam("units", UNITS_OF_MEASUREMENT)
                        .build())
                .retrieve()
                .body(CurrentWeatherDto.class);
    }

    private CurrentWeatherDto roundTemperatures(CurrentWeatherDto currentWeather) {
        CurrentWeatherDto.TemperatureInfo temperatureInfo = currentWeather.getTemperatureInfo();

        if (temperatureInfo != null) {
            temperatureInfo.setTemperature(
                    round(temperatureInfo.getTemperature())
                            .orElseGet(temperatureInfo::getTemperature)
            );
            temperatureInfo.setFeelsLike(
                    round(temperatureInfo.getFeelsLike())
                            .orElseGet(temperatureInfo::getFeelsLike)
            );
        }

        return currentWeather;
    }

    private Optional<BigDecimal> round(BigDecimal value) {
        return Optional.ofNullable(value)
                .map(v -> v.setScale(0, RoundingMode.HALF_UP));
    }
}
