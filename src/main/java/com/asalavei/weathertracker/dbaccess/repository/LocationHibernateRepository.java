package com.asalavei.weathertracker.dbaccess.repository;

import com.asalavei.weathertracker.dbaccess.entity.Location;
import org.springframework.stereotype.Repository;

@Repository
public class LocationHibernateRepository extends BaseHibernateRepository<Location> implements LocationRepository {
}
