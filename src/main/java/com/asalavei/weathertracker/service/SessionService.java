package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.exception.NotFoundException;
import com.asalavei.weathertracker.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public Session create(User user) {
        Session session = Session.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(getSessionExpiryTime())
                .build();

        return save(session);
    }

    public User getUserById(String id) {
        return sessionRepository.findById(id)
                .map(Session::getUser)
                .orElseThrow(() -> new NotFoundException("Session with ID '" + id + "' not found"));
    }

    public void extendSession(String id) {
        sessionRepository.updateSessionExpiration(id, getSessionExpiryTime());
    }

    public void invalidate(String id) {
        sessionRepository.updateSessionExpiration(id, LocalDateTime.now());
    }

    public boolean isSessionValid(String id) {
        return sessionRepository.findByIdAndExpiresAtAfter(id, LocalDateTime.now()).isPresent();
    }

    private LocalDateTime getSessionExpiryTime() {
        return LocalDateTime.now().plusMinutes(30);
    }

    private Session save(Session session) {
        return sessionRepository.save(session);
    }
}
