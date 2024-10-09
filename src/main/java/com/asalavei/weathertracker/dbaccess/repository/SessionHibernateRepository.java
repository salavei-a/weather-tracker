package com.asalavei.weathertracker.dbaccess.repository;

import com.asalavei.weathertracker.dbaccess.entity.Session;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class SessionHibernateRepository extends BaseHibernateRepository<Session> implements SessionRepository {

    @Override
    public Optional<Session> findByIdAndExpiresAtAfter(String id, LocalDateTime expiresAfter) {
        return executeInTransaction(s -> {
            Session session = s.createQuery("from Session where id = :id and expiresAt > :expiresAfter", Session.class)
                    .setParameter("id", id)
                    .setParameter("expiresAfter", expiresAfter)
                    .uniqueResult();
            return Optional.ofNullable(session);
        });
    }

    @Override
    public Optional<Session> findById(String id) {
        return executeInTransaction(s -> {
            Session session = s.createQuery("from Session where id = :id", Session.class)
                    .setParameter("id", id)
                    .uniqueResult();
            return Optional.ofNullable(session);
        });
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
