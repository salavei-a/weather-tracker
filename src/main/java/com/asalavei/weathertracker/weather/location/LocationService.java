package com.asalavei.weathertracker.weather.location;

import com.asalavei.weathertracker.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LocationService {

    private final LocationRepository locationRepository;

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
}
