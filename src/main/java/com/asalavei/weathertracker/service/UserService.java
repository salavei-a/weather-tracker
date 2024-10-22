package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.exception.NotFoundException;
import com.asalavei.weathertracker.repository.UserRepository;
import com.asalavei.weathertracker.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(UserRequestDto userRequest) {
        User user = User.builder()
                .username(userRequest.getUsername().trim().toLowerCase())
                .password(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()))
                .build();
        return save(user);
    }

    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username '" + username + "' not found"));
    }

    private User save(User user) {
        return userRepository.save(user);
    }
}
