package com.asalavei.weathertracker.weather.openweather;

import com.asalavei.weathertracker.weather.WeatherService;
import com.asalavei.weathertracker.weather.location.LocationResponseDto;
import com.asalavei.weathertracker.exception.WeatherServiceException;
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
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenWeatherService implements WeatherService {

    private static final String CLIENT_ERROR = "Client error occurred";
    private static final String SERVER_ERROR = "Server error occurred";
    private static final String SERVER_ERROR_MESSAGE = "Failed to fetch weather data due to server error";

    private final RestClient restClient;

    @Value("${open-weather.api.path}")
    private String weatherApiPath;

    @Value("${open-weather.geocoding.path}")
    private String geocodingApiPath;

    @Value("${open-weather.units}")
    private String units;

    @Value("${open-weather.api.key}")
    private String apiKey;

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
}
