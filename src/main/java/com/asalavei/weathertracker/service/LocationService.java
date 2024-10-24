package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.Location;
import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.repository.LocationRepository;
import com.asalavei.weathertracker.dto.LocationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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

    public List<Location> findAllByUserId(Long userId) {
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
