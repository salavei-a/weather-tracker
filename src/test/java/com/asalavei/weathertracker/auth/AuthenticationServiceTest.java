package com.asalavei.weathertracker.auth;

import com.asalavei.weathertracker.auth.session.Session;
import com.asalavei.weathertracker.auth.session.SessionHibernateRepository;
import com.asalavei.weathertracker.auth.session.SessionService;
import com.asalavei.weathertracker.auth.user.User;
import com.asalavei.weathertracker.auth.user.UserHibernateRepository;
import com.asalavei.weathertracker.auth.user.UserService;
import com.asalavei.weathertracker.config.TestConfig;
import com.asalavei.weathertracker.exception.AuthenticationException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.asalavei.weathertracker.util.CredentialsUtil.normalizeUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"weather-tracker.session-max-age=1800", "weather-tracker.session-near-expiration-offset=300"})
@ContextConfiguration(classes = {TestConfig.class, AuthenticationService.class, UserService.class, SessionService.class,
        UserHibernateRepository.class, SessionHibernateRepository.class})
class AuthenticationServiceTest {

    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";

    private static SignUpRequestDto signUpRequest;
    private static SignInRequestDto signInRequest;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionFactory sessionFactory;

    @BeforeAll
    static void setUp() {
        signUpRequest = SignUpRequestDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .matchingPassword(PASSWORD)
                .build();
        signInRequest = SignInRequestDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
    }

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
        userService.register(signUpRequest);

        // Act
        Session session = authenticationService.authenticate(signInRequest);

        // Assert
        assertEquals(normalizeUsername(USERNAME), session.getUser().getUsername());
    }

    @Test
    void givenNonExistingUsername_whenAuthenticate_thenThrowAuthenticationException() {
        String nonExistingUsername = "nonExistingUsername";
        SignInRequestDto nonExistingUserSignInRequest = SignInRequestDto.builder()
                .username(nonExistingUsername)
                .password(PASSWORD)
                .build();

        Executable act = () -> authenticationService.authenticate(nonExistingUserSignInRequest);

        assertThrows(AuthenticationException.class, act);
    }

    @Test
    void givenWrongPassword_whenAuthenticate_thenThrowAuthenticationException() {
        String wrongPassword = "WrongPassword";
        SignInRequestDto wrongPasswordSignInRequest = SignInRequestDto.builder()
                .username(USERNAME)
                .password(wrongPassword)
                .build();

        Executable act = () -> authenticationService.authenticate(wrongPasswordSignInRequest);

        assertThrows(AuthenticationException.class, act);
    }

    @Test
    void givenCorrectCredentials_whenAuthenticateTwice_thenTwoUniqueSessionsCreated() {
        userService.register(signUpRequest);

        Session firstSession = authenticationService.authenticate(signInRequest);
        Session secondSession = authenticationService.authenticate(signInRequest);

        assertEquals(firstSession.getUser().getUsername(), secondSession.getUser().getUsername());
        assertNotEquals(firstSession.getId(), secondSession.getId());
    }
}