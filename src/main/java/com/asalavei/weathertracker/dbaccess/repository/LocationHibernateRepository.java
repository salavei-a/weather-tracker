package com.asalavei.weathertracker.dbaccess.repository;

import com.asalavei.weathertracker.dbaccess.entity.Location;
import org.springframework.stereotype.Repository;

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
}
