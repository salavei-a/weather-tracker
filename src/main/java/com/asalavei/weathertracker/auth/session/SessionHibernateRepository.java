package com.asalavei.weathertracker.auth.session;

import com.asalavei.weathertracker.common.BaseHibernateRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class SessionHibernateRepository extends BaseHibernateRepository<Session> implements SessionRepository {

    public SessionHibernateRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Optional<Session> findActive(String id) {
        return executeInTransaction(s ->
                s.createQuery("from Session s join fetch s.user where s.id = :id and s.expiresAt > :current", Session.class)
                        .setParameter("id", id)
                        .setParameter("current", LocalDateTime.now())
                        .uniqueResultOptional());
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
