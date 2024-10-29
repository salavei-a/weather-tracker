package com.asalavei.weathertracker.auth;

import com.asalavei.weathertracker.exception.NotFoundException;
import com.asalavei.weathertracker.exception.UserAlreadyExistsException;
import com.asalavei.weathertracker.config.TestConfig;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.asalavei.weathertracker.util.CredentialsUtil.normalizeUsername;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class, UserService.class, UserHibernateRepository.class})
class UserServiceTest {

    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";

    private static SignUpRequestDto signUpRequest;

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
    }

    @BeforeEach
    void cleanDatabase() {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users CASCADE", User.class).executeUpdate();
            tx.commit();
        }
    }

    @Test
    void givenAvailableUsername_whenRegister_thenSaveUser() {
        userService.register(signUpRequest);

        assertEquals(normalizeUsername(USERNAME), userService.getUser(USERNAME).getUsername());
    }

    @Test
    void givenExistingUsername_whenRegister_thenThrowUserAlreadyExistsException() {
        userService.register(signUpRequest);

        Executable act = () -> userService.register(signUpRequest);

        assertThrows(UserAlreadyExistsException.class, act);
    }

    @Test
    void givenUpperCaseUsername_whenRegister_thenSaveLowerCaseUsername() {
        String username = "UPPER_USERNAME";
        SignUpRequestDto upperCaseUsernameSignUpRequest = SignUpRequestDto.builder()
                .username(username)
                .password(USERNAME)
                .build();

        userService.register(upperCaseUsernameSignUpRequest);

        assertEquals(normalizeUsername(username), userService.getUser(username).getUsername());
    }

    @Test
    void givenUserRequest_whenRegister_thenPasswordIsHashedInDatabase() {
        userService.register(signUpRequest);

        User user = userService.getUser(USERNAME);
        assertNotEquals(PASSWORD, user.getPassword());
        assertTrue(BCrypt.checkpw(PASSWORD, user.getPassword()));
    }

    @Test
    void givenExistingUser_whenGetUser_thenReturnUser() {
        userService.register(signUpRequest);

        User user = userService.getUser(USERNAME);

        assertEquals(normalizeUsername(USERNAME), user.getUsername());
    }

    @Test
    void givenNotExistingUser_whenGetUser_thenThrowNotFoundException() {
        Executable act = () -> userService.getUser(USERNAME);

        assertThrows(NotFoundException.class, act);
    }
}