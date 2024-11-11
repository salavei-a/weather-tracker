package com.asalavei.weathertracker.config;

import com.asalavei.weathertracker.weather.location.Location;
import com.asalavei.weathertracker.auth.session.Session;
import com.asalavei.weathertracker.auth.user.User;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class PersistenceConfig {

    private final Environment env;

    @Value("${hibernate.connection.url}")
    private String url;

    @Value("${hibernate.connection.username}")
    private String username;

    @Value("${hibernate.connection.password}")
    private String password;

    @Bean
    public SessionFactory sessionFactory() {
        Properties properties = new Properties();

        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.username", username);
        properties.setProperty("hibernate.connection.password", password);
        properties.setProperty("hibernate.connection.driver_class", env.getRequiredProperty("hibernate.connection.driver_class"));
        properties.setProperty("hibernate.connection.provider_class", env.getRequiredProperty("hibernate.connection.provider_class"));
        properties.setProperty("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
        properties.setProperty("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
        properties.setProperty("hibernate.current_session_context_class", env.getRequiredProperty("hibernate.current_session_context_class"));

        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration.setProperties(properties);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Location.class);
        configuration.addAnnotatedClass(Session.class);

        return configuration.buildSessionFactory();
    }

    @Bean
    public Flyway flyway() {
        Flyway flyway = Flyway.configure()
                .dataSource(url, username, password)
                .load();
        flyway.migrate();
        return flyway;
    }
}