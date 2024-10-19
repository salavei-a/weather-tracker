package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.dto.UserRequestDto;
import com.asalavei.weathertracker.exception.AuthenticationException;
import com.asalavei.weathertracker.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;

    public User authenticate(UserRequestDto userRequestDto) {
        String username = userRequestDto.getUsername();

        try {
            User user = userService.getByUsername(username);

            if (user.getPassword().equals(userRequestDto.getPassword())) {
                return user;
            }
        } catch (NotFoundException e) {
            // Ignored as part of authentication flow
            log.info(e.getMessage());
        }

        log.info("Authentication failed for user: '{}'", username);
        throw new AuthenticationException("Incorrect username or password");
    }
}
