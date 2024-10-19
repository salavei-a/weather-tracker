package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.Location;
import com.asalavei.weathertracker.dto.CurrentWeatherDto;
import com.asalavei.weathertracker.dto.LocationResponseDto;
import com.asalavei.weathertracker.exception.WeatherServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenWeatherService implements WeatherService {

    private static final String API_KEY = System.getenv("OPEN_WEATHER_API_KEY");
    private static final String WEATHER_API_PATH = "/data/2.5/weather";
    private static final String DIRECT_GEOCODING_API_PATH = "/geo/1.0/direct";
    private static final String UNITS_OF_MEASUREMENT = "metric";

    private static final String CLIENT_ERROR = "Client error occurred";
    private static final String SERVER_ERROR = "Server error occurred";
    private static final String SERVER_ERROR_MESSAGE = "Failed to fetch weather data due to server error";

    private final RestClient restClient;
    private final LocationService locationService;

    @Override
    public boolean isWeatherDataAvailable(BigDecimal latitude, BigDecimal longitude) {
        try {
            fetchWeatherByCoordinates(latitude, longitude);
        } catch (WeatherServiceException e) {
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while checking location. latitude: {}, longitude: {}.", latitude, longitude, e);
            return false;
        }

        return true;
    }

    @Override
    public List<CurrentWeatherDto> getUserLocationsWeather(Long userId) {
        List<Location> userLocations = locationService.findAllByUserId(userId);

        return userLocations.stream()
                .map(location -> {
                    BigDecimal latitude = location.getLatitude();
                    BigDecimal longitude = location.getLongitude();

                    CurrentWeatherDto currentWeather = fetchWeatherByCoordinates(latitude, longitude);
                    currentWeather.setName(location.getName());
                    currentWeather.getLocationInfo().setLatitude(latitude);
                    currentWeather.getLocationInfo().setLongitude(longitude);
                    return currentWeather;
                })
                .map(this::roundTemperatures)
                .toList();
    }

    @Override
    public List<LocationResponseDto> fetchLocationDetails(String locationName) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DIRECT_GEOCODING_API_PATH)
                        .queryParam("q", locationName)
                        .queryParam("limit", 5)
                        .queryParam("appid", API_KEY)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    logRequest(req);
                    logResponseBody(CLIENT_ERROR, res);
                    throw new WeatherServiceException("No location details available for the given location name");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    logRequest(req);
                    logResponseBody(SERVER_ERROR, res);
                    throw new WeatherServiceException(SERVER_ERROR_MESSAGE);
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
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
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    logRequest(req);
                    logResponseBody(CLIENT_ERROR, res);
                    throw new WeatherServiceException("Weather data not available for the given coordinates");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    logRequest(req);
                    logResponseBody(SERVER_ERROR, res);
                    throw new WeatherServiceException(SERVER_ERROR_MESSAGE);
                })
                .body(CurrentWeatherDto.class);
    }

    private void logRequest(HttpRequest request) {
        log.error("Request failed. Method: {}, URI: {}", request.getMethod(), request.getURI());
    }

    private void logResponseBody(String errorType, ClientHttpResponse res) throws IOException {
        String responseBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
        log.error("{}. Status code: {}, Response body: {}", errorType, res.getStatusCode(), responseBody);
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
