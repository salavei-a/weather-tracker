package com.asalavei.weathertracker.config;

import com.asalavei.weathertracker.entity.Location;
import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:hibernate.properties")
public class PersistenceConfig {

    private final Environment env;

    @PostConstruct
    public void runFlywayMigration() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        env.getProperty("hibernate.connection.url"),
                        env.getProperty("hibernate.connection.username"),
                        env.getProperty("hibernate.connection.password")
                )
                .load();
        flyway.migrate();
    }

    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Location.class);
        configuration.addAnnotatedClass(Session.class);

        return configuration.buildSessionFactory();
    }
}
