package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.entity.Session;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session> {
    Optional<Session> findByIdAndExpiresAtAfter(String id, LocalDateTime expiresAfter);

    Optional<Session> findById(String id);

    void updateSessionExpiration(String id, LocalDateTime newExpiresAt);
}
