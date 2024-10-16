package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.entity.Location;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class LocationHibernateRepository extends BaseHibernateRepository<Location> implements LocationRepository {

    @Override
    public List<Location> findAllByUserId(Long userId) {
        return executeInTransaction(s ->
                s.createQuery("from Location where user.id = :userId", Location.class)
                        .setParameter("userId", userId)
                        .getResultList()
        );
    }

    @Override
    public void deleteByNameAndLatitudeAndLongitudeAndUserId(String name, BigDecimal latitude, BigDecimal longitude, Long userId) {
        executeInTransaction(s ->
                s.createQuery("delete from Location where name = :name " +
                              "and latitude = :latitude " +
                              "and longitude = :longitude " +
                              "and user.id = :userId")
                        .setParameter("name", name)
                        .setParameter("latitude", latitude)
                        .setParameter("longitude", longitude)
                        .setParameter("userId", userId)
                        .executeUpdate());
    }
}
