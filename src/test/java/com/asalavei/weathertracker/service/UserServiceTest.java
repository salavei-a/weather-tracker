package com.asalavei.weathertracker.service;

import com.asalavei.weathertracker.entity.Location;
import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.exception.NotFoundException;
import com.asalavei.weathertracker.exception.UserAlreadyExistsException;
import com.asalavei.weathertracker.repository.UserHibernateRepository;
import com.asalavei.weathertracker.service.UserServiceTest.TestConfig;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class, UserService.class, UserHibernateRepository.class})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionFactory sessionFactory;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("db/migration/V1__test_init.sql");

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

        assertEquals(username.toLowerCase(), userService.getUser(username).getUsername());
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

        assertEquals(username.toLowerCase(), savedUser.getUsername());
    }

    @Test
    void givenNotExistingUser_whenGetUser_thenThrowNotFoundException() {
        String username = "NotExistentUser";

        Executable act = () -> userService.getUser(username);

        assertThrows(NotFoundException.class, act);
    }

    @Configuration
    public static class TestConfig {

        @Bean
        public SessionFactory sessionFactory() {
            org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Location.class);
            configuration.addAnnotatedClass(Session.class);

            configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            configuration.setProperty("hibernate.connection.url", postgreSQLContainer.getJdbcUrl());
            configuration.setProperty("hibernate.connection.username", postgreSQLContainer.getUsername());
            configuration.setProperty("hibernate.connection.password", postgreSQLContainer.getPassword());
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            configuration.setProperty("hibernate.show_sql", true);
            configuration.setProperty("hibernate.current_session_context_class", "thread");

            return configuration.buildSessionFactory();
        }
    }
}