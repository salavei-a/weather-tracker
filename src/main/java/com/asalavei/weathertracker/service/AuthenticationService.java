package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.dto.UserRequestDto;
import com.asalavei.weathertracker.exception.AuthenticationException;
import com.asalavei.weathertracker.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final SessionService sessionService;

    public Session authenticate(UserRequestDto userRequestDto) {
        String username = userRequestDto.getUsername().trim().toLowerCase();

        try {
            User user = userService.loadUserByUsername(username);

            if (isPasswordCorrect(userRequestDto, user)) {
                return sessionService.create(user);
            }
        } catch (NotFoundException e) {
            // Ignored as part of authentication flow
            log.info(e.getMessage());
        }

        log.info("Authentication failed for user: '{}'", username);
        throw new AuthenticationException("Incorrect username or password");
    }

    private boolean isPasswordCorrect(UserRequestDto userRequestDto, User user) {
        return BCrypt.checkpw(userRequestDto.getPassword(), user.getPassword());
    }
}
