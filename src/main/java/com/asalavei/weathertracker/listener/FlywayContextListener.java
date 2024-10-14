package com.asalavei.weathertracker.listener;

import com.asalavei.weathertracker.config.HibernateConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

@Slf4j
@WebListener
public class FlywayContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Flyway flyway = Flyway.configure()
                .dataSource(HibernateConfig.getHikariDataSource())
                .load();

        flyway.migrate();
        log.info("Flyway migration completed successfully");
    }
}
