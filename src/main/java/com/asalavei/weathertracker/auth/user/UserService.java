package com.asalavei.weathertracker.auth.user;

import com.asalavei.weathertracker.auth.SignUpRequestDto;
import com.asalavei.weathertracker.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.asalavei.weathertracker.util.CredentialsUtil.hashPassword;
import static com.asalavei.weathertracker.util.CredentialsUtil.normalizeUsername;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void register(SignUpRequestDto signUpRequest) {
        User user = User.builder()
                .username(normalizeUsername(signUpRequest.getUsername()))
                .password(hashPassword(signUpRequest.getPassword()))
                .build();
        userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(normalizeUsername(username))
                .orElseThrow(() -> new NotFoundException("User with username '" + username + "' not found"));
    }
}
