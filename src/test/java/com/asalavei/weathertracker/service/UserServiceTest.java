package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.exception.NotFoundException;
import com.asalavei.weathertracker.exception.UserAlreadyExistsException;
import com.asalavei.weathertracker.repository.UserHibernateRepository;
import com.asalavei.weathertracker.config.TestConfig;
import com.asalavei.weathertracker.dto.UserRequestDto;
import com.asalavei.weathertracker.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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

    @Autowired
    private UserService userService;

    @Autowired
    private SessionFactory sessionFactory;

    @BeforeEach
    void cleanDatabase() {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users CASCADE", User.class).executeUpdate();
            tx.commit();
        }
    }

    @Test
    void givenAvailableUsername_whenRegister_thenSuccessfullySaveUser() {
        // Arrange
        String username = "test";
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username(username)
                .password("test")
                .build();

        // Act
        userService.register(userRequestDto);

        // Assert
        assertEquals(username, userService.getUser(username).getUsername());
    }

    @Test
    void givenExistingUsername_whenRegister_thenThrowUserAlreadyExistsException() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("existingUser")
                .password("test")
                .build();
        userService.register(userRequestDto);

        Executable act = () -> userService.register(userRequestDto);

        assertThrows(UserAlreadyExistsException.class, act);
    }

    @Test
    void givenUpperCaseUsername_whenRegister_thenSaveLowerCaseUsername() {
        String username = "UPPER_USERNAME";
        UserRequestDto user = UserRequestDto.builder()
                .username(username)
                .password("test")
                .build();

        userService.register(user);

        assertEquals(normalizeUsername(username), userService.getUser(username).getUsername());
    }

    @Test
    void givenUserRequest_whenRegister_thenPasswordIsHashedInDatabase() {
        String rawPassword = "test";
        String username = "test";
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username(username)
                .password(rawPassword)
                .build();

        userService.register(userRequestDto);

        User savedUser = userService.getUser(username);
        assertNotEquals(rawPassword, savedUser.getPassword());
        assertTrue(BCrypt.checkpw(rawPassword, savedUser.getPassword()));
    }

    @Test
    void givenExistingUser_whenGetUser_thenReturnUser() {
        String username = "ExistentUser";
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username(username)
                .password("test")
                .build();
        userService.register(userRequestDto);

        User savedUser = userService.getUser(username);

        assertEquals(normalizeUsername(username), savedUser.getUsername());
    }

    @Test
    void givenNotExistingUser_whenGetUser_thenThrowNotFoundException() {
        String username = "NotExistentUser";

        Executable act = () -> userService.getUser(username);

        assertThrows(NotFoundException.class, act);
    }
}