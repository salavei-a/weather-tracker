package com.asalavei.weathertracker.listener;

import com.asalavei.weathertracker.config.HibernateConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

@Slf4j
@WebListener
public class HibernateContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HibernateConfig.getSessionFactory();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();

        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();

            try {
                DriverManager.deregisterDriver(driver);
                log.info("JDBC Driver deregistered successfully: {}", driver);
            } catch (SQLException e) {
                log.error("Failed to deregister JDBC Driver: {}", driver, e);
            }
        }

        HibernateConfig.shutdown();
    }
}
