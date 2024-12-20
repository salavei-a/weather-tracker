package com.asalavei.weathertracker.weather.location;

import com.asalavei.weathertracker.common.CrudRepository;

import java.math.BigDecimal;
import java.util.List;

public interface LocationRepository extends CrudRepository<Location> {
    List<Location> findAllByUser(Long userId);

    void deleteLocationForUser(Long userId, BigDecimal latitude, BigDecimal longitude);
}
