package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.entity.Session;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session> {
    Optional<Session> findActive(String id);

    void updateSessionExpiration(String id, LocalDateTime newExpiresAt);
}
