package com.asalavei.weathertracker.weather;

import com.asalavei.weathertracker.weather.location.Location;
import com.asalavei.weathertracker.weather.location.LocationService;
import com.asalavei.weathertracker.weather.openweather.CurrentWeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserWeatherService {

    private final WeatherService weatherService;
    private final LocationService locationService;

    public List<CurrentWeatherDto> getUserLocationsWeather(Long userId) {
        List<Location> userLocations = locationService.findAllUserLocations(userId);

        return userLocations.stream()
                .map(location -> {
                    BigDecimal latitude = location.getLatitude();
                    BigDecimal longitude = location.getLongitude();

                    CurrentWeatherDto currentWeather = weatherService.fetchWeatherByCoordinates(latitude, longitude);
                    currentWeather.setName(location.getName());
                    currentWeather.getLocationInfo().setLatitude(latitude);
                    currentWeather.getLocationInfo().setLongitude(longitude);
                    return currentWeather;
                })
                .map(this::roundTemperatures)
                .toList();
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
