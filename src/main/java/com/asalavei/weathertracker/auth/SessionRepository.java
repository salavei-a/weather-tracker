package com.asalavei.weathertracker.auth;

import com.asalavei.weathertracker.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session> {
    Optional<Session> findActive(String id);

    void updateSessionExpiration(String id, LocalDateTime newExpiresAt);
}