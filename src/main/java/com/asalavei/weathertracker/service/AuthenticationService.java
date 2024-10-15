package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.dto.UserRequestDto;
import com.asalavei.weathertracker.exception.AuthenticationException;
import com.asalavei.weathertracker.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {

    private final UserService userService;

    @Autowired
    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    public User authenticate(UserRequestDto userRequestDto) {
        try {
            User user = userService.getByUsername(userRequestDto.getUsername());

            if (user.getPassword().equals(userRequestDto.getPassword())) {
                return user;
            }
        } catch (NotFoundException ignored) {
            // Ignored as part of authentication flow
        }

        throw new AuthenticationException("Invalid username or password");
    }
}
