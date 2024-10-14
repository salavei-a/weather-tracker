package com.asalavei.weathertracker.config;

import jakarta.servlet.Filter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Java configuration file that is used for web application initialization
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        HiddenHttpMethodFilter hiddenHttpMethodFilter = new HiddenHttpMethodFilter();

        DelegatingFilterProxy authenticationFilter = new DelegatingFilterProxy("authenticationFilter");
        authenticationFilter.setTargetFilterLifecycle(true);

        return new Filter[]{
                hiddenHttpMethodFilter,
                authenticationFilter
        };
    }
}
