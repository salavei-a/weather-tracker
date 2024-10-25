package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.config.TestConfig;
import com.asalavei.weathertracker.dto.UserRequestDto;
import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.exception.AuthenticationException;
import com.asalavei.weathertracker.repository.SessionHibernateRepository;
import com.asalavei.weathertracker.repository.UserHibernateRepository;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.asalavei.weathertracker.util.CredentialsUtil.normalizeUsername;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class, AuthenticationService.class, UserService.class, SessionService.class,
        UserHibernateRepository.class, SessionHibernateRepository.class})
class AuthenticationServiceTest {

    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionFactory sessionFactory;

    @BeforeEach
    void cleanDatabase() {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users CASCADE", User.class).executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE sessions CASCADE", Session.class).executeUpdate();
            tx.commit();
        }
    }

    @Test
    void givenCorrectCredentials_whenAuthenticate_thenReturnUserSession() {
        // Arrange
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
        userService.register(userRequestDto);

        // Act
        Session session = authenticationService.authenticate(userRequestDto);

        // Assert
        assertEquals(normalizeUsername(USERNAME), session.getUser().getUsername());
    }

    @Test
    void givenNonExistingUsername_whenAuthenticate_thenThrowAuthenticationException() {
        String nonExistingUsername = "nonExistingUsername";
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username(nonExistingUsername)
                .password(PASSWORD)
                .build();

        Executable act = () -> authenticationService.authenticate(userRequestDto);

        assertThrows(AuthenticationException.class, act);
    }

    @Test
    void givenWrongPassword_whenAuthenticate_thenThrowAuthenticationException() {
        String wrongPassword = "WrongPassword";
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .password(wrongPassword)
                .build();

        Executable act = () -> authenticationService.authenticate(userRequestDto);

        assertThrows(AuthenticationException.class, act);
    }

    @Test
    void givenCorrectCredentials_whenAuthenticateTwice_thenTwoUniqueSessionsCreated() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
        userService.register(userRequestDto);

        Session firstSession = authenticationService.authenticate(userRequestDto);
        Session secondSession = authenticationService.authenticate(userRequestDto);

        assertEquals(firstSession.getUser().getUsername(), secondSession.getUser().getUsername());
        assertNotEquals(firstSession.getId(), secondSession.getId());
    }
}