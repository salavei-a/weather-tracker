package com.asalavei.weathertracker.config;

import jakarta.servlet.ServletContext;
import org.springframework.web.WebApplicationInitializer;

/**
 * Java configuration file for initializing logging setting to prevent Logback from stopping too soon
 */
public class LoggingWebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        servletContext.setInitParameter("logbackDisableServletContainerInitializer", "true");
    }
}
