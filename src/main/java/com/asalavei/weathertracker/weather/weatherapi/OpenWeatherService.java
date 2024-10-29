package com.asalavei.weathertracker.weather.weatherapi;

import com.asalavei.weathertracker.weather.location.LocationRequestDto;
import com.asalavei.weathertracker.weather.location.Location;
import com.asalavei.weathertracker.weather.location.LocationResponseDto;
import com.asalavei.weathertracker.exception.WeatherServiceException;
import com.asalavei.weathertracker.weather.location.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
@Service
public class OpenWeatherService implements WeatherService {

    private static final String CLIENT_ERROR = "Client error occurred";
    private static final String SERVER_ERROR = "Server error occurred";
    private static final String SERVER_ERROR_MESSAGE = "Failed to fetch weather data due to server error";

    private final RestClient restClient;
    private final LocationService locationService;

    @Value("${weather.api.path}")
    private String weatherApiPath;

    @Value("${weather.geocoding.path}")
    private String geocodingApiPath;

    @Value("${weather.units}")
    private String units;

    @Value("${weather.api.key}")
    private String apiKey;

    @Override
    public boolean locationExists(LocationRequestDto location) {
        BigDecimal epsilon = new BigDecimal("0.1");

        try {
            return fetchLocationDetails(location.getName()).stream()
                    .anyMatch(l -> l.getName().equals(location.getName())
                            && l.getLatitude().subtract(location.getLatitude()).compareTo(epsilon) < 0
                            && l.getLongitude().subtract(location.getLongitude()).compareTo(epsilon) < 0);
        } catch (WeatherServiceException e) {
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while checking location with name={}, latitude={}, longitude={}",
                    location.getName(), location.getLatitude(), location.getLongitude(), e);
            return false;
        }
    }

    @Override
    public List<CurrentWeatherDto> getUserLocationsWeather(Long userId) {
        List<Location> userLocations = locationService.findAllUserLocations(userId);

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
                        .path(geocodingApiPath)
                        .queryParam("q", locationName)
                        .queryParam("limit", 5)
                        .queryParam("appid", apiKey)
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
                        .path(weatherApiPath)
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("appid", apiKey)
                        .queryParam("units", units)
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
