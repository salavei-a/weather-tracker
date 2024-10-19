package com.asalavei.weathertracker.exception;

public class UserAlreadyExistsException extends AppRuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
