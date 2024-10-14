package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.entity.Session;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class SessionHibernateRepository extends BaseHibernateRepository<Session> implements SessionRepository {

    @Override
    public Optional<Session> findByIdAndExpiresAtAfter(String id, LocalDateTime expiresAfter) {
        return executeInTransaction(s ->
                Optional.ofNullable(s.createQuery("from Session where id = :id and expiresAt > :expiresAfter", Session.class)
                        .setParameter("id", id)
                        .setParameter("expiresAfter", expiresAfter)
                        .uniqueResult()));
    }

    @Override
    public Optional<Session> findById(String id) {
        return executeInTransaction(s ->
                Optional.ofNullable(s.createQuery("from Session where id = :id", Session.class)
                        .setParameter("id", id)
                        .uniqueResult()));
    }

    @Override
    public void updateExpiresAt(String id, LocalDateTime newExpiresAt) {
        executeInTransaction(s ->
                s.createQuery("update Session set expiresAt = :newExpiresAt where id = :sessionId")
                        .setParameter("sessionId", id)
                        .setParameter("newExpiresAt", newExpiresAt)
                        .executeUpdate()
        );
    }
}
