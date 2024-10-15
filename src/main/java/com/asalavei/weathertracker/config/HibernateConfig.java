package com.asalavei.weathertracker.config;

import com.asalavei.weathertracker.entity.Location;
import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.exception.AppRuntimeException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class HibernateConfig {

    private static final Properties properties = configureProperties();
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static final HikariDataSource hikariDataSource = buildDataSource();

    private static final String HIBERNATE_PROPERTIES_FILE = "hibernate.properties";
    private static final String DB_URL = "jdbc:postgresql://localhost:4681/weather_tracker";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

    private HibernateConfig() {
    }

    private static Properties configureProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = HibernateConfig.class.getClassLoader().getResourceAsStream(HIBERNATE_PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new AppRuntimeException(String.format("Configuration file '%s' not found in the classpath", HIBERNATE_PROPERTIES_FILE));
            }

            properties.load(inputStream);
        } catch (IOException e) {
            throw new AppRuntimeException(String.format("Failed to load '%s'", HIBERNATE_PROPERTIES_FILE), e);
        }

        properties.setProperty("hibernate.connection.url", getEnvOrDefault("POSTGRES_URL", DB_URL));
        properties.setProperty("hibernate.connection.username", getEnvOrDefault("POSTGRES_USER", DB_USER));
        properties.setProperty("hibernate.connection.password", getEnvOrDefault("POSTGRES_PASSWORD", DB_PASSWORD));

        return properties;
    }

    private static String getEnvOrDefault(String envKey, String defaultValue) {
        return Optional.ofNullable(System.getenv(envKey)).orElse(defaultValue);
    }

    private static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.setProperties(properties);

        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Location.class);
        configuration.addAnnotatedClass(Session.class);

        log.info("Hibernate SessionFactory initialized successfully");

        return configuration.buildSessionFactory();
    }

    private static HikariDataSource buildDataSource() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(properties.getProperty("hibernate.connection.driver_class"));
        hikariConfig.setJdbcUrl(properties.getProperty("hibernate.connection.url"));
        hikariConfig.setUsername(properties.getProperty("hibernate.connection.username"));
        hikariConfig.setPassword(properties.getProperty("hibernate.connection.password"));

        hikariConfig.setConnectionTimeout(Long.parseLong(properties.getProperty("hibernate.hikari.connectionTimeout")));
        hikariConfig.setMinimumIdle(Integer.parseInt(properties.getProperty("hibernate.hikari.minimumIdle")));
        hikariConfig.setMaximumPoolSize(Integer.parseInt(properties.getProperty("hibernate.hikari.maximumPoolSize")));
        hikariConfig.setIdleTimeout(Long.parseLong(properties.getProperty("hibernate.hikari.idleTimeout")));

        log.info("HikariDataSource initialized successfully");

        return new HikariDataSource(hikariConfig);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public static void shutdown() {
        if (!sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("Hibernate SessionFactory closed successfully");
        }

        if (!hikariDataSource.isClosed()) {
            hikariDataSource.close();
            log.info("HikariDataSource closed successfully");
        }
    }
}
