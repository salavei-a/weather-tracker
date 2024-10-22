package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.entity.Location;

import java.math.BigDecimal;
import java.util.List;

public interface LocationRepository extends CrudRepository<Location> {
    List<Location> findAllByUser(Long userId);

    void deleteLocationForUser(String name, BigDecimal latitude, BigDecimal longitude, Long userId);
}
