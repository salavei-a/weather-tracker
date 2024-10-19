package com.asalavei.weathertracker.exception;

public class LocationAlreadyExistsException extends AppRuntimeException {

    public LocationAlreadyExistsException(String message) {
        super(message);
    }
}
