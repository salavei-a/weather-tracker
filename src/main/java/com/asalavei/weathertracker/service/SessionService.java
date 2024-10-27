package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Value("${weather-tracker.session-max-age}")
    private int sessionMaxAge;

    public Session create(User user) {
        Session session = Session.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(getSessionExpiryTime())
                .build();

        return sessionRepository.save(session);
    }

    public Optional<Session> getValidSession(String id) {
        return sessionRepository.findActiveById(id);
    }

    public void extendSession(String id) {
        sessionRepository.updateSessionExpiration(id, getSessionExpiryTime());
    }

    public void invalidate(String id) {
        sessionRepository.updateSessionExpiration(id, LocalDateTime.now());
    }

    private LocalDateTime getSessionExpiryTime() {
        return LocalDateTime.now().plusSeconds(sessionMaxAge);
    }
}
