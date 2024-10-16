package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.Location;
import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.repository.LocationRepository;
import com.asalavei.weathertracker.dto.LocationRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location create(LocationRequestDto locationRequest, User user) {
        Location location = Location.builder()
                .name(locationRequest.getName())
                .user(user)
                .latitude(locationRequest.getLatitude())
                .longitude(locationRequest.getLongitude())
                .build();

        return save(location);
    }

    private Location save(Location location) {
        return locationRepository.save(location);
    }

    public List<Location> findAllByUserId(Long userId) {
        return locationRepository.findAllByUserId(userId);
    }

    public void delete(LocationRequestDto locationRequest, Long userId) {
        locationRepository.deleteByNameAndLatitudeAndLongitudeAndUserId(
                locationRequest.getName(),
                locationRequest.getLatitude(),
                locationRequest.getLongitude(),
                userId);
    }
}
