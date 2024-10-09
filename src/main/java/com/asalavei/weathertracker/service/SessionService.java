package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.dbaccess.entity.User;
import com.asalavei.weathertracker.dbaccess.entity.Session;
import com.asalavei.weathertracker.dbaccess.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session create(User user) {
        Session session = Session.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        return save(session);
    }

    private Session save(Session session) {
        return sessionRepository.save(session);
    }

    public User getUserById(String id) {
        return sessionRepository.findById(id)
                .map(Session::getUser)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        // TODO: custom exception
    }

    public void invalidate(String id) {
        sessionRepository.updateExpiresAt(id, LocalDateTime.now());
    }

    public boolean isSessionValid(String id) {
        return sessionRepository.findByIdAndExpiresAtAfter(id, LocalDateTime.now()).isPresent();
    }
}
