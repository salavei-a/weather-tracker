package com.asalavei.weathertracker.weather.location;

import com.asalavei.weathertracker.auth.User;
import com.asalavei.weathertracker.exception.WeatherServiceException;
import com.asalavei.weathertracker.weather.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final WeatherService weatherService;

    public void createUserLocation(LocationRequestDto locationRequest, User user) {
        Location location = Location.builder()
                .name(locationRequest.getName())
                .user(user)
                .latitude(locationRequest.getLatitude())
                .longitude(locationRequest.getLongitude())
                .build();

        locationRepository.save(location);
    }

    public List<Location> findAllUserLocations(Long userId) {
        return locationRepository.findAllByUser(userId);
    }

    public void deleteUserLocation(LocationRequestDto locationRequest, Long userId) {
        locationRepository.deleteLocationForUser(
                locationRequest.getName(),
                locationRequest.getLatitude(),
                locationRequest.getLongitude(),
                userId);
    }

    public boolean locationExists(LocationRequestDto locationRequest) {
        BigDecimal epsilon = new BigDecimal("0.1");

        try {
            return weatherService.fetchLocationDetails(locationRequest.getName()).stream()
                    .anyMatch(l -> l.getName().equals(locationRequest.getName())
                            && l.getLatitude().subtract(locationRequest.getLatitude()).compareTo(epsilon) < 0
                            && l.getLongitude().subtract(locationRequest.getLongitude()).compareTo(epsilon) < 0);
        } catch (WeatherServiceException e) {
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while checking location with name={}, latitude={}, longitude={}",
                    locationRequest.getName(), locationRequest.getLatitude(), locationRequest.getLongitude(), e);
            return false;
        }
    }
}
