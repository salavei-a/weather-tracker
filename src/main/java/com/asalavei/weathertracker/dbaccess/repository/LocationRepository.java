package com.asalavei.weathertracker.dbaccess.repository;

import com.asalavei.weathertracker.dbaccess.entity.Location;

import java.util.List;

public interface LocationRepository extends CrudRepository<Location> {
    List<Location> findAllByUserId(Long userId);

    void deleteByNameAndUserId(String name, Long userId);
}
