package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.exception.NotFoundException;
import com.asalavei.weathertracker.repository.UserRepository;
import com.asalavei.weathertracker.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.asalavei.weathertracker.util.CredentialsUtil.hashPassword;
import static com.asalavei.weathertracker.util.CredentialsUtil.normalizeUsername;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void register(UserRequestDto userRequestDto) {
        User user = User.builder()
                .username(normalizeUsername(userRequestDto.getUsername()))
                .password(hashPassword(userRequestDto.getPassword()))
                .build();
        userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(normalizeUsername(username))
                .orElseThrow(() -> new NotFoundException("User with username '" + username + "' not found"));
    }
}
