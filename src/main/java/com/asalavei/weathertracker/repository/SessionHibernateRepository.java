package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.entity.Session;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class SessionHibernateRepository extends BaseHibernateRepository<Session> implements SessionRepository {

    @Override
    public Optional<Session> findActiveById(String id) {
        return executeInTransaction(s ->
                Optional.ofNullable(s.createQuery("from Session s join fetch s.user where s.id = :id and s.expiresAt > :current", Session.class)
                        .setParameter("id", id)
                        .setParameter("current", LocalDateTime.now())
                        .uniqueResult()));
    }

    @Override
    public void updateSessionExpiration(String id, LocalDateTime newExpiresAt) {
        executeInTransaction(s ->
                s.createQuery("update Session set expiresAt = :newExpiresAt where id = :sessionId")
                        .setParameter("sessionId", id)
                        .setParameter("newExpiresAt", newExpiresAt)
                        .executeUpdate()
        );
    }
}
