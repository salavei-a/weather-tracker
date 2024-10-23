package com.asalavei.weathertracker.config;

import com.asalavei.weathertracker.entity.Location;
import com.asalavei.weathertracker.entity.Session;
import com.asalavei.weathertracker.entity.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
public class PersistenceConfig {

    @Value("${hibernate.connection.driver_class}")
    private String driverClass;

    @Value("${hibernate.connection.url}")
    private String jdbcUrl;

    @Value("${hibernate.connection.username}")
    private String username;

    @Value("${hibernate.connection.password}")
    private String password;

    @Value("${hibernate.connection.provider_class}")
    private String providerClass;

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Value("${hibernate.current_session_context_class}")
    private String currentSessionContextClass;

    @Value("${hibernate.hikari.connectionTimeout}")
    private String connectionTimeout;

    @Value("${hibernate.hikari.minimumIdle}")
    private String minimumIdle;

    @Value("${hibernate.hikari.maximumPoolSize}")
    private String maximumPoolSize;

    @Value("${hibernate.hikari.idleTimeout}")
    private String idleTimeout;

    @Bean
    public SessionFactory sessionFactory() {
        Properties properties = new Properties();

        properties.setProperty("hibernate.connection.driver_class", driverClass);
        properties.setProperty("hibernate.connection.url", jdbcUrl);
        properties.setProperty("hibernate.connection.username", username);
        properties.setProperty("hibernate.connection.password", password);
        properties.setProperty("hibernate.connection.provider_class", providerClass);
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.show_sql", showSql);
        properties.setProperty("hibernate.current_session_context_class", currentSessionContextClass);

        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration.setProperties(properties);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Location.class);
        configuration.addAnnotatedClass(Session.class);

        return configuration.buildSessionFactory();
    }


    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(driverClass);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setConnectionTimeout(Long.parseLong(connectionTimeout));
        hikariConfig.setMinimumIdle(Integer.parseInt(minimumIdle));
        hikariConfig.setMaximumPoolSize(Integer.parseInt(maximumPoolSize));
        hikariConfig.setIdleTimeout(Long.parseLong(idleTimeout));

        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .load();
        flyway.migrate();
        return flyway;
    }
}
