package com.asalavei.weathertracker.weather.location;

import com.asalavei.weathertracker.exception.LocationAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.asalavei.weathertracker.common.Constants.REDIRECT_HOME;

@Slf4j
@ControllerAdvice
@Order(1)
public class LocationExceptionHandler {

    @ExceptionHandler(LocationAlreadyExistsException.class)
    public String handleLocationAlreadyExistsException() {
        return REDIRECT_HOME;
    }
}
