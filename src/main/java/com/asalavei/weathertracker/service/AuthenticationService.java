package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.dbaccess.entity.User;
import com.asalavei.weathertracker.dto.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserService userService;

    @Autowired
    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }


    public User authenticate(UserRequestDto userRequestDto) {
        User user = userService.getByUsername(userRequestDto.getUsername());

        if (user.getPassword().equals(userRequestDto.getPassword())) {
            return user;
        }

        throw new RuntimeException("Password incorrect");
        // TODO: custom exception AuthenticationException
    }
}
