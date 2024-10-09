package com.asalavei.weathertracker.dbaccess.repository;

import com.asalavei.weathertracker.dbaccess.entity.Session;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session> {
    Optional<Session> findByIdAndExpiresAtAfter(String id, LocalDateTime expiresAfter);

    Optional<Session> findById(String id);

    void updateExpiresAt(String id, LocalDateTime newExpiresAt);
}
