package com.asalavei.weathertracker.weather.location;

import com.asalavei.weathertracker.common.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends CrudRepository<Location> {
    Optional<Location> findByUserAndCoordinates(Long userId, BigDecimal latitude, BigDecimal longitude);

    List<Location> findAllByUser(Long userId);

    void delete(Location location);
}
