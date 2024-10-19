package com.asalavei.weathertracker.exception;

public class AlreadyExistsException extends AppRuntimeException {

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
