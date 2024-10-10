package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.dto.LocationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OpenWeatherService implements WeatherService {

    private static final String API_KEY = System.getenv("WEATHER_API_KEY");
    private static final String BASE_URL = "https://api.openweathermap.org";
    private static final String WEATHER_API_PATH = BASE_URL + "/data/2.5/weather";
    private static final String DIRECT_GEOCODING_API_PATH = BASE_URL + "/geo/1.0/direct";

    private final RestClient restClient;

    @Autowired
    public OpenWeatherService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<LocationResponseDto> getLocationDetails(String locationName) {
        String url = String.format("%s?q=%s&limit=5&appid=%s", DIRECT_GEOCODING_API_PATH, locationName, API_KEY);

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
