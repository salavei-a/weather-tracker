package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.dbaccess.entity.User;
import com.asalavei.weathertracker.dbaccess.repository.UserRepository;
import com.asalavei.weathertracker.mapper.UserMapper;
import com.asalavei.weathertracker.dto.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public User create(UserRequestDto userRequest) {
        return save(userMapper.toEntity(userRequest));
    }

    private User save(User user) {
        return userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // TODO: throw ResourceNotFoundException
    }
}
