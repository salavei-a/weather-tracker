package com.asalavei.weathertracker.weather.location;

import com.asalavei.weathertracker.common.BaseHibernateRepository;
import com.asalavei.weathertracker.exception.AlreadyExistsException;
import com.asalavei.weathertracker.exception.LocationAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Repository
public class LocationHibernateRepository extends BaseHibernateRepository<Location> implements LocationRepository {

    public LocationHibernateRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Location save(Location location) {
        try {
            return super.save(location);
        } catch (AlreadyExistsException e) {
            log.debug("Location with name={}, latitude={}, longitude={} already exists for user={}",
                    location.getName(), location.getLatitude(), location.getLongitude(), location.getUser().getUsername(), e);
            throw new LocationAlreadyExistsException("Location has already been added");
        }
    }

    @Override
    public List<Location> findAllByUser(Long userId) {
        return executeInTransaction(s ->
                s.createQuery("from Location where user.id = :userId order by id asc", Location.class)
                        .setParameter("userId", userId)
                        .getResultList()
        );
    }

    @Override
    public void deleteLocationForUser(Long userId, BigDecimal latitude, BigDecimal longitude) {
        executeInTransaction(s ->
                s.createQuery("delete from Location where user.id = :userId " +
                              "and latitude = :latitude " +
                              "and longitude = :longitude")
                        .setParameter("userId", userId)
                        .setParameter("latitude", latitude)
                        .setParameter("longitude", longitude)
                        .executeUpdate());
    }
}
